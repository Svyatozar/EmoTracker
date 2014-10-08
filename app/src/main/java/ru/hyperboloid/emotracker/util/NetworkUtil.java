package ru.hyperboloid.emotracker.util;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.interfaces.BooleanCallback;
import ru.hyperboloid.emotracker.model.Event;

public class NetworkUtil
{
    private static final String SERVER_NAME = "http://emo.forkme.ru/emotracker-api/api/";

    private static final String CREATE_TOKEN = "tokens/create";
    private static final String CREATE_USER = "users";
    private static final String CREATE_EVENT = "data/saveDataEvent ";

    private RequestQueue queue;

    public NetworkUtil(RequestQueue queue)
    {
        this.queue = queue;
    }

    public void doRegisterRequest(final String login, final String name, final String age, final String gender, final String email, final BooleanCallback callback)
    {
        JSONObject voidObj = null;

        try
        {
            voidObj = new JSONObject("{}");
        }
        catch (JSONException e)
        {
            Log.e("LOG", e.getMessage());
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, SERVER_NAME + CREATE_TOKEN, voidObj,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG", "Response " + response.toString());
                        getRegister(login, name, age, gender, email, response, callback);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("LOG", "Error.Response" + error.toString());
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

        queue.add(postRequest);
    }

    private void getRegister(final String login, final String name, String age, String gender, final String email, final JSONObject tokenInfo, final BooleanCallback callback)
    {
        String tokenId = null;
        String key = null;
        String token = null;

        JSONObject info = null;

        try
        {
            info = tokenInfo.getJSONObject("result");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            tokenId = info.getString("id");
            key = info.getString("key");
            token = info.getString("token");
        }
        catch (JSONException e)
        {}

        final String finalTokenId = tokenId;
        final String finalKey = key;
        final String finalToken = token;

        Map<String, String>  params = new HashMap<String, String>();
        params.put("fullName", name);
        params.put("userName", login);
        params.put("password", "00000");
        params.put("email", email);
        params.put("tokenId", finalTokenId);
        params.put("key", finalKey);
        params.put("token", finalToken);

        JSONObject data = new JSONObject(params);

        Log.i("LOG", data.toString());

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, SERVER_NAME + CREATE_USER, data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG", "Response " + response.toString());

                        String loginId = null;

                        try
                        {
                            JSONObject object = response.getJSONObject("result");
                            loginId = object.getString("id");
                            Log.d("LOG", "LOGIN ID =  " + loginId);
                        }
                        catch (JSONException e)
                        {
                            Log.e("LOG", e.getMessage().toString());
                        }

                        if (null != loginId)
                        {
                            ApplicationWrapper.getSettingsProvider().writeLogin(loginId);
                            callback.onCallback(true);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("LOG", "Error.Response" + volleyError.toString());

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null)
                        {
                            Log.d("LOG", "Error.Response " + new String(volleyError.networkResponse.data));
                        }

                        Toast.makeText(ApplicationWrapper.getContext(), "Ошибка при создании пользователя! Проверьте, все ли поля заполнены верно.", Toast.LENGTH_SHORT).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

        queue.add(registerRequest);
    }

    public void addEvent(Event event)
    {
        // TODO
    }

    public void addEvent(int pulse, int stress, int activity, int steps)
    {
        Log.i("LOG", "ADD EVENT: " + pulse);

        Map<String, String>  params = new HashMap<String, String>();
        params.put("userId", ApplicationWrapper.getSettingsProvider().getLogin());
        params.put("name", "TEST");
        params.put("startDate", "2014-10-09");
        params.put("endDate", "2014-10-09");
        params.put("type_id", "0");
        params.put("isAuto", "true");

        params.put("puls", String.valueOf(pulse));
        params.put("stress", String.valueOf(stress));
        params.put("activity", String.valueOf(activity));

        JSONObject data = new JSONObject(params);

        Log.i("LOG", data.toString());

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, SERVER_NAME + CREATE_EVENT, data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG", "Response " + response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("LOG", "Error.Response" + volleyError.toString());

                        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null)
                        {
                            Log.d("LOG", "Error.Response " + new String(volleyError.networkResponse.data));
                        }
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

        queue.add(registerRequest);
    }
}
