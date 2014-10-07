package ru.hyperboloid.emotracker.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Set;
import java.util.UUID;

/**
 * Created by olshanikov on 9/25/14.
 */
public class GetBluetoothConnectionTask extends AsyncTask<Void, Void, BluetoothSocket> {

    private static final String EMOTRACK_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    private String address;
    private BluetoothAdapter adapter;
    private final Callback callback;

    public GetBluetoothConnectionTask(BluetoothAdapter adapter, String address, Callback callback) {
        this.callback = callback;
        this.adapter = adapter;
        this.address = address;
    }

    @Override
    protected BluetoothSocket doInBackground(Void... params) {
        BluetoothSocket socket = null;
        if (address == null) {
            address = getCachedAddress(adapter);
            if (address == null) {
                Log.e("LOG", "Null mac address intent"); // TODO
                cancel(true);
            }
        }

        if (!isCancelled()) {
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            if (device == null) {
                Log.e("LOG", "Null device for mac address " + address);
                cancel(true);
            }
            try
            {
//                Method m = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
//                socket = (BluetoothSocket)m.invoke(device, Integer.valueOf(1));

                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(EMOTRACK_UUID));
                socket.connect();
            } catch (Exception e)
            {
                Log.e("LOG", "Error creating socket", e);
                cancel(true);
            }
        }
        return socket;
    }

    private String getCachedAddress(BluetoothAdapter btAdapter) {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices)
            {
                if (device.getName().equals("TestEmoTrac"))
                {
                    Log.i("LOG", "device founded: " + device.getName());

                    Log.i("LOG", "Found target device");
                    return device.getAddress();
                }
                Log.i("LOG", "Found device " + device.getName() + " address " + device.getAddress());
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        if (callback != null) {
            callback.onCompleted(bluetoothSocket);
        }
    }

    @Override
    protected void onCancelled(BluetoothSocket bluetoothSocket) {
        if (callback != null) {
            callback.onError();
        }
    }

    @Override
    protected void onCancelled() {
        if (callback != null) {
            callback.onError();
        }
    }

    public static interface Callback {

        public void onCompleted(BluetoothSocket socket);

        public void onError();
    }
}
