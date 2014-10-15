package ru.hyperboloid.emotracker;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.hyperboloid.emotracker.fragments.FragmentEvents;
import ru.hyperboloid.emotracker.fragments.FragmentIndicators;
import ru.hyperboloid.emotracker.fragments.FragmentSettings;
import ru.hyperboloid.emotracker.fragments.NavigationDrawerFragment;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.model.Event;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;
import ru.hyperboloid.emotracker.util.SettingsProvider;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ServiceConnection btServiceConnection;
    private Messenger serviceMessenger;
    private Messenger clientMessenger;

    private ProgressDialog connectionDialog;

    private List<Handler> incomingHandlers = new ArrayList<Handler>();

    private Handler getInformationRepeater;

    private static boolean active = false;

    private FragmentEvents fragmentEvents;
    private FragmentIndicators fragmentIndicators;
    private FragmentSettings fragmentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentIndicators = new FragmentIndicators();
        fragmentSettings = new FragmentSettings();

        getInformationRepeater = new Handler(informationRepeatCallback);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        active = true;
    }

    public void onPause()
    {
        super.onPause();
        active = false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        FragmentManager fragmentManager = getFragmentManager();

        switch (position)
        {
            case 0:
                mTitle = getString(R.string.title_section1);

                if (null == fragmentEvents)
                    fragmentEvents = new FragmentEvents();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentEvents)
                        .commit();
                break;
            case 1:
                mTitle = getString(R.string.title_section2);

                if (null == fragmentIndicators)
                    fragmentIndicators = new FragmentIndicators();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentIndicators)
                        .commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section3);

                if (null == fragmentSettings)
                    fragmentSettings = new FragmentSettings();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragmentSettings)
                        .commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_power_on)
        {
            clientMessenger = new Messenger(new BluetoothHandler());

            btServiceConnection = new BluetoothServiceConnection();

            connectionDialog = new ProgressDialog(this);
            connectionDialog.setMessage(getString(R.string.bluetooth_connecting_message));
            connectionDialog.show();
            bindService(new Intent(this, BluetoothService.class), btServiceConnection, Context.BIND_AUTO_CREATE);
            return true;
        }

        if (id == R.id.action_power_off)
        {
            getInformationRepeater.removeMessages(0);
            incomingHandlers.clear();

            if (null != btServiceConnection)
            {
                unbindService(btServiceConnection);
                stopService(new Intent(MainActivity.this, BluetoothService.class));
                btServiceConnection = null;
            }

            System.exit(0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class BluetoothServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.i("LOG", "Service connected");
            serviceMessenger = new Messenger(service);

            fragmentEvents.setDeviceState(true);

            fragmentIndicators.setServiceMessenger(serviceMessenger);

            incomingHandlers.clear();
            incomingHandlers.add(fragmentIndicators.getHandler());
            incomingHandlers.add(fragmentEvents.getHandler());

            Message msg = Message.obtain(null, BluetoothService.MESSAGE_REGISTER, 0, 0);
            msg.replyTo = clientMessenger;

            BinaryUtil.send(serviceMessenger, msg);

            startService(new Intent(MainActivity.this, BluetoothService.class));
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            fragmentEvents.setDeviceState(true);
            serviceMessenger = null;
        }
    }

    class BluetoothHandler extends Handler
    {
        public BluetoothHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BluetoothService.MESSAGE_BLUETOOTH_CONNECTED:
                    connectionDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Bluetooth connection established", Toast.LENGTH_SHORT).show();
                    getInformationRepeater.sendEmptyMessageDelayed(0, 5000);
                    break;
                case BluetoothService.MESSAGE_BLUETOOTH_CONNECT_FAILED:
                    connectionDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Bluetooth connection failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (msg.getData() != null && msg.getData().containsKey(BluetoothService.RESPONSE_DATA)) {
                        Serializable response = msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                        Log.i("LOG", response.toString());
                    }
                    super.handleMessage(msg);
            }

            sendEvent(msg);

            for (Handler handler : incomingHandlers)
            {
                handler.handleMessage(msg);
            }
        }
    }

    private Handler.Callback informationRepeatCallback = new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message message)
        {
            if (serviceMessenger != null)
            {
                Message msg = Message.obtain(null, BinaryUtil.COMMAND_GET_RATES);
                BinaryUtil.send(serviceMessenger, msg);
            }

            if (null != getInformationRepeater)
            {
                getInformationRepeater.sendEmptyMessageDelayed(0, 5000);
            }

            return true;
        }
    };

    private void sendEvent(Message message)
    {
        int pulse;
        int stress;
        int activity;
        int steps;

        DeviceInfo deviceInfo = (DeviceInfo) message.getData().getSerializable(BluetoothService.RESPONSE_DATA);
        if (deviceInfo != null)
        {
            pulse = deviceInfo.chss;
            stress = deviceInfo.stressInd;
            activity = deviceInfo.aktivnost;
            steps = deviceInfo.stepsCnt;
        }
        else
        {
            return;
        }

        String info = "Пульс: " + pulse + " Стресс: " + stress + " Активность: " + activity + " Шаги: " + steps;

        boolean isPulseAttention = ApplicationWrapper.getSettingsProvider().getSwitcherState(SettingsProvider.PULSE_SWITCHER_STATE);
        boolean isStressAttention = ApplicationWrapper.getSettingsProvider().getSwitcherState(SettingsProvider.STRESS_SWITCHER_STATE);
        boolean isStepsAttention = ApplicationWrapper.getSettingsProvider().getSwitcherState(SettingsProvider.STEP_SWITCHER_STATE);
        boolean isActivityAttention = ApplicationWrapper.getSettingsProvider().getSwitcherState(SettingsProvider.ACTIVITY_SWITCHER_STATE);

        if (isPulseAttention)
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.PULSE_ATTENTION_VALUE);

            if (pulse > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - ПУЛЬС\nПОРОГ-ВНИМАНИЕ", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - ПУЛЬС\nПОРОГ-ВНИМАНИЕ", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - ПУЛЬС\nПОРОГ-ВНИМАНИЕ");
            }
        }
        else
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.PULSE_ALERT_VALUE);

            if (pulse > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - ПУЛЬС\nПОРОГ-ТРЕВОГА", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - ПУЛЬС\nПОРОГ-ТРЕВОГА", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - ПУЛЬС\nПОРОГ-ТРЕВОГА");
            }
        }

        if (isStressAttention)
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.STRESS_ATTENTION_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - СТРЕСС\nПОРОГ-ВНИМАНИЕ", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - СТРЕСС\nПОРОГ-ВНИМАНИЕ", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - СТРЕСС\nПОРОГ-ВНИМАНИЕ");
            }
        }
        else
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.STRESS_ALERT_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - СТРЕСС\nПОРОГ-ТРЕВОГА", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - СТРЕСС\nПОРОГ-ТРЕВОГА", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - СТРЕСС\nПОРОГ-ТРЕВОГА");
            }
        }

        if (isStepsAttention)
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.STEP_ATTENTION_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - ШАГИ\nПОРОГ-ВНИМАНИЕ", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - ШАГИ\nПОРОГ-ВНИМАНИЕ", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - ШАГИ\nПОРОГ-ВНИМАНИЕ");
            }
        }
        else
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.STEP_NORMAL_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА", info, 1);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА");
            }
        }

        if (isActivityAttention)
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.ACTIVITY_ATTENTION_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - АКТИВНОСТЬ\nПОРОГ-ВНИМАНИЕ", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - АКТИВНОСТЬ\nПОРОГ-ВНИМАНИЕ", info, 0);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - АКТИВНОСТЬ\nПОРОГ-ВНИМАНИЕ");
            }
        }
        else
        {
            int threshold = ApplicationWrapper.getSettingsProvider().getThresholdValue(SettingsProvider.ACTIVITY_NORMAL_VALUE);

            if (stress > threshold)
            {
                ApplicationWrapper.getNetworkUtil().addEvent("ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА", pulse, stress, activity, steps);
                Event event = new Event(android.R.drawable.ic_dialog_info, "ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА", info, 0);
                ApplicationWrapper.getDataBaseWrapper().writeEvent(event);
                sendNotification("ИСТОЧНИК - ШАГИ\nПОРОГ-НОРМА");
            }
        }
    }

    private void sendNotification(String message)
    {
        if (!active)
        {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
                            .setContentTitle("EmoTracker")
                            .setContentText(message);

            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            stackBuilder.addParentStack(MainActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(777, mBuilder.build());
        }
    }
}
