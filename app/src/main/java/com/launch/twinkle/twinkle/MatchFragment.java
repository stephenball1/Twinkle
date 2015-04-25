package com.launch.twinkle.twinkle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.User;
import com.launch.twinkle.twinkle.models.Users;

import java.util.Calendar;
import java.util.Locale;

// Match fragment runs assume there is a job that update the matchId for each user everyday.
public class MatchFragment extends Fragment {
  private static final String TAG = MatchFragment.class.getSimpleName();
  // match user id, Name, age, comment, commenter user id, number of messages.
  private View view;
  private Long matchedUserId;
  private Firebase firebase;

  public MatchFragment() {
    firebase = new Firebase(Constants.FIREBASE_URL);
  }

  @Override
  public void onStart() {
    super.onStart();

    String matchedUserIdKey = "matches/" + ApplicationState.getLoggedInUserId() + "/matchedUserId";
    firebase.child(matchedUserIdKey).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        matchedUserId = (Long) snapshot.getValue();
        System.out.println("matchedUserId: " + matchedUserId);
        String userKey = "user/" + matchedUserId;
        firebase.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            Users matchedUser = snapshot.getValue(Users.class);
            setMatchingPage(matchedUser);
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

  public void setMatchingPage(Users user) {
    TextView matchName = (TextView) view.findViewById(R.id.match_name);
    matchName.setText(user.getFirstName());

    TextView matchAge = (TextView) view.findViewById(R.id.match_age);
    matchAge.setText(user.getAge() + " yrs old");
/*
    String key = "messageList/" + matchedUserId;
    firebase.child(key).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        TextView matchMoreMessages = (TextView) view.findViewById(R.id.match_more_messages);

        if (snapshot == null) {
          matchMoreMessages.setText("No messages");
          return;
        }
        MessageList list = snapshot.getValue(MessageList.class);

        if (list == null) {
          Message message = new Message("", ApplicationState.getLoggedInUserId(), "What do you guys think?");
          message.create();
          list = new MessageList(matchedUserId);
          list.pushToChildList("messageIds", message.getId());
        } else {
          LinkedHashMap<String, String> messageIds = list.getMessageIds();
          matchMoreMessages.setText(messageIds.size() + " more messages");

          TreeMap<String, String> sorted = Utils.sortByValue(messageIds);
          Object[] texts = sorted.values().toArray();
          String text = (String) texts[messageIds.size() - 1];
          populateMessage(text);
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
*/
    //setPage(user.getId(), (ImageView) view.findViewById(R.id.match_picture), false);
  }

  public void setPage(String userId, final ImageView imageView, final boolean includeSender) {

    // Proof of concept for binding to picture
    String pictureKey = "users/" + userId;
    Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL);
    userFirebaseRef.child(pictureKey).

    addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        final User user = snapshot.getValue(User.class);
        try {
          new PictureLoaderTask(new BitmapRunnable() {
            public void run() {
              imageView.setImageBitmap(getBitmap());
              if (includeSender) {
                ((TextView) view.findViewById(R.id.message_sender)).setText(user.getFirstName());
              }
            }
          }).execute(user.getProfilePictureUrl());

        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_match, container, false);

    getActivity().invalidateOptionsMenu();

    getActivity().getActionBar().setTitle("Today's Match");

    Button yesButton = (Button) view.findViewById(R.id.yes_button);
    yesButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Button yesButton = (Button) v.findViewById(R.id.yes_button);
        yesButton.setText("Waiting...");
      }
    });

    Button noButton = (Button) view.findViewById(R.id.no_button);
    noButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        System.out.println("here!");
        Fragment waitingFragment = new WaitingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, waitingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
      }
    });
    initTempButton(view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  private class PictureGetter extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... v) {
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }
  }

  private void populateMessage(String messageId) {
    Firebase ref = new Firebase(Constants.FIREBASE_URL + Message.tableName + "/" + messageId);
    ref.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        // Map a Message object to an entry in our listview
        Message message = snapshot.getValue(Message.class);
        final MessageWithImage messageWithImage = new MessageWithImage(message);
        TextView matchMessage = (TextView) view.findViewById(R.id.message);
        matchMessage.setText(message.getMessage());
        TextView messageTime = (TextView) view.findViewById(R.id.message_time);

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
        boolean includeSender = !message.getUserId().equals(ApplicationState.getLoggedInUserId());
        if (!includeSender) view.findViewById(R.id.message_sender).setVisibility(View.GONE);

        setPage(message.getUserId(), (ImageView) view.findViewById(R.id.profile_picture),
                includeSender);
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }

  private void initTempButton(View view) {
    /*
    Button clickButton = (Button) view.findViewById(R.id.match_more_messages);
    clickButton.setOnClickListener( new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Fragment chatFragment = ChatFragment.newInstance(matchedUserId);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, chatFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
      }
    });
    */
  }

}
