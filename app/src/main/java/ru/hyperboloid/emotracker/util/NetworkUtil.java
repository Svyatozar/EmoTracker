package ru.hyperboloid.emotracker.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.interfaces.BooleanCallback;
import ru.hyperboloid.emotracker.model.Event;

public class NetworkUtil
{
    private static final String SERVER_NAME = "http://emo.forkme.ru/emotracker-api/api/";

    private static final String CREATE_TOKEN = "tokens/create";
    private static final String CREATE_USER = "users";
    private static final String LOG_IN = "users/login";
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
                            String errorText = new String(volleyError.networkResponse.data);
                            Log.d("LOG", "Error.Response " + errorText);

                            if (errorText.contains("Email already exist"))
                            {
                                Toast.makeText(ApplicationWrapper.getContext(), "Ошибка при создании пользователя! Такой email уже существует", Toast.LENGTH_SHORT).show();
                            }
                            else
                                if (errorText.contains("Name already exist"))
                                {
                                    logIn(login, callback);
                                }
                                else
                                    callback.onCallback(false);
                        }
                        else
                        {
                            callback.onCallback(false);
                        }

                        // Duplicate User. Email already exist
                        // Duplicate User. Name already exist
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

    private void logIn(final String userName, final BooleanCallback callback)
    {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("userName", userName);
        params.put("password", "00000");

        JSONObject data = new JSONObject(params);

        Log.i("LOG", data.toString());

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, SERVER_NAME + LOG_IN, data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG", "Response FOR LOGIN" + response.toString());

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
                            Toast.makeText(ApplicationWrapper.getContext(), "Логин уже зарегистрирован, вы авторизованы.", Toast.LENGTH_SHORT).show();
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
                            String errorText = new String(volleyError.networkResponse.data);
                            Log.d("LOG", "Error.Response " + errorText);

                            callback.onCallback(false);
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

    public void addEvent(Event event)
    {
        // TODO
    }

    public void addEvent(String name, final int pulse, final int stress, final int activity, int steps)
    {
        Log.i("LOG", "ADD EVENT: " + pulse);

        Map<String, String>  params = new HashMap<String, String>();
        params.put("userId", ApplicationWrapper.getSettingsProvider().getLogin());
        params.put("name", name);
        params.put("startDate", formatDate(Calendar.getInstance().getTime()));
        params.put("endDate", formatDate(Calendar.getInstance().getTime()));
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

//        new AsyncTask<Void, Void, Void>()
//        {
//            @Override
//            protected Void doInBackground(Void... voids)
//            {
//
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httppost = new HttpPost(SERVER_NAME + CREATE_TOKEN);
//
//                try
//                {
//                    //httppost.setEntity(new StringEntity("{}"));
//                    httppost.setHeader("Accept", "application/json");
//                    httppost.setHeader("Content-Type", "application/json");
//
//                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//                    Log.i("LOG", "TIME: " + Calendar.getInstance().getTime().toString());
//
//                    nameValuePairs.add(new BasicNameValuePair("userId", ApplicationWrapper.getSettingsProvider().getLogin()));
//                    nameValuePairs.add(new BasicNameValuePair("name", "TEST"));
//                    nameValuePairs.add(new BasicNameValuePair("startDate", Calendar.getInstance().getTime().toString()));
//                    nameValuePairs.add(new BasicNameValuePair("endDate", Calendar.getInstance().getTime().toString()));
//                    nameValuePairs.add(new BasicNameValuePair("type_id", "1"));
//                    nameValuePairs.add(new BasicNameValuePair("isAuto", "true"));
//                    nameValuePairs.add(new BasicNameValuePair("puls", String.valueOf(pulse)));
//                    nameValuePairs.add(new BasicNameValuePair("stress", String.valueOf(stress)));
//                    nameValuePairs.add(new BasicNameValuePair("activity", String.valueOf(activity)));
//
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    // Execute HTTP Post Request
//                    HttpResponse response = httpclient.execute(httppost);
//
//                    HttpEntity res = new BufferedHttpEntity(response.getEntity());
//
//                    InputStream inputStream = res.getContent();
//
//                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder total = new StringBuilder();
//                    String line;
//                    while ((line = r.readLine()) != null)
//                    {
//                        total.append(line);
//                    }
//
//                    Log.i("LOG", "ANSWER: " + total.toString());
//                }
//                catch (ClientProtocolException e)
//                {
//                    Log.e("LOG", "ERROR: " + e.getMessage());
//                }
//                catch (IOException e)
//                {
//                    Log.e("LOG", "ERROR: " + e.getMessage());
//                }
//
//                return null;
//            }
//        }.execute();
    }

    public String formatDate(Date date)
    {
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(ApplicationWrapper.getContext().getString(R.string.date_format), Locale.US);

        return targetDateFormat.format(date);
    }
}
