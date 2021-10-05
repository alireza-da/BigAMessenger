package data;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bigamessenger.R;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;

import model.Message;
import model.UserDetailed;

public class ChatListAdaptor extends ArrayAdapter<Message> {
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private HashMap<UserDetailed, Uri> userDetailedList;

    public ChatListAdaptor(@NonNull Context context, String givenUserId, List<Message> messages, HashMap<UserDetailed, Uri> userDetailedList) {
        super(context, 0, messages);
        userId = givenUserId;
        this.userDetailedList = userDetailedList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder = new ViewHolder();
        final Message message = getItem(position);
        final boolean isMe = message.getUserId().equals(userId);
        final Uri profileImageUri = getProfileImage(message.getUserId());

        if (isMe) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_row_sent, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_row_recieved, parent, false);
        }

        viewHolder.username = convertView.findViewById(R.id.text_message_name);
        viewHolder.body = convertView.findViewById(R.id.text_message_body);
        viewHolder.date = convertView.findViewById(R.id.text_message_time);
        viewHolder.profileImage = convertView.findViewById(R.id.image_message_profile);
        convertView.setTag(viewHolder);
        viewHolder.profileImage.setImageURI(profileImageUri);
        viewHolder.body.setText(message.getBody());
        viewHolder.username.setText(message.getUsername());
        viewHolder.date.setText(message.getDate());
        return convertView ;
    }

    private Uri getProfileImage(String givenUserId) {
        for (Map.Entry<UserDetailed, Uri> entry:userDetailedList.entrySet()
             ) {
            if (entry.getKey().getUserDetailId().equals(givenUserId)){
                return entry.getValue();
            }
        }
        return null;
    }



    private class ViewHolder{
        public ImageView profileImage;
        public TextView date;
        public TextView body;
        public TextView username;
    }
}
