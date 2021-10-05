package com.example.bigamessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button loginButton ;
    private TextView createAccButton;
    private EditText usernameField , passwordField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.btnLogin);
        createAccButton = findViewById(R.id.btnSignUp);
        createAccButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        usernameField = findViewById(R.id.login_username_field);
        passwordField = findViewById(R.id.login_password_field);
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnSignUp:
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
                break;

            case R.id.btnLogin:
                if (!usernameField.getText().toString().isEmpty() && !passwordField.getText().toString().isEmpty()){
                    try {
                        loginUser(usernameField.getText().toString(), passwordField.getText().toString());
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Login failed!" , Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(getApplicationContext(), "Please input username and password to login.", Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void loginUser(final String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Log.v("USER", username + " logged in successfully");
                    finish();
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.e("USER", "Log in error");
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}