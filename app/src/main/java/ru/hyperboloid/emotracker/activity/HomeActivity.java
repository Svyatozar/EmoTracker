package ru.hyperboloid.emotracker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.hyperboloid.emotracker.R;
//import ru.hyperboloid.emotracker.StatusFragment;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {

    ServiceConnection btServiceConnection;
    Messenger serviceMessenger;
    Messenger clientMessenger;

    ProgressDialog connectionDialog;

    List<Handler> incomingHandlers = new ArrayList<Handler>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientMessenger = new Messenger(new BluetoothHandler());

        btServiceConnection = new BluetoothServiceConnection();

        connectionDialog = new ProgressDialog(this);
        connectionDialog.setMessage(getString(R.string.bluetooth_connecting_message));
        connectionDialog.show();
        bindService(new Intent(this, BluetoothService.class), btServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class BluetoothServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("EmoTracker", "Service connected");
            serviceMessenger = new Messenger(service);

//            StatusFragment statusFragment = new StatusFragment();
//            statusFragment.setServiceMessenger(serviceMessenger);
//            incomingHandlers.add(statusFragment.getHandler());

//            getFragmentManager().beginTransaction()
//                    .replace(R.id.action_example, statusFragment)
//                    .commit();




//            httppost.setHeader("Accept", "application/json");
//            httppost.setHeader("Content-Type", "application/json");


            //        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
//        {
//            @Override
//            protected Void doInBackground(Void... voids)
//            {
//                // Create a new HttpClient and Post Header
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httppost = new HttpPost(SERVER_NAME + CREATE_TOKEN);
//
//                try
//                {
//                    httppost.setEntity(new StringEntity("{}"));
//                    httppost.setHeader("Accept", "application/json");
//                    httppost.setHeader("Content-Type", "application/json");
//
//                    // Execute HTTP Post Request
//                    HttpResponse response = httpclient.execute(httppost);
//
//                    HttpEntity res = new BufferedHttpEntity(response.getEntity());
//
//                    InputStream inputStream = res.getContent();
//
//                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder total = new StringBuilder();
//                    String line;
//                    while ((line = r.readLine()) != null) {
//                        total.append(line);
//                    }
//
//                    Log.i("LOG", "ANSWER: " + total.toString());
//
//                } catch (ClientProtocolException e) {
//                    // TODO Auto-generated catch block
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                }
//
//                return null;
//            }
//        }.execute();

            Message msg = Message.obtain(null, BluetoothService.MESSAGE_REGISTER, 0, 0);
            msg.replyTo = clientMessenger;

            BinaryUtil.send(serviceMessenger, msg);

            startService(new Intent(HomeActivity.this, BluetoothService.class));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
        }
    }

    class BluetoothHandler extends Handler {

        public BluetoothHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_BLUETOOTH_CONNECTED:
                    connectionDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Bluetooth connection established", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_BLUETOOTH_CONNECT_FAILED:
                    connectionDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Bluetooth connection failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (msg.getData() != null && msg.getData().containsKey(BluetoothService.RESPONSE_DATA)) {
                        Serializable response = msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                        Log.i("LOG", response.toString());
                    }
                    super.handleMessage(msg);
            }

            for (Handler handler : incomingHandlers) {
                handler.handleMessage(msg);
            }
        }
    }
}
