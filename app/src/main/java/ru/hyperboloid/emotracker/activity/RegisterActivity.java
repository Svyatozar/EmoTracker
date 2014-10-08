package ru.hyperboloid.emotracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.MainActivity;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.interfaces.BooleanCallback;


public class RegisterActivity extends Activity
{
    private TextView login;
    private TextView name;
    private TextView age;
    private TextView gender;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = (TextView)findViewById(R.id.login);
        name = (TextView)findViewById(R.id.name);
        age = (TextView)findViewById(R.id.age);
        gender = (TextView)findViewById(R.id.gender);
        email = (TextView)findViewById(R.id.email);

        final Button register = (Button)findViewById(R.id.registerButton);
        final Button goButton = (Button)findViewById(R.id.goButton);

        register.setOnClickListener(new View.OnClickListener()
        {
            BooleanCallback callback = new BooleanCallback()
            {
                @Override
                public void onCallback(boolean isSuccess)
                {
                    goButton.setEnabled(true);
                    register.setEnabled(false);

                    ApplicationWrapper.getSettingsProvider().writeLogin(login.getText().toString());
                }
            };

            @Override
            public void onClick(View view)
            {
                ApplicationWrapper.getNetworkUtil().doRegisterRequest(login.getText().toString(),
                                                                      name.getText().toString(),
                                                                      age.getText().toString(),
                                                                      gender.getText().toString(),
                                                                      email.getText().toString(),
                                                                      callback);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        if (null != ApplicationWrapper.getSettingsProvider().getLogin())
        {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
        else
        {
            getActionBar().setTitle(getString(R.string.register));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

