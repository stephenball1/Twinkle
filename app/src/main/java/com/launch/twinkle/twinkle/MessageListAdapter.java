package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.MessageList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageListAdapter extends FirebaseListAdapter<MessageList> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;
  private List<Message> messages;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, MessageList.class, layout, activity);
    this.mUsername = mUsername;
    this.messages = new ArrayList<Message>();
  }

  /**
   * Bind an instance of the <code>Message</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
   * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
   * to the constructor, as well as a single <code>Message</code> instance that represents the current data to bind.
   *
   * @param view A view instance corresponding to the layout we passed to the constructor.
   * @param messageList An instance representing the current state of a chat message list
   */
  @Override
  protected void populateView(View view, MessageList messageList) {
    // Map a Message object to an entry in our listview
    String messageText = "" + messages.size();
    String userId = "WOMPWOMP"; //message.getUserId();
    TextView authorText = (TextView) view.findViewById(R.id.author);
    authorText.setText(userId + ": ");
    // If the message was sent by this user, color it differently
    if (userId != null && userId.equals(mUsername)) {
      authorText.setTextColor(Color.RED);
    } else {
      authorText.setTextColor(Color.BLUE);
    }
    ((TextView) view.findViewById(R.id.message)).setText(messageText);
  }

  @Override
  protected void update(MessageList messageList) {
    updateMessages(messageList);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return messages.size();
  }

  @Override
  public Object getItem(int i) {
    return messages.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  private void updateMessages(MessageList messageList) {
    Set<String> messageIds = messageList.getMessageIds();
    messages.clear();
    if (messageIds != null) {

      for (String messageId : messageIds) {
        Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            Message message = snapshot.getValue(Message.class);
            messages.add(message);
          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {
          }
        });
      }
    }
  }
}