package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.adapters.EventsAdapter;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;

/**
 * Фрагмент для настройки и отображения событий
 */
public class FragmentEvents extends Fragment
{
    private ListView eventsList;
    private EventsAdapter eventsAdapter;

    private Button startButton;
    private Chronometer chronometer;

    private TextView deviceState;

    private boolean connectionStateFlag = false;

    private List<int[]> eventData = new ArrayList<int[]>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        eventsList = (ListView)rootView.findViewById(R.id.eventsListView);
        eventsAdapter = new EventsAdapter();

        eventsAdapter.loadData(ApplicationWrapper.getDataBaseWrapper().readEvents());

        eventsList.setAdapter(eventsAdapter);

        chronometer = (Chronometer)rootView.findViewById(R.id.chronometer);
        startButton = (Button)rootView.findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (startButton.getText().equals(getString(R.string.start)))
                {
                    if (connectionStateFlag)
                    {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        startButton.setText(getString(R.string.stop));
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Подключите устройство!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    chronometer.stop();
                    startButton.setText(getString(R.string.start));
                }
            }
        });

        deviceState = (TextView)rootView.findViewById(R.id.deviceState);

        setDeviceState(connectionStateFlag);

        return rootView;
    }

    public Handler getHandler() {
        return new StatusHandler();
    }

    public class StatusHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                switch (msg.what)
                {
                    case BinaryUtil.COMMAND_GET_RATES:
                        DeviceInfo deviceInfo = (DeviceInfo) msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                        if (deviceInfo != null)
                        {
                            eventData.add(new int[]{deviceInfo.chss, deviceInfo.stressInd, deviceInfo.aktivnost});
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            } catch (NullPointerException e)
            {
                Log.e("LOG", "NULL IN HANDLER");
            }
        }
    }


    /**
     * Установить статус устройства
     * @param state true - подключено, false - нет
     */
    public void setDeviceState(boolean state)
    {
        connectionStateFlag = state;

        if (state)
        {
            deviceState.setText(getString(R.string.device_is_ok));
        }
        else
        {
            deviceState.setText(getString(R.string.device_is_bad));
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }
}