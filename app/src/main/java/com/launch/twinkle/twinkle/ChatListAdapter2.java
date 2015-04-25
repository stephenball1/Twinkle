package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.launch.twinkle.twinkle.models.Chat2;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter2 extends FirebaseListAdapter2<Chat2> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;

  public ChatListAdapter2(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, Chat2.class, layout, activity);
    this.mUsername = mUsername;
  }

  /**
   * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
   * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
   * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
   *
   * @param view A view instance corresponding to the layout we passed to the constructor.
   * @param chat An instance representing the current state of a chat message
   */
  @Override
  protected void populateView(View view, Chat2 chat) {
    // Map a Chat object to an entry in our listview
    String author = chat.getAuthor();
    TextView authorText = (TextView) view.findViewById(R.id.author2);
    authorText.setText(author + ": ");
    // If the message was sent by this user, color it differently
    if (author != null && author.equals(mUsername)) {
      authorText.setTextColor(Color.RED);
    } else {
      authorText.setTextColor(Color.BLUE);
    }
    ((TextView) view.findViewById(R.id.message2)).setText(chat.getMessage());
  }
}