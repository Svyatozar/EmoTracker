package ru.hyperboloid.emotracker.task;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import ru.hyperboloid.emotracker.model.DeviceInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by olshanikov on 9/22/14.
 */
public class TestBTSocketTask extends AsyncTask<Void, Void, Void> {

    final Activity context;
    final BluetoothSocket socket;

    public TestBTSocketTask(Activity context, BluetoothSocket socket) {
        this.context = context;
        this.socket = socket;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("EmoTrack", "OnCancelled");
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            socket.connect();
            Log.i("EmoTrack", "Socket connected " + socket.isConnected());
        } catch (Exception e) {
            Log.e("EmoTrack", "Error connecting to bluetooth socket", e);
            log("Error connecting to bluetooth socket");
            closeSocket();
            return null;
        }

        Log.i("EmoTrack", "Seems that connection is ok");
        log("Seems that connection is ok");

        InputStream is;
        OutputStream os;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            Thread.sleep(100);
        } catch (Exception e) {
            Log.e("EmoTrack", "Error getting input and output streams", e);
            log("Error getting input and output streams");
            closeSocket();
            return null;
        }

        byte[] dataToSend = new byte[]{(byte) 0x80, 0x01, 0x01, (byte) 0x82};
        Log.i("EmoTrack", "Write to bluetooth socket");
        log("Write to bluetooth socket");
        try {
            os.write(dataToSend, 0, dataToSend.length);
            os.flush();

            Log.i("EmoTrack", "Data to socket wrote");
            log("Data to socket wrote");

            Thread.sleep(100);
        } catch (Exception e) {
            Log.e("TestEmoTrack", "Error writing to socket", e);
            log("Error writing to socket");
        }

        Log.i("EmoTrack", "Reading from socket");
        log("Reading from socket");
        try {
            byte[] input = read(is);
            for (byte b : input) {
                Log.i("EmoTrack", "Input data " + b);
                log("Input data " + b);
            }

            Log.i("EmoTrack", "Bytes count " + input.length);
            log("Bytes count " + input.length);

            DeviceInfo deviceInfo = DeviceInfo.parse(input);
            log(deviceInfo.toString());
        } catch (Exception e) {
            Log.i("EmoTrack", "Error reading from socket", e);
            log("Error reading from socket");
        }
        closeSocket();

        return null;
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
            Log.i("EmoTrack", "Available");
            baos.write(is.read());
            Thread.sleep(10);
        }

        return baos.toByteArray();

    }

    private void closeSocket() {
        if (socket != null) {
            try {
                Log.i("EmoTrack", "Closing socket");
                socket.close();
            } catch (Exception e) {
                Log.e("EmoTrack", "Error closing socket", e);
            }
        }
    }

    private void log(final String log) {
//        context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView logView = (TextView) context.findViewById(R.id.log_view);
//                logView.append(log);
//                logView.append("\n");
//            }
//        });
    }
}
