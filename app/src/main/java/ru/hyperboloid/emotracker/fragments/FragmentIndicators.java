package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.Random;

import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;
import ru.hyperboloid.emotracker.util.ViewHelper;

import static android.view.View.VISIBLE;
import static com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * Фрагмент с индикаторами показателей
 */
public class FragmentIndicators extends Fragment implements GraphView.OnTouchEventInterceptor
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

    private View pulseInfo;
    private View stressInfo;
    private View activityInfo;

    private String pulseText;
    private String stressText;
    private String activityText;
    private String stepText;

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

        pulseInfo = rootView.findViewById(R.id.pulseInfo);
        stressInfo = rootView.findViewById(R.id.stressInfo);
        activityInfo = rootView.findViewById(R.id.activityInfo);

        pulseLayout = (LinearLayout)rootView.findViewById(R.id.pulseLayout);
        stressLayout = (LinearLayout)rootView.findViewById(R.id.stressLayout);
        activityLayout = (LinearLayout)rootView.findViewById(R.id.activityLayout);

        if (null == pulseGraphView)
        {
            pulseGraphView = new LineGraphView(getActivity(),"", this);
            stressGraphView = new LineGraphView(getActivity(),"", this);
            activityGraphView = new LineGraphView(getActivity(),"", this);

            pulseGraphView.setId(ViewHelper.generateViewId());
            stressGraphView.setId(ViewHelper.generateViewId());
            activityGraphView.setId(ViewHelper.generateViewId());

            tuneGraphView(pulseGraphView);
            tuneGraphView(stressGraphView);
            tuneGraphView(activityGraphView);

            pulseGraphView.addSeries(pulseSeries);
            stressGraphView.addSeries(stressSeries);
            activityGraphView.addSeries(activitySeries);

            pulseLayout.addView(pulseGraphView);
            stressLayout.addView(stressGraphView);
            activityLayout.addView(activityGraphView);
        }
        else
        {
            pulseSplash.setVisibility(View.GONE);
            stressSplash.setVisibility(View.GONE);
            activitySplash.setVisibility(View.GONE);

            pulseLayout.addView(pulseGraphView);
            stressLayout.addView(stressGraphView);
            activityLayout.addView(activityGraphView);
        }

        if (null != pulseText)
        {
            pulse.setText(pulseText);
            stress.setText(stressText);
            activity.setText(activityText);
            steps.setText(stepText);
        }

//        if (0 == arrayPulse.size())
//        {
//            arrayPulse.add(5);
//
//            for (int i = 0; i < 1000; i++)
//            {
//                if (VISIBLE == pulseSplash.getVisibility())
//                {
//                    pulseSplash.setVisibility(View.GONE);
//                    stressSplash.setVisibility(View.GONE);
//                    activitySplash.setVisibility(View.GONE);
//                }
//
//                pulseText = Integer.toString(i);
//                stressText = Integer.toString(i);
//                activityText = Integer.toString(i);
//                stepText = Integer.toString(i);
//
//                pulse.setText(pulseText);
//                stress.setText(stressText);
//                activity.setText(activityText);
//                steps.setText(stepText);
//
//                Random rand = new Random();
//
//                pulseSeries.appendData(new GraphViewData(globalStepCounter,(double)rand.nextInt(500)), true);
//                stressSeries.appendData(new GraphViewData(globalStepCounter,(double)rand.nextInt(500)), true);
//                activitySeries.appendData(new GraphViewData(globalStepCounter,(double)rand.nextInt(500)), true);
//
//                globalStepCounter++;
//            }
//        }

        return rootView;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        pulseLayout.removeAllViews();
        stressLayout.removeAllViews();
        activityLayout.removeAllViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (0 == pulseLayout.getChildCount())
        {
            pulseLayout.addView(pulseInfo);
            stressLayout.addView(stressInfo);
            activityLayout.addView(activityInfo);

            pulseLayout.addView(pulseGraphView);
            stressLayout.addView(stressGraphView);
            activityLayout.addView(activityGraphView);

            pulseLayout.addView(pulseSplash);
            stressLayout.addView(stressSplash);
            activityLayout.addView(activitySplash);
        }
    }

    private void tuneGraphView(GraphView graphView)
    {
        graphView.setScalable(true);
        graphView.setScrollable(true);

        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.NONE);

        graphView.setViewPort(0, 8);
    }

    public void setServiceMessenger(Messenger messenger) {
        this.serviceMessenger = messenger;
    }

    public Handler getHandler() {
        return new StatusHandler();
    }

    @Override
    public void onTouchEvent(View view, MotionEvent event)
    {
        Log.i("LOG", "TOUCH EVENT: " + event);

        if (view != pulseGraphView)
        {
            pulseGraphView.invokeTouchEvent(event);
        }

        if (view != stressGraphView)
        {
            stressGraphView.invokeTouchEvent(event);
        }

        if (view != activityGraphView)
        {
            activityGraphView.invokeTouchEvent(event);
        }

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
                                    if (arrayPulse.size() > 1)
                                    {
                                        pulseSplash.setVisibility(View.GONE);
                                        stressSplash.setVisibility(View.GONE);
                                        activitySplash.setVisibility(View.GONE);
                                    }
                                }

                                pulseText = Integer.toString(deviceInfo.chss);
                                stressText = Integer.toString(deviceInfo.stressInd);
                                activityText = Integer.toString(deviceInfo.aktivnost);
                                stepText = Integer.toString(deviceInfo.stepsCnt);

                                pulse.setText(pulseText);
                                stress.setText(stressText);
                                activity.setText(activityText);
                                steps.setText(stepText);

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

