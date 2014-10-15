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

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hyperboloid.emotracker.MainActivity;
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

    LinearLayout pulseLayout;
    LinearLayout stressLayout;
    LinearLayout activityLayout;

    Messenger serviceMessenger;

    XYPlot pulsePlot;
    XYPlot stressPlot;
    XYPlot activityPlot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_indicators, container, false);

        pulse = (TextView)rootView.findViewById(R.id.pulseCounter);
        stress = (TextView)rootView.findViewById(R.id.stressCounter);
        activity = (TextView)rootView.findViewById(R.id.activityCounter);
        steps = (TextView)rootView.findViewById(R.id.stepCounter);

        pulseLayout = (LinearLayout)rootView.findViewById(R.id.pulseLayout);
        stressLayout = (LinearLayout)rootView.findViewById(R.id.stressLayout);
        activityLayout = (LinearLayout)rootView.findViewById(R.id.activityLayout);

        // initialize our XYPlot reference:
        pulsePlot = (XYPlot) rootView.findViewById(R.id.pulsePlot);
        stressPlot = (XYPlot) rootView.findViewById(R.id.stressPlot);
        activityPlot = (XYPlot) rootView.findViewById(R.id.activityPlot);

        return rootView;
    }

    public void setServiceMessenger(Messenger messenger) {
        this.serviceMessenger = messenger;
    }

    public Handler getHandler() {
        return new StatusHandler();
    }

    public class StatusHandler extends Handler {
        int counter = 1;

        List<Number> arrayPulse = new ArrayList<Number>();
        List<Number> arrayStress = new ArrayList<Number>();
        List<Number> arrayActivity = new ArrayList<Number>();

        @Override
        public void handleMessage(Message msg) {

            try
            {
                switch (msg.what) {
                    case BinaryUtil.COMMAND_GET_RATES:
                        DeviceInfo deviceInfo = (DeviceInfo) msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
                        if (deviceInfo != null)
                        {
                            pulse.setText(Integer.toString(deviceInfo.chss));
                            stress.setText(Integer.toString(deviceInfo.stressInd));
                            activity.setText(Integer.toString(deviceInfo.aktivnost));
                            steps.setText(Integer.toString(deviceInfo.stepsCnt));

                            arrayPulse.add(deviceInfo.chss);
                            arrayStress.add(deviceInfo.stressInd);
                            arrayActivity.add(deviceInfo.aktivnost);

                            XYSeries seriesPulse = new SimpleXYSeries(arrayPulse, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
                            XYSeries seriesStress = new SimpleXYSeries(arrayStress, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
                            XYSeries seriesActivity = new SimpleXYSeries(arrayActivity, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");

                            pulsePlot.clear();
                            stressPlot.clear();
                            activityPlot.clear();

                            // same as above:
                            LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
                            seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
                            seriesFormat.configure(getActivity().getApplicationContext(),
                                    R.xml.line_point_formatter_with_plf2);

                            pulsePlot.addSeries(seriesPulse, seriesFormat);
                            stressPlot.addSeries(seriesStress, seriesFormat);
                            activityPlot.addSeries(seriesActivity, seriesFormat);

                            pulsePlot.redraw();
                            stressPlot.redraw();
                            activityPlot.redraw();

                            counter++;
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
