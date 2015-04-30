package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Query;
import com.launch.twinkle.twinkle.models.Chat2;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
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
    ((TextView) view.findViewById(R.id.message)).setText(chat.getMessage());

    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(chat.getTimestamp());
    java.text.DateFormat dateFormat = DateFormat.getTimeFormat(view.getContext());
    TextView messageTime = (TextView) view.findViewById(R.id.message_time);
    messageTime.setVisibility(View.VISIBLE);
    messageTime.setText(dateFormat.format(cal.getTime()));

    final ImageView image;
    ImageView selfProfileImage = (ImageView) view.findViewById(R.id.self_profile_picture);
    ImageView profileImage = (ImageView) view.findViewById(R.id.profile_picture);
    boolean senderIsSelf = chat.getAuthor().equals(mUsername);
    if (senderIsSelf) {
      selfProfileImage.setVisibility(View.VISIBLE);
      profileImage.setVisibility(View.GONE);
      image = selfProfileImage;
    } else {
      selfProfileImage.setVisibility(View.GONE);
      profileImage.setVisibility(View.VISIBLE);
      image = profileImage;
    }

    new PictureLoaderTask(new BitmapRunnable() {
      public void run() {
        image.setImageBitmap(getBitmap());
      }
    }).execute(Utils.getProfileUrl(chat.getAuthor()));

    RelativeLayout messageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
    RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) messageContainer.getLayoutParams();
    float d = image.getContext().getResources().getDisplayMetrics().density;
    int small = (int) (15 * d);
    int medium = (int) (10 * d);
    int large = (int) (35 * d);
    if (senderIsSelf) {
      containerParams.setMargins(small, 0, large, 0);
      messageContainer.setPadding(small, medium, large, medium);
    } else {
      containerParams.setMargins(large, 0, small, 0);
      messageContainer.setPadding(large, medium, small, medium);
    }
    messageContainer.setLayoutParams(containerParams);
  }
}