package model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


@ParseClassName("Channel")
public class Channel extends ParseObject {
    public void setUsers(List<ParseUser> parseUsers){
        put("users", parseUsers);
    }

    public void setName(String name){
        put("name", name);
    }

    public void setAdmin(ParseUser user){
        put("admin", user);
    }

}
