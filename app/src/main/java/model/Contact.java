package model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Contact")
public class Contact extends ParseObject {
    public void setHostId(String hostId){
        put("host", hostId);
    }

    public void setName(String name){
        put("name", name);
    }

    public void setContactUserId(String contactUserId){
        put("contactUserId", contactUserId);
    }

    public void setPhoneNumber(String phoneNumber){
        put("phoneNumber", phoneNumber);
    }

    public String getPhoneNumber(){
        return getString("phoneNumber");
    }

    public String getHostId(){
        return  getString("host");
    }

    public String getName(){
        return getString("name");
    }

    public String getContactUserId(){
        return getString("contactUserId");
    }



}
