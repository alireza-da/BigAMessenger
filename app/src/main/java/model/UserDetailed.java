package model;

import android.net.Uri;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


import java.io.File;
import java.util.ArrayList;

@ParseClassName("UserDetailed")
public class UserDetailed extends ParseObject {

    public void setContacts(ArrayList<ParseUser> contacts){
        put("contacts", contacts);
    }



    public ParseFile getProfilePhotoFile() {
        return (ParseFile) get("profilePhotoFile");
    }

    public void setProfilePhotoFile(ParseFile profilePhotoFile) {
        put("profilePhotoFile", profilePhotoFile);
    }

    public String getUserDetailId() {
        return getString("userDetail_id");
    }

    public void setUserDetailId(String id) {
        put("userDetail_id", id);
    }

    public Uri getProfilePhoto() {
        if (getString("profilePhoto") != null){
            return Uri.parse(getString("profilePhoto"));
        }
        return null;
    }

    public void setProfilePhoto(Uri profilePhoto) {
        put("profilePhoto", profilePhoto);
    }

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }
}
