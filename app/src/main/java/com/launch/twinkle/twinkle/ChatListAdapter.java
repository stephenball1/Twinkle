package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Match;
import com.launch.twinkle.twinkle.models.MessageList;
import com.launch.twinkle.twinkle.models.User;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;

public class ChatListAdapter extends FirebaseListAdapter<String, ChatPreview> {

  private String lookup;

  public ChatListAdapter(Query ref, Activity activity, int layout) {
    super(ref, String.class, layout, activity);
  }

  @Override
  protected void populateView(View view, String chatId, ChatPreview preview) {
    TextView textView = (TextView) view.findViewById(R.id.message);
    final View finalView = view;

    // In theory, when the message becomes not null, another render will be called
    // (because we called notifyDataSetChanged below) and we will successfully have the
    // message
    if (preview != null) {
      Message message = preview.getLastMessage();
      User matchedUser = preview.getMatchedUser();
      User initialUser = preview.getInitialUser();
      if (message != null && matchedUser != null && initialUser != null) {
        textView.setText(initialUser.getDisplayName());
      }

      Bitmap matchedUserBitmap = preview.getMatchedUserBitmap();
      if (matchedUserBitmap != null) {
        ImageView image = (ImageView) view.findViewById(R.id.matched_profile_picture);
        image.setImageBitmap(matchedUserBitmap);
      }

      Bitmap initialUserBitmap = preview.getInitialUserBitmap();
      if (initialUserBitmap != null) {
        ImageView image = (ImageView) view.findViewById(R.id.initial_profile_picture);
        image.setImageBitmap(initialUserBitmap);
      }
    }
  }

  @Override
  protected void update(final String chatId) {
    if (chatId != null && chatId.length() > 0) {
      final ChatPreview preview = new ChatPreview();
      Firebase ref = new Firebase(Constants.FIREBASE_URL + "messageLists/" + chatId);
      ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
          // Map a Message object to an entry in our listview
          MessageList list = snapshot.getValue(MessageList.class);

          if (list == null) {
            Message message = new Message("", ApplicationState.getLoggedInUserId(), "What do you guys think?");
            message.create();
            list = new MessageList(chatId);
            list.pushToChildList("messageIds", message.getId());
          } else {
            TreeMap<String, String> sorted = Utils.sortByValue(list.getMessageIds());
            Object[] texts = sorted.values().toArray();
            String messageId = (String) texts[texts.length - 1];

            Firebase messageRef = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);
            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot snapshot) {
                // Map a Message object to an entry in our listview
                Message message = snapshot.getValue(Message.class);
                preview.setLastMessage(message);

                setSecondaryValue(chatId, preview);
                notifyDataSetChanged();
              }

              @Override
              public void onCancelled(FirebaseError firebaseError) {
              }
            });
          }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
      });

      Firebase matchRef = new Firebase(Constants.FIREBASE_URL + "matches/" + chatId);
      matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
          Match match = snapshot.getValue(Match.class);

          String matchedUserKey = "users/" + match.getMatchedUserId();
          Firebase matchedUserRef = new Firebase(Constants.FIREBASE_URL).child(matchedUserKey);
          matchedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
              User matchedUser = snapshot.getValue(User.class);
              preview.setMatchedUser(matchedUser);
              String url = matchedUser.getProfilePictureUrl();

              new PictureLoaderTask(new BitmapRunnable() {
                public void run() {
                  preview.setMatchedUserBitmap(getBitmap());
                  setSecondaryValue(chatId, preview);
                  notifyDataSetChanged();
                }
              }).execute(url);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
          });

          String initialUserKey = "users/" + match.getInitialUserId();
          Firebase initialUserRef = new Firebase(Constants.FIREBASE_URL).child(initialUserKey);
          initialUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
              User user = snapshot.getValue(User.class);
              preview.setInitialUser(user);
              String url = user.getProfilePictureUrl();

              new PictureLoaderTask(new BitmapRunnable() {
                public void run() {
                  preview.setInitialUserBitmap(getBitmap());
                  setSecondaryValue(chatId, preview);
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
      setSecondaryValue(chatId, null); // @sball I think I did this to handle deleted messages
      notifyDataSetChanged();
    }
  }
}
