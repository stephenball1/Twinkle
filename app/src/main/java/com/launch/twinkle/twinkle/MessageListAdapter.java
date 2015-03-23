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
import com.launch.twinkle.twinkle.models.User;

import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MessageListAdapter extends FirebaseListAdapter<String, MessageWithUser> {

  // The mUsername for this client. We use this to indicate which messages originated from this user
  private String mUsername;
  private String lookup;

  public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, String.class, layout, activity);
    this.mUsername = mUsername;
  }

  @Override
  // TODO @sball: a lot of this code is duplicated in MatchFragment
  protected void populateView(View view, String messageId, MessageWithUser messageWithUser) {
    Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);

    // In theory, when the message becomes not null, another render will be called
    // (because we called notifyDataSetChanged below) and we will successfully have the
    // message
    if (messageWithUser != null) {
      Message message = messageWithUser.getMessage();
      TextView textView = (TextView) view.findViewById(R.id.message);
      TextView messageTime = (TextView) view.findViewById(R.id.message_time);


      boolean senderIsSelf = false;
      if (message != null) {
        textView.setText(message.getMessage());
        senderIsSelf = message.getUserId().equals(ApplicationState.getLoggedInUserId());
        if (message.getTimestamp() > 0) {
          messageTime.setVisibility(View.VISIBLE);
          // Todo @sball: refactor to better share code between message list
          // and profile message snippet
          Calendar cal = Calendar.getInstance(Locale.ENGLISH);
          cal.setTimeInMillis(message.getTimestamp());
          java.text.DateFormat dateFormat = DateFormat.getTimeFormat(view.getContext());
          messageTime.setText(dateFormat.format(cal.getTime()));
        } else {
          messageTime.setVisibility(View.GONE);
        }
      }

      User sender = messageWithUser.getUser();
      TextView senderName = ((TextView) view.findViewById(R.id.message_sender));
      if (sender != null) {
        senderName.setText(sender.getFirstName());
      }

      ImageView image;
      ImageView selfProfileImage = (ImageView) view.findViewById(R.id.self_profile_picture);
      ImageView profileImage = (ImageView) view.findViewById(R.id.profile_picture);
      if (senderIsSelf) {
        selfProfileImage.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
        senderName.setVisibility(View.GONE);
        image = selfProfileImage;
      } else {
        selfProfileImage.setVisibility(View.GONE);
        profileImage.setVisibility(View.VISIBLE);
        senderName.setVisibility(View.VISIBLE);
        image = profileImage;
      }

      Bitmap bitmap = messageWithUser.getUserImage();
      if (bitmap != null) {
        image.setImageBitmap(bitmap);
      }

      if (message != null) {
        RelativeLayout messageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
        LayoutParams containerParams = (RelativeLayout.LayoutParams) messageContainer.getLayoutParams();
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
          final MessageWithUser messageWithUser = new MessageWithUser(message);
          setSecondaryValue(messageId, messageWithUser);
          notifyDataSetChanged();

          String userKey = "users/" + message.getUserId();
          Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL);
          userFirebaseRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
              User user = snapshot.getValue(User.class);
              messageWithUser.setUser(user);
              new PictureLoaderTask(new BitmapRunnable() {
                public void run() {
                  messageWithUser.setUserImage(getBitmap());
                  notifyDataSetChanged();
                }
              }).execute(user.getProfilePictureUrl());
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
