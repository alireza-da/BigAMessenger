package model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public String getUserId(){
        return getString("userId");
    }

    public String getBody(){
        return getString("body");
    }

    public void setUserId(String userId){
        put("userId",userId);
    }

    public void setBody(String body){
        put("body",body);
    }

    public String getUsername(){
        return getString("username");
    }

    public void setUsername(String username){
        put("username",username);
    }


    public void setDate(String date){
        put("date",date);
    }

    public String getDate(){
        return getString("date");
    }
}
