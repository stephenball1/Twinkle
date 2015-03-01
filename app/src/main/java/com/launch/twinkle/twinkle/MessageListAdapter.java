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

public class MessageListAdapter extends FirebaseListAdapter<String, Message> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;
  private String lookup;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, String.class, layout, activity);
    this.mUsername = mUsername;
  }

  @Override
  protected void populateView(View view, String messageId, Message message) {
    Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);

    TextView textView = (TextView) view.findViewById(R.id.message);
    final View finalView = view;

    // In theory, when the message becomes not null, another render will be called
    // (because we called notifyDataSetChanged below) and we will successfully have the
    // message
    if (message != null) {
      textView.setText(message.getMessage());
    }
  }

  @Override
  protected void update(final String messageId) {
    if (messageId != null && messageId.length() > 0) {
      Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);
      ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
          // Map a Message object to an entry in our listview
          Message message = snapshot.getValue(Message.class);
          setSecondaryValue(messageId, message);
          notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
      });
    } else {
      setSecondaryValue(messageId, null); // @sball I think I did this to handle deleted messages
      notifyDataSetChanged();
    }
  }
}
