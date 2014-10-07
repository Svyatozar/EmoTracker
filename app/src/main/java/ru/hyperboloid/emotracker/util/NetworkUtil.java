package ru.hyperboloid.emotracker.util;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.hyperboloid.emotracker.interfaces.BooleanCallback;

public class NetworkUtil
{
    private static final String SERVER_NAME = "http://emo.forkme.ru/emotracker-api/api/";

    private static final String CREATE_TOKEN = "tokens/create";
    private static final String CREATE_USER = "users/create";

    private RequestQueue queue;

    public NetworkUtil(RequestQueue queue)
    {
        this.queue = queue;
    }

    public void doRegisterRequest(final String login, final String name, final String age, final String gender, final BooleanCallback callback)
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
                        getRegister(login, name, age, gender, response);

                        callback.onCallback(true);
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

    private void getRegister(final String login, final String name, String age, String gender, final JSONObject tokenInfo)
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

        Map<String, String>  params = new HashMap<String, String>();
        params.put("fullName", name);
        params.put("userName", login);
        params.put("password", "00000");
        params.put("email", "testmail@mail.mail");
        params.put("tokenId", tokenId);
        params.put("key", key);
        params.put("token", token);

        JSONObject parameters = new JSONObject(params);

        Log.i("LOG", "JSON: " + parameters.toString());

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, SERVER_NAME + CREATE_USER, parameters,
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

        //queue.add(registerRequest);
    }
}
