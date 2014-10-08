package ru.hyperboloid.emotracker.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.command.AbstractCommand;
import ru.hyperboloid.emotracker.command.CancelPeriodicCommand;
import ru.hyperboloid.emotracker.command.GetStatusCommand;
import ru.hyperboloid.emotracker.command.PorogShiftAckCommand;
import ru.hyperboloid.emotracker.command.SetPorogValuesCommand;
import ru.hyperboloid.emotracker.task.GetBluetoothConnectionTask;
import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by olshanikov on 9/22/14.
 */
public class BluetoothService extends Service {

    public static final String MAC_ADDRESS = "extra.mac_address";

    public static final String RESPONSE_DATA = "response_data";
    public static final String REQUEST_DATA = "request_data";

    public static final int MESSAGE_REGISTER = 2000;

    public static final int MESSAGE_BLUETOOTH_CONNECT_FAILED = 1000;
    public static final int MESSAGE_BLUETOOTH_CONNECTED = 1001;

    BluetoothSocket socket;
    Messenger serviceMessenger = new Messenger(new IncomingHandler());
    Messenger clientMessenger;
    Messenger localHandler = new Messenger(new BluetoothHandler());

    ListenerThread listenerThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("LOG", "ON_START_COMMAND");

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), R.string.no_bluetooth_message, Toast.LENGTH_SHORT).show();
        } else {
            if (socket == null && intent != null) {
                String address = intent.getStringExtra(MAC_ADDRESS);
                new GetBluetoothConnectionTask(btAdapter, address, new BluetoothSocketConnectedCallback()).execute();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("LOG", "ON_BIND");
        return serviceMessenger.getBinder();
    }

    @Override
    public void onDestroy()
    {
        Log.i("LOG", "service done");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public synchronized void writeCommand(AbstractCommand command) {
        try {
            byte[] data = command.serialize();
            Log.i("LOG", "Writing data " + BinaryUtil.bytesToHex(data));

            OutputStream os = socket.getOutputStream();
            os.write(data);
            os.flush();
        } catch (Exception e) {
            Log.e("LOG", "Error writing command to socket", e);
        }
    }

    class BluetoothHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BinaryUtil.COMMAND_POROG_SHIFT:
                    writeCommand(new PorogShiftAckCommand());
                    break;
            }
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == MESSAGE_REGISTER)
            {
                clientMessenger = msg.replyTo;
            }
            else if (socket != null && socket.isConnected()) {
                switch (msg.what) {
                    case BinaryUtil.COMMAND_GET_RATES:
                        writeCommand(new GetStatusCommand());
                        break;
                    case BinaryUtil.COMMAND_SET_POROG:
                        SetPorogValuesCommand command = (SetPorogValuesCommand) msg.getData().getSerializable(REQUEST_DATA);

                        writeCommand(command);
                        break;
                    case BinaryUtil.COMMAND_CANCEL_PERIODIC:
                        writeCommand(new CancelPeriodicCommand());
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    public class BluetoothSocketConnectedCallback implements GetBluetoothConnectionTask.Callback {

        @Override
        public void onCompleted(BluetoothSocket socket) {
            BluetoothService.this.socket = socket;

            listenerThread = new ListenerThread("ListenerThread");
            listenerThread.start();

            Message msg = Message.obtain(null, MESSAGE_BLUETOOTH_CONNECTED);
            BinaryUtil.send(clientMessenger, msg);
        }

        @Override
        public void onError() {
            Message msg = Message.obtain(null, MESSAGE_BLUETOOTH_CONNECT_FAILED);
            BinaryUtil.send(clientMessenger, msg);
        }
    }

    public class ListenerThread extends HandlerThread {

        public ListenerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    InputStream is = socket.getInputStream();

                    byte[] data = read(is);

                    if (data.length >= 4) {
                        Log.i("LOG", "Total byte count " + data.length);
                        Serializable response = BinaryUtil.parseResponse(data);

                        if (response != null) {
                            Message msg = Message.obtain(null, data[2], 0, 0);
                            if (clientMessenger != null) {
                                Bundle b = new Bundle();
                                b.putSerializable(RESPONSE_DATA, BinaryUtil.parseResponse(data));
                                msg.setData(b);

                                try {
                                    clientMessenger.send(msg);
                                } catch (Exception e) {
                                    Log.e("LOG", "Error sending message to clientMessenger", e);
                                }
                            }

                            msg = Message.obtain(null, data[2], 0, 0);
                            localHandler.send(msg);
                        }
                    } else if (data.length > 0) {
                        Log.e("LOG", "Got " + data.length + " bytes response");
                    }

                    sleep(10);
                } catch (Exception e) {
                    Log.e("LOG", "Error reading from socket", e);
                    return;
                }
            }
        }

        public byte[] read(InputStream is) throws Exception {

            int threshold = 0;
            while (is.available() == 0 && threshold < 3000) {
                Thread.sleep(1);
                threshold++;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.reset();

            while (is.available() > 0) {
                baos.write(is.read());
                Thread.sleep(100);
            }

            return baos.toByteArray();

        }
    }
}
