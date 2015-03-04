package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageListAdapter extends FirebaseListAdapter<String, MessageWithImage> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;
  private String lookup;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, String.class, layout, activity);
    this.mUsername = mUsername;
  }

  @Override
  protected void populateView(View view, String messageId, MessageWithImage messageWithImage) {
    Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);

    // In theory, when the message becomes not null, another render will be called
    // (because we called notifyDataSetChanged below) and we will successfully have the
    // message
    if (messageWithImage != null) {
      Message message = messageWithImage.getMessage();
      TextView textView = (TextView) view.findViewById(R.id.message);
      boolean senderIsSelf = false;
      if (message != null) {
        textView.setText(message.getMessage());
        senderIsSelf = message.getUserId().equals(ApplicationState.getLoggedInUserId());
      }

      ImageView image;
      ImageView selfProfileImage = (ImageView) view.findViewById(R.id.self_profile_picture);
      ImageView profileImage = (ImageView) view.findViewById(R.id.profile_picture);
      if (senderIsSelf) {
        selfProfileImage.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
        image = selfProfileImage;
      } else {
        selfProfileImage.setVisibility(View.GONE);
        profileImage.setVisibility(View.VISIBLE);
        image = profileImage;
      }
      Bitmap bitmap = messageWithImage.getBitmap();
      if (bitmap != null) {
        image.setImageBitmap(bitmap);
      }

      if (message != null) {
        LayoutParams inputParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
        LayoutParams textParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        float d = image.getContext().getResources().getDisplayMetrics().density;
        int small = (int) (15 * d);
        int medium = (int) (10 * d);
        int large = (int) (35 * d);
        if (message.getUserId().equals(ApplicationState.getLoggedInUserId())) {
          textParams.setMargins(small, 0, large, 0);
          textView.setPadding(small, medium, large, medium);
        } else {
          textParams.setMargins(large, 0, small, 0);
          textView.setPadding(large, medium, small, medium);
        }
        image.setLayoutParams(inputParams);
        textView.setLayoutParams(textParams);
      }
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
          final MessageWithImage messageWithImage = new MessageWithImage(message);
          setSecondaryValue(messageId, messageWithImage);
          notifyDataSetChanged();

          String pictureKey = "users/" + message.getUserId() + "/profilePictureUrl";
          Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL);
          userFirebaseRef.child(pictureKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
              String url = (String) snapshot.getValue();
              new PictureLoaderTask(new BitmapRunnable() {
                public void run() {
                  messageWithImage.setBitmap(getBitmap());
                  notifyDataSetChanged();
                }
              }).execute(url);
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
    } else {
      setSecondaryValue(messageId, null); // @sball I think I did this to handle deleted messages
      notifyDataSetChanged();
    }
  }
}
