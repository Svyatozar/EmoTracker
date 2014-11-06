package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import java.util.ArrayList;
import java.util.List;

import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.service.BluetoothService;
import ru.hyperboloid.emotracker.util.BinaryUtil;

/**
 * Фрагмент с индикаторами показателей
 */
public class FragmentIndicators extends Fragment implements View.OnTouchListener
{
    TextView pulse;
    TextView stress;
    TextView activity;
    TextView steps;

    private PointF minXY;
    private PointF maxXY;

    LinearLayout pulseLayout;
    LinearLayout stressLayout;
    LinearLayout activityLayout;

    Messenger serviceMessenger;

    XYPlot pulsePlot;
    XYPlot stressPlot;
    XYPlot activityPlot;

    private SimpleXYSeries[] series = null;

    SimpleXYSeries seriesPulse;
    SimpleXYSeries seriesStress;
    SimpleXYSeries seriesActivity;

    List<Number> arrayPulse = new ArrayList<Number>();
    List<Number> arrayStress = new ArrayList<Number>();
    List<Number> arrayActivity = new ArrayList<Number>();

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        seriesPulse = new SimpleXYSeries(arrayPulse, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        seriesStress = new SimpleXYSeries(arrayStress, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        seriesActivity = new SimpleXYSeries(arrayActivity, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");

        seriesPulse.addFirst(null, 0);
        seriesStress.addFirst(null, 0);
        seriesActivity.addFirst(null, 0);
    }

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

        pulsePlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        stressPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        activityPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

        pulsePlot.setOnTouchListener(this);
        stressPlot.setOnTouchListener(this);
        activityPlot.setOnTouchListener(this);

        pulsePlot.calculateMinMaxVals();
        minXY = new PointF(pulsePlot.getCalculatedMinX().floatValue(),
                pulsePlot.getCalculatedMinY().floatValue());
        maxXY = new PointF(pulsePlot.getCalculatedMaxX().floatValue(),
                pulsePlot.getCalculatedMaxY().floatValue());

        pulsePlot.clear();
        stressPlot.clear();
        activityPlot.clear();

        pulsePlot.getGraphWidget().setRangeTick(false);
        stressPlot.getGraphWidget().setRangeTick(false);
        activityPlot.getGraphWidget().setRangeTick(false);

        if (0 == seriesPulse.size())
        {
            seriesPulse.addLast(null, 0);
            seriesStress.addLast(null, 0);
            seriesActivity.addLast(null, 0);
        }

        // same as above:
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null);

        pulsePlot.addSeries(seriesPulse, seriesFormat);
        stressPlot.addSeries(seriesStress, seriesFormat);
        activityPlot.addSeries(seriesActivity, seriesFormat);

        pulsePlot.redraw();
        stressPlot.redraw();
        activityPlot.redraw();

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

                            seriesPulse.addLast(null, (float)deviceInfo.chss);
                            seriesStress.addLast(null, (float)deviceInfo.stressInd);
                            seriesActivity.addLast(null, (float) deviceInfo.aktivnost);
                        }

                        if (null != pulse)
                        {
                            pulse.setText(Integer.toString(deviceInfo.chss));
                            stress.setText(Integer.toString(deviceInfo.stressInd));
                            activity.setText(Integer.toString(deviceInfo.aktivnost));
                            steps.setText(Integer.toString(deviceInfo.stepsCnt));

                            pulsePlot.redraw();
                            stressPlot.redraw();
                            activityPlot.redraw();
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

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;

    @Override
    public boolean onTouch(View arg0, MotionEvent event)
    {
        XYPlot mySimpleXYPlot = (XYPlot)arg0;

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG)
                {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    scroll(oldFirstFinger.x - firstFinger.x, mySimpleXYPlot);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();

                }
//
//                else if (mode == TWO_FINGERS_DRAG) {
//                    float oldDist = distBetweenFingers;
//                    distBetweenFingers = spacing(event);
//                    zoom(oldDist / distBetweenFingers);
//                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
//                            BoundaryMode.FIXED);
//                    mySimpleXYPlot.redraw();
//                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x - minXY.x;
        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;

        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;

        minXY.x = Math.min(minXY.x, series[3].getX(series[3].size() - 3)
                .floatValue());
        maxXY.x = Math.max(maxXY.x, series[0].getX(1).floatValue());
        clampToDomainBounds(domainSpan, null);
    }

    private void scroll(float pan, XYPlot mySimpleXYPlot)
    {
        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();
        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
        clampToDomainBounds(domainSpan, mySimpleXYPlot);
    }

    private void clampToDomainBounds(float domainSpan, XYPlot mySimpleXYPlot)
    {
        SimpleXYSeries series = null;

        switch (mySimpleXYPlot.getId())
        {
            case R.id.pulsePlot:
                series = seriesPulse;
                break;

            case R.id.stressPlot:
                series = seriesStress;
                break;

            case R.id.activityPlot:
                series = seriesActivity;
                break;
        }

        float leftBoundary = series.getX(0).floatValue();
        float rightBoundary = series.getX(series.size() - 1).floatValue();
        // enforce left scroll boundary:
        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > series.getX(series.size() - 1).floatValue()) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
}

