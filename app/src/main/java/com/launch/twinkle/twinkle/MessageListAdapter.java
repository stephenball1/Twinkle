package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageListAdapter extends FirebaseListAdapter<Boolean> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;
  private List<Message> messages;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, Boolean.class, layout, activity);
    this.mUsername = mUsername;
  }

  @Override
  protected void populateView(View view, String messageId, Boolean bool) {
    Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);

    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    //StrictMode.setThreadPolicy(policy);

    final View finalView = view;
    final String finalMessageId = messageId;

    ref.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        // Map a Message object to an entry in our listview
        Message message = snapshot.getValue(Message.class);
        String messageText = message.getMessage();
        String userId = message.getUserId();
        TextView authorText = (TextView) finalView.findViewById(R.id.author);
        authorText.setText(finalMessageId + ": ");
        // If the message was sent by this user, color it differently
        if (userId != null && userId.equals(mUsername)) {
          authorText.setTextColor(Color.RED);
        } else {
          authorText.setTextColor(Color.BLUE);
        }
        ((TextView) finalView.findViewById(R.id.message)).setText(messageText);
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }
}
