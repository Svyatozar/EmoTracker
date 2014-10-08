package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.adapters.EventsAdapter;

/**
 * Фрагмент с настройками
 */
public class FragmentSettings extends Fragment
{
    private Switch pulseAttentionSwitch;
    private TextView pulseAttentionText;
    private SeekBar pulseAttentionSeekBar;

    private Switch pulseAlertSwitch;
    private TextView pulseAlertText;
    private SeekBar pulseAlertSeekBar;
    // NEXT BLOCK
    private Switch stressAttentionSwitch;
    private TextView stressAttentionText;
    private SeekBar stressAttentionSeekBar;

    private Switch stressAlertSwitch;
    private TextView stressAlertText;
    private SeekBar stressAlertSeekBar;
    // NEXT BLOCK
    private Switch stepAttentionSwitch;
    private TextView stepAttentionText;
    private SeekBar stepAttentionSeekBar;

    private Switch stepNormSwitch;
    private TextView stepNormText;
    private SeekBar stepNormSeekBar;
    // NEXT BLOCK
    private Switch activityAttentionSwitch;
    private TextView activityAttentionText;
    private SeekBar activityAttentionSeekBar;

    private Switch activityNormSwitch;
    private TextView activityNormText;
    private SeekBar activityNormSeekBar;
    // NEXT BLOCK
    private TextView timeStepText;
    private SeekBar timeStepSeekBar;
    // NEXT BLOCK
    private Switch autoEventsSwitch;
    private ToggleButton soundButton;
    private ToggleButton vibroButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        /**
         * Инициализация
         */

        pulseAttentionSwitch = (Switch)rootView.findViewById(R.id.pulseAttentionSwitch);
        pulseAttentionText = (TextView)rootView.findViewById(R.id.pulseAttentionText);
        pulseAttentionSeekBar = (SeekBar)rootView.findViewById(R.id.pulseAttentionSeekBar);

        pulseAlertSwitch = (Switch)rootView.findViewById(R.id.pulseAlertSwitch);
        pulseAlertText = (TextView)rootView.findViewById(R.id.pulseAlertText);
        pulseAlertSeekBar = (SeekBar)rootView.findViewById(R.id.pulseAlertSeekBar);

        setConnectedSwitchers(pulseAttentionSwitch, pulseAlertSwitch);
        connectTextToSeekBar(pulseAttentionSeekBar, pulseAttentionText);
        connectTextToSeekBar(pulseAlertSeekBar, pulseAlertText);
        // NEXT BLOCK
        stressAttentionSwitch = (Switch)rootView.findViewById(R.id.stressAttentionSwitch);
        stressAttentionText = (TextView)rootView.findViewById(R.id.stressAttentionText);
        stressAttentionSeekBar = (SeekBar)rootView.findViewById(R.id.stressAttentionSeekBar);

        stressAlertSwitch = (Switch)rootView.findViewById(R.id.stressAlertSwitch);
        stressAlertText = (TextView)rootView.findViewById(R.id.stressAlertText);
        stressAlertSeekBar = (SeekBar)rootView.findViewById(R.id.stressAlertSeekBar);

        setConnectedSwitchers(stressAttentionSwitch, stressAlertSwitch);
        connectTextToSeekBar(stressAttentionSeekBar, stressAttentionText);
        connectTextToSeekBar(stressAlertSeekBar, stressAlertText);
        // NEXT BLOCK
        stepAttentionSwitch = (Switch)rootView.findViewById(R.id.stepAttentionSwitch);
        stepAttentionText = (TextView)rootView.findViewById(R.id.stepAttentionText);
        stepAttentionSeekBar = (SeekBar)rootView.findViewById(R.id.stepAttentionSeekBar);

        stepNormSwitch = (Switch)rootView.findViewById(R.id.stepNormSwitch);
        stepNormText = (TextView)rootView.findViewById(R.id.stepNormText);
        stepNormSeekBar = (SeekBar)rootView.findViewById(R.id.stepNormSeekBar);

        setConnectedSwitchers(stepAttentionSwitch, stepNormSwitch);
        connectTextToSeekBar(stepAttentionSeekBar, stepAttentionText);
        connectTextToSeekBar(stepNormSeekBar, stepNormText);
        // NEXT BLOCK
        activityAttentionSwitch = (Switch)rootView.findViewById(R.id.activityAttentionSwitch);
        activityAttentionText = (TextView)rootView.findViewById(R.id.activityAttentionText);
        activityAttentionSeekBar = (SeekBar)rootView.findViewById(R.id.activityAttentionSeekBar);

        activityNormSwitch = (Switch)rootView.findViewById(R.id.activityNormSwitch);
        activityNormText = (TextView)rootView.findViewById(R.id.activityNormText);
        activityNormSeekBar = (SeekBar)rootView.findViewById(R.id.activityNormSeekBar);

        setConnectedSwitchers(activityAttentionSwitch, activityNormSwitch);
        connectTextToSeekBar(activityAttentionSeekBar, activityAttentionText);
        connectTextToSeekBar(activityNormSeekBar, activityNormText);

        // NEXT BLOCK
        timeStepText = (TextView)rootView.findViewById(R.id.timeStepText);
        timeStepSeekBar = (SeekBar)rootView.findViewById(R.id.timeStepSeekBar);

        connectTextToSeekBar(timeStepSeekBar, timeStepText);
        // NEXT BLOCK
        autoEventsSwitch = (Switch)rootView.findViewById(R.id.autoEventsSwitch);
        soundButton = (ToggleButton)rootView.findViewById(R.id.soundButton);
        vibroButton = (ToggleButton)rootView.findViewById(R.id.vibroButton);

        /**
         * Конец инициализации
         */

        return rootView;
    }

    private void setConnectedSwitchers(final Switch root, final Switch target)
    {
        root.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    target.setChecked(false);
                    target.setEnabled(false);
                }
                else
                {
                    target.setChecked(true);
                    target.setEnabled(true);
                }

                Log.i("LOG", "CHECKED " + isChecked);

                onCheckListener(buttonView, isChecked);
            }
        });

        target.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    root.setChecked(false);
                    root.setEnabled(false);
                }
                else
                {
                    root.setChecked(true);
                    root.setEnabled(true);
                }

                Log.i("LOG", "CHECKED " + isChecked);

                onCheckListener(buttonView, isChecked);
            }
        });
    }

    private void connectTextToSeekBar(SeekBar seekBar, final TextView textView)
    {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
            {
                textView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                switch (seekBar.getId())
                {

                }
            }
        });
    }

    private void onCheckListener(View checker, boolean isChecked)
    {
        switch (checker.getId())
        {

        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }
}
