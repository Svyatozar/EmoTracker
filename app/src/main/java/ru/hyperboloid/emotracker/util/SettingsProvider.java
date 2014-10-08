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
    private static final String LOGIN_FIELD = "login";

    private String PREFERENCE_FILENAME;

    private SharedPreferences sPref;
    private Context context;

    public SettingsProvider(Context context)
    {
        PREFERENCE_FILENAME = context.getString(R.string.preferences_filename);

        sPref = context.getSharedPreferences(PREFERENCE_FILENAME, context.MODE_MULTI_PROCESS);
        this.context = context;
    };

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
