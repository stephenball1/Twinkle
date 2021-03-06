package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.ChatSummary;
import com.launch.twinkle.twinkle.models.CircleOfTrustChatId;
import com.launch.twinkle.twinkle.models.Users;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class CircleOfTrustAdapter extends FirebaseListAdapter2<CircleOfTrustChatId> {

  private String mUsername;
  private Firebase firebase;

  public CircleOfTrustAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, CircleOfTrustChatId.class, layout, activity);
    this.mUsername = mUsername;
    this.firebase = new Firebase(Constants.FIREBASE_URL);

  }

  @Override
  protected void populateView(View view, CircleOfTrustChatId chatId) {
    final View finalView = view;
    firebase.child("circleOfTrustChat/" + chatId.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(final DataSnapshot snapshot) {
        ChatSummary chatSummary = snapshot.getValue(ChatSummary.class);
        // TODO(judymou): populate name
        new PictureLoaderTask(new BitmapRunnable() {
          public void run() {
            ((ImageView) finalView.findViewById(R.id.matched_profile_picture)).setImageBitmap(getBitmap());
          }
        }).execute(Utils.getProfileUrl(chatSummary.getFriendMatch()));

        new PictureLoaderTask(new BitmapRunnable() {
          public void run() {
            ((ImageView) finalView.findViewById(R.id.initial_profile_picture)).setImageBitmap(getBitmap());
          }
        }).execute(Utils.getProfileUrl(chatSummary.getFriend()));

        firebase.child("user/" + chatSummary.getFriendMatch()).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Users user = dataSnapshot.getValue(Users.class);
            ((TextView) finalView.findViewById(R.id.message)).setText(user.getFirstName());
          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {

          }
        });
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }
}