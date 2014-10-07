package ru.hyperboloid.emotracker;

import android.app.Application;
import android.content.res.Configuration;

import com.android.volley.toolbox.Volley;

import ru.hyperboloid.emotracker.util.NetworkUtil;

public class ApplicationWrapper extends Application
{
    private volatile static ApplicationWrapper instance;

    private static NetworkUtil networkUtil;

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

        networkUtil = new NetworkUtil(Volley.newRequestQueue(getApplicationContext()));
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
}
