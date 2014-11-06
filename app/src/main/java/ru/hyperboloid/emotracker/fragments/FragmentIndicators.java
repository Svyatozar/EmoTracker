package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.List;

import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;

import static android.view.View.VISIBLE;
import static com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * Фрагмент с индикаторами показателей
 */
public class FragmentIndicators extends Fragment
{
    TextView pulse;
    TextView stress;
    TextView activity;
    TextView steps;

    LinearLayout pulseLayout;
    LinearLayout stressLayout;
    LinearLayout activityLayout;

    GraphView pulseGraphView;
    GraphView stressGraphView;
    GraphView activityGraphView;

    GraphViewSeries pulseSeries = new GraphViewSeries(new GraphViewData[] {});
    GraphViewSeries stressSeries = new GraphViewSeries(new GraphViewData[] {});
    GraphViewSeries activitySeries = new GraphViewSeries(new GraphViewData[] {});

    Messenger serviceMessenger;

    List<Number> arrayPulse = new ArrayList<Number>();
    List<Number> arrayStress = new ArrayList<Number>();
    List<Number> arrayActivity = new ArrayList<Number>();

    private int globalStepCounter = 0;

    private View pulseSplash;
    private View stressSplash;
    private View activitySplash;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_indicators, container, false);

        pulseSplash = rootView.findViewById(R.id.pulseSplash);
        stressSplash = rootView.findViewById(R.id.stressSplash);
        activitySplash = rootView.findViewById(R.id.activitySplash);

        pulse = (TextView)rootView.findViewById(R.id.pulseCounter);
        stress = (TextView)rootView.findViewById(R.id.stressCounter);
        activity = (TextView)rootView.findViewById(R.id.activityCounter);
        steps = (TextView)rootView.findViewById(R.id.stepCounter);

        pulseLayout = (LinearLayout)rootView.findViewById(R.id.pulseLayout);
        stressLayout = (LinearLayout)rootView.findViewById(R.id.stressLayout);
        activityLayout = (LinearLayout)rootView.findViewById(R.id.activityLayout);

        pulseGraphView = new LineGraphView(getActivity(),"");
        stressGraphView = new LineGraphView(getActivity(),"");
        activityGraphView = new LineGraphView(getActivity(),"");

        tuneGraphView(pulseGraphView);
        tuneGraphView(stressGraphView);
        tuneGraphView(activityGraphView);

        pulseGraphView.addSeries(pulseSeries);
        stressGraphView.addSeries(stressSeries);
        activityGraphView.addSeries(activitySeries);

        pulseLayout.addView(pulseGraphView);
        stressLayout.addView(stressGraphView);
        activityLayout.addView(activityGraphView);

        if (arrayPulse.size() > 0)
        {
            pulseSplash.setVisibility(View.GONE);
            stressSplash.setVisibility(View.GONE);
            activitySplash.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void tuneGraphView(GraphView graphView)
    {
        graphView.setScalable(true);
        graphView.setScrollable(true);

        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.NONE);
        graphView.getGraphViewStyle().setTextSize(0);

        graphView.setViewPort(0, 8);
    }

    public void setServiceMessenger(Messenger messenger) {
        this.serviceMessenger = messenger;
    }

    public Handler getHandler() {
        return new StatusHandler();
    }

    public class StatusHandler extends Handler {

        @Override
        public void handleMessage(Message msg)
        {
            try
            {
                switch (msg.what) {
                    case BinaryUtil.COMMAND_GET_RATES:
                        DeviceInfo deviceInfo = (DeviceInfo) msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                        if (deviceInfo != null)
                        {
                            arrayPulse.add(deviceInfo.chss);
                            arrayStress.add(deviceInfo.stressInd);
                            arrayActivity.add(deviceInfo.aktivnost);

                            if (null != pulse)
                            {
                                if (VISIBLE == pulseSplash.getVisibility())
                                {
                                    pulseSplash.setVisibility(View.GONE);
                                    stressSplash.setVisibility(View.GONE);
                                    activitySplash.setVisibility(View.GONE);
                                }

                                pulse.setText(Integer.toString(deviceInfo.chss));
                                stress.setText(Integer.toString(deviceInfo.stressInd));
                                activity.setText(Integer.toString(deviceInfo.aktivnost));
                                steps.setText(Integer.toString(deviceInfo.stepsCnt));

                                pulseSeries.appendData(new GraphViewData(globalStepCounter,(double)deviceInfo.chss), true);
                                stressSeries.appendData(new GraphViewData(globalStepCounter,(double)deviceInfo.stressInd), true);
                                activitySeries.appendData(new GraphViewData(globalStepCounter,(double)deviceInfo.aktivnost), true);

                                globalStepCounter++;
                            }
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
            catch (NullPointerException e)
            {
                Log.e("LOG", "NULL IN HANDLER");
            }
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }
}

