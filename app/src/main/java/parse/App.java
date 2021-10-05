package parse;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import model.Contact;
import model.Message;
import model.UserDetailed;

public class App extends Application {
    private static final String APP_ID =  "FSD9FK3329SKJFD99SKDJ";
    private static final String CLIENT_KEY = "KJK9AksiHU7lsujKSDJF49jGnTYKD";
    private static final String DEFAULT_URL = "http://193.26.14.226:1337/parse";


    @Override
    public void onCreate() {
        super.onCreate();
        try {

            if (new CheckUrl().execute(DEFAULT_URL).get()){
                Parse.enableLocalDatastore(getApplicationContext());
                ParseObject.registerSubclass(Message.class);
                ParseObject.registerSubclass(UserDetailed.class);
                ParseObject.registerSubclass(Contact.class);
                Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId(APP_ID)
                        .clientKey(CLIENT_KEY)
                        .server(DEFAULT_URL)
                        .build()
                );
            }
            else{
                Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.v("SERVER", "Connected to server - "+Parse.getServer());
    }
    private static class CheckUrl extends AsyncTask<String, URL, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                isConnectedToServer(strings[0]);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }
        private void isConnectedToServer(String url) throws IOException {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.getResponseCode();
        }
    }

}