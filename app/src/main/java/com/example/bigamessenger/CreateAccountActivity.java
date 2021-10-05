package com.example.bigamessenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;

import buttonConfigurations.ProgressGenerator;
import data.FileUtil;
import model.UserDetailed;

public class CreateAccountActivity extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {
    private EditText userNameField, passwordField, emailField, phoneNumberField;
    private ProgressGenerator progressGenerator;
    private Button createAccountButton , setProfilePhotoButton;
    private TextView loginLink;
    private File profilePhotoFile;
    private static int PICK_PHOTO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        progressGenerator = new ProgressGenerator(this);
        createAccountButton = findViewById(R.id.create_account_btn);
        setProfilePhotoButton = findViewById(R.id.upload_profile_btn);
        emailField = findViewById(R.id.user_email_field);
        userNameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        phoneNumberField = findViewById(R.id.phone_number_field);
        loginLink = findViewById(R.id.login_link);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create user and send to database
                final String email = emailField.getText().toString();
                final String username = userNameField.getText().toString();
                final String password = passwordField.getText().toString();
                final String phoneNumberFld = phoneNumberField.getText().toString();
                final String[] id = new String[1];
                final String[] phoneNumber = new String[1];
                final UserDetailed userDetailed = new UserDetailed();
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumberFld.isEmpty()){
                    final AlertDialog.Builder emptyAlert = new AlertDialog.Builder(CreateAccountActivity.this);
                    emptyAlert.setTitle("Empty Error");
                    emptyAlert.setMessage("Fill all field in order to complete the validation and submission.");
                    emptyAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    emptyAlert.show();
                }
                else {
                    final ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if ( e == null){
                                createAccountButton.setEnabled(false);
                                emailField.setEnabled(false);
                                passwordField.setEnabled(false);
                                userNameField.setEnabled(false);

                                id[0] = user.getObjectId();
                                phoneNumber[0] = phoneNumberField.getText().toString();
                                userDetailed.setPhoneNumber(phoneNumber[0]);
                                userDetailed.setUserDetailId(id[0]);
                                final ParseFile photo = new ParseFile(profilePhotoFile);
                                photo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            userDetailed.setProfilePhotoFile(photo);
                                            userDetailed.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getApplicationContext(), "Profile details uploaded", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Profile details upload failed", Toast.LENGTH_SHORT).show();
                                                        e.printStackTrace();
                                                    }
                                                    logInUser(user,password);
                                                    Toast.makeText(getApplicationContext(), "Signed up successfully",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(getApplicationContext(), "can't save your image on server", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            logInUser(user,password);
                                            Toast.makeText(getApplicationContext(), "Signed up successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            else {
                                Log.e("USER", "SIGN UP ERROR");
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });


                }
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
            }
        });
        setProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, PICK_PHOTO);
            }
        });
    }

    private void logInUser(ParseUser user, String password) {
        if (!user.getUsername().isEmpty() && !password.isEmpty()){
            ParseUser.logInInBackground(user.getUsername(), password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null){
                        Log.v("USER", user.getUsername()+" Connected");
                        finish();
                        startActivity(new Intent(CreateAccountActivity.this, ChatActivity.class));
                    }
                    else {
                        Log.e("USER", "SIGN IN ERROR");
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                try {

                    profilePhotoFile = FileUtil.from(this, uri);
                    Toast.makeText(getApplicationContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Photo upload failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onComplete() { // after registration is done
        finish();
        startActivity(new Intent(CreateAccountActivity.this, ChatActivity.class));
    }
}