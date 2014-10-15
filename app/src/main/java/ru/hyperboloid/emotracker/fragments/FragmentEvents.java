package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.adapters.EventsAdapter;

/**
 * Фрагмент для настройки и отображения событий
 */
public class FragmentEvents extends Fragment
{
    private ListView eventsList;
    private EventsAdapter eventsAdapter;

    private Button startButton;
    private TextView timeCounter;

    private TextView deviceState;

    private boolean connectionStateFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        eventsList = (ListView)rootView.findViewById(R.id.eventsListView);
        eventsAdapter = new EventsAdapter();

        eventsAdapter.loadData(ApplicationWrapper.getDataBaseWrapper().readEvents());

        eventsList.setAdapter(eventsAdapter);

        startButton = (Button)rootView.findViewById(R.id.startButton);
        timeCounter = (TextView)rootView.findViewById(R.id.timeCounter);

        deviceState = (TextView)rootView.findViewById(R.id.deviceState);

        setDeviceState(connectionStateFlag);

        return rootView;
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