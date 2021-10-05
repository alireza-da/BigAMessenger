package com.example.bigamessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.GetFileCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buttonConfigurations.ProgressGenerator;
import data.ChatListAdaptor;
import model.Message;
import model.UserDetailed;

public class ChatActivity extends AppCompatActivity {
    private int notificationId = 0;
    private boolean newMessages;
    private static final String CHANNEL_ID = "BIG A "+ParseUser.getCurrentUser().getUsername()+ParseUser.getCurrentUser().getObjectId();
    private EditText messageField;
    private Button sendButton;
    private ProgressGenerator progressGenerator;
    private static String USER_ID = "userId";
    private String currentUserId;
    private String currentUsername, currentUserEmail;
    private ListView chatListView;
    private ArrayList<Message> messageArrayList;
    private HashMap<UserDetailed, Uri> userDetailedArrayList;
    private ChatListAdaptor chatListAdaptor;
    private Handler handler = new Handler();
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 70;
    private DrawerLayout drawerLayout;
    private TextView profileNavEmail, profileNavUsername, versionNumberTextView;
    private NavigationView navigationView;
    private ImageView profileNavPhoto;
    private final String VERSION_NUMBER = BuildConfig.VERSION_NAME;
    private Uri imageProfileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getCurrentUser();
        handler.postDelayed(runnable, 100);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.contact_us_item) {
                    Intent mailIntent = new Intent(Intent.ACTION_SEND);
                    mailIntent.setType("message/rfc822");
                    mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"siriusblackir@gmail.com"});
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacting Big A Support");
                    mailIntent.putExtra(Intent.EXTRA_TEXT, "ID: " + currentUserId + ":\n\n");
                    startActivity(Intent.createChooser(mailIntent, "Choose email client"));
                }
                // complete this part later
//                if (item.getItemId() == R.id.edit_profile_item){
//
//                }
//                if (item.getItemId() == R.id.setting_item){
//
//                }
                if (item.getItemId() == R.id.contacts_item){
                    startActivity(new Intent(ChatActivity.this, ContactsActivity.class));
                }
                return true;
            }
        });
    }


    private void setProfileInfo() {
        profileNavUsername.setText(currentUsername);
        profileNavEmail.setText(currentUserEmail);
        String version = "Version: "+VERSION_NUMBER;
        versionNumberTextView.setText(version);
        ParseQuery<UserDetailed> parseQueryUserDetailed = new ParseQuery<>(UserDetailed.class);
        parseQueryUserDetailed.findInBackground(new FindCallback<UserDetailed>() {
            @Override
            public void done(List<UserDetailed> objects, ParseException e) {
                try {
                    if (e == null && ParseUser.getQuery().count() != objects.size()) {
                        for (UserDetailed u : objects
                        ) {
                            final UserDetailed userDetailed = u;
                            u.getProfilePhotoFile().getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    if(e == null){
                                        if (userDetailed.getUserDetailId().equals(currentUserId)) {
                                            profileNavPhoto.setImageURI(Uri.fromFile(file));
                                        }
                                        imageProfileUri = Uri.fromFile(file);
                                        if (!isContainedUserDetail(userDetailed)) {
                                            userDetailedArrayList.put(userDetailed, imageProfileUri);
                                        }
                                    }
                                }
                            });

                        }
                    }
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{

            super.onBackPressed();
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                refreshChats();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            handler.postDelayed(this, 850);
        }
    };

    private void getCurrentUser() {
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        currentUsername = ParseUser.getCurrentUser().getUsername();
        currentUserEmail = ParseUser.getCurrentUser().getEmail();
        messagePosting();
    }

    private void messagePosting() {
        messageField =  findViewById(R.id.message_field);
        sendButton = findViewById(R.id.send_button);
        chatListView = findViewById(R.id.chat_listView);
        messageArrayList = new ArrayList<>();
        userDetailedArrayList = new HashMap<>();
        chatListAdaptor = new ChatListAdaptor(ChatActivity.this , currentUserId, messageArrayList, userDetailedArrayList);
        chatListView.setAdapter(chatListAdaptor);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageField.getText().toString().isEmpty()){
                    Message message = new Message();
                    message.setUserId(currentUserId);
                    message.setUsername(currentUsername);
                    message.setBody(messageField.getText().toString());
                    message.setDate(LocalDateTime.now().getHour() +":"+ (LocalDateTime.now().getMinute() >= 10 ? LocalDateTime.now().getMinute():"0"+LocalDateTime.now().getMinute()));
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            receiveMessage();
                        }
                    });
                    messageField.setText("");
                }else Toast.makeText(getApplicationContext(), "Fill message field!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void receiveMessage(){
        ParseQuery<Message> messageParseQuery = new ParseQuery<Message>(Message.class);
        messageParseQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        messageParseQuery.orderByAscending("createdAt");
        messageParseQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e == null){
                    messageArrayList.clear();
                    messageArrayList.addAll(objects);
                    chatListAdaptor.notifyDataSetChanged();
                    chatListView.invalidate();
                }else {
                    Log.e("DATA", "Retrieving data error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshChats() throws ParseException {
        profileNavEmail = findViewById(R.id.nav_profile_email);
        profileNavUsername = findViewById(R.id.nav_profile_username);
        profileNavPhoto = findViewById(R.id.nav_profile_photo);
        versionNumberTextView = findViewById(R.id.version_number_textView);
        setProfileInfo();
        receiveMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out){
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        finish();
                        startActivity(new Intent(ChatActivity.this, MainActivity.class));
                    }else {
                        Log.v("USER", "Logout error");
                        e.printStackTrace();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isContainedUserDetail(UserDetailed userDetailed){
        for (Map.Entry<UserDetailed, Uri> entry:userDetailedArrayList.entrySet()
             ) {
            if (entry.getKey().getUserDetailId().equals(userDetailed.getUserDetailId())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}