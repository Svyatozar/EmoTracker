package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;

/**
 * Фрагмент с индикаторами показателей
 */
public class FragmentIndicators extends Fragment
{
    TextView pulse;
    TextView stress;
    TextView activity;
    TextView steps;

    Messenger serviceMessenger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_indicators, container, false);

        pulse = (TextView)rootView.findViewById(R.id.pulseCounter);
        stress = (TextView)rootView.findViewById(R.id.stressCounter);
        activity = (TextView)rootView.findViewById(R.id.activityCounter);
        steps = (TextView)rootView.findViewById(R.id.stepCounter);

        return rootView;
    }

    public void setServiceMessenger(Messenger messenger) {
        this.serviceMessenger = messenger;
    }

    public Handler getHandler() {
        return new StatusHandler();
    }

    public class StatusHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BinaryUtil.COMMAND_GET_RATES:
                    DeviceInfo deviceInfo = (DeviceInfo) msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                    if (deviceInfo != null) {
                        pulse.setText(Integer.toString(deviceInfo.chss));
                        stress.setText(Integer.toString(deviceInfo.stressInd));
                        activity.setText(Integer.toString(deviceInfo.aktivnost));
                        steps.setText(Integer.toString(deviceInfo.stepsCnt));
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }
}
