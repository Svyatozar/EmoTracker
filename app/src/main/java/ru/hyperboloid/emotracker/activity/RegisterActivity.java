package ru.hyperboloid.emotracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.splunk.mint.Mint;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.MainActivity;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.interfaces.BooleanCallback;


public class RegisterActivity extends Activity
{
    private TextView login;
    private TextView name;
    private TextView age;
    private Spinner genderSpinner;
    private TextView email;

    private TextView registerTitle;

    private String gender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Crashlytics.start(this);
        Mint.initAndStartSession(RegisterActivity.this, "9ac28b61");
        setContentView(R.layout.activity_register);

        login = (TextView)findViewById(R.id.login);
        name = (TextView)findViewById(R.id.name);
        age = (TextView)findViewById(R.id.age);
        genderSpinner = (Spinner)findViewById(R.id.gender);
        email = (TextView)findViewById(R.id.email);

        registerTitle = (TextView)findViewById(R.id.registerTitle);

        final Button register = (Button)findViewById(R.id.registerButton);
        final Button goButton = (Button)findViewById(R.id.goButton);

        register.setOnClickListener(new View.OnClickListener()
        {
            BooleanCallback callback = new BooleanCallback()
            {
                @Override
                public void onCallback(boolean isSuccess)
                {
                    if (isSuccess)
                    {
                        registerTitle.setText(getString(R.string.press_go));

                        goButton.setEnabled(true);
                        register.setEnabled(false);
                    }
                    else
                    {

                    }
                }
            };

            @Override
            public void onClick(View view)
            {
                if ((gender != null) && (!gender.equals("Пол")))
                {
                    ApplicationWrapper.getNetworkUtil().doRegisterRequest(login.getText().toString(),
                            name.getText().toString(),
                            age.getText().toString(),
                            gender,
                            email.getText().toString(),
                            callback);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Корректно заполните все поля!", Toast.LENGTH_SHORT).show();
                }

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(adapter);
        genderSpinner.setPrompt(getString(R.string.enter_gender));

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                gender = (String)parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}

