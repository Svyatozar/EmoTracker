package ru.hyperboloid.emotracker;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.android.volley.toolbox.Volley;

import ru.hyperboloid.emotracker.util.DataBaseWrapper;
import ru.hyperboloid.emotracker.util.NetworkUtil;
import ru.hyperboloid.emotracker.util.SettingsProvider;

public class ApplicationWrapper extends Application
{
    private volatile static ApplicationWrapper instance;

    private static NetworkUtil networkUtil;
    private static SettingsProvider settingsProvider;
    private static DataBaseWrapper dataBaseWrapper;

    private static Context context;

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        context = getApplicationContext();

        dataBaseWrapper = new DataBaseWrapper(context);
        networkUtil = new NetworkUtil(Volley.newRequestQueue(context));
        settingsProvider = new SettingsProvider(context);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    public static NetworkUtil getNetworkUtil()
    {
        return networkUtil;
    }
    public static SettingsProvider getSettingsProvider()
    {
        return settingsProvider;
    }
    public static DataBaseWrapper getDataBaseWrapper()
    {
        return dataBaseWrapper;
    }

    public static Context getContext()
    {
        return context;
    }
}
