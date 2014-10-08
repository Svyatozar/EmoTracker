package ru.hyperboloid.emotracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ru.hyperboloid.emotracker.R;

/**
 * Created by svyatozar on 22.05.14.
 */

public class SettingsProvider
{
    public static final String LOGIN_FIELD = "login";

    public static final String PULSE_ATTENTION_VALUE = "pls_atnsh_val";
    public static final String PULSE_ALERT_VALUE = "pls_alert_val";
    public static final String PULSE_SWITCHER_STATE = "pls_sw_state";

    public static final String STRESS_ATTENTION_VALUE = "strs_at_val";
    public static final String STRESS_ALERT_VALUE = "strs_al_val";
    public static final String STRESS_SWITCHER_STATE = "strs_sw_state";

    public static final String STEP_ATTENTION_VALUE = "step_at_val";
    public static final String STEP_NORMAL_VALUE = "step_norm_val";
    public static final String STEP_SWITCHER_STATE = "step_sw_state";

    public static final String ACTIVITY_ATTENTION_VALUE = "activity_at_val";
    public static final String ACTIVITY_NORMAL_VALUE = "activity_norm_val";
    public static final String ACTIVITY_SWITCHER_STATE = "activity_sw_state";

    public static final String TIME_STEP_VALUE = "time_step";

    public static final String IS_SOUND_USED = "is_sound";
    public static final String IS_VIBRO_USED = "is_vibro";

    private String PREFERENCE_FILENAME;

    private SharedPreferences sPref;
    private Context context;

    public SettingsProvider(Context context)
    {
        PREFERENCE_FILENAME = context.getString(R.string.preferences_filename);

        sPref = context.getSharedPreferences(PREFERENCE_FILENAME, context.MODE_MULTI_PROCESS);
        this.context = context;
    };

    public void writeThresholdValue(String source, int value)
    {
        Log.i("LOG", "WRITE THRESHOLD = " + value);

        SharedPreferences.Editor ed = sPref.edit();

        ed.putInt(source, value);

        ed.commit();
    }

    public int getThresholdValue(String source)
    {
        return sPref.getInt(source, 0);
    }

    public void writeSwitcherState(String source, boolean state)
    {
        Log.i("LOG", "WRITE SWITCHER STATE = " + state);

        SharedPreferences.Editor ed = sPref.edit();

        ed.putBoolean(source, state);

        ed.commit();
    }

    public boolean getSwitcherState(String source)
    {
        return sPref.getBoolean(source, true);
    }

    public void writeTimeStep(int step)
    {
        Log.i("LOG", "WRITE TIME STEP = " + step);

        SharedPreferences.Editor ed = sPref.edit();

        ed.putInt(TIME_STEP_VALUE, step);

        ed.commit();
    }

    public int getTimeStep()
    {
        return sPref.getInt(TIME_STEP_VALUE, 0);
    }

    public void writeLogin(String login)
    {
        Log.i("LOG", "WRITE Login = " + login);

        SharedPreferences.Editor ed = sPref.edit();

        ed.putString(LOGIN_FIELD, login);

        ed.commit();
    }

    public String getLogin()
    {
        String login = sPref.getString(LOGIN_FIELD, null);

        return login;
    }
}
