package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.launch.twinkle.twinkle.models.Message;

public class MessageListAdapter extends FirebaseListAdapter<Message> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, Message.class, layout, activity);
    this.mUsername = mUsername;
  }

  /**
   * Bind an instance of the <code>Message</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
   * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
   * to the constructor, as well as a single <code>Message</code> instance that represents the current data to bind.
   *
   * @param view A view instance corresponding to the layout we passed to the constructor.
   * @param message An instance representing the current state of a chat message
   */
  @Override
  protected void populateView(View view, Message message) {
    // Map a Message object to an entry in our listview
    String messageText = message.getMessage();
    String userId = message.getUserId();
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
}