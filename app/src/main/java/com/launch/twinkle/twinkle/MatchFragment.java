package com.launch.twinkle.twinkle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.launch.twinkle.twinkle.models.Users;

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
    final ImageView imageView = (ImageView) view.findViewById(R.id.match_picture);
    new PictureLoaderTask(new BitmapRunnable() {
      public void run() {
        imageView.setImageBitmap(getBitmap());
      }
    }).execute(Utils.getProfileUrl(ApplicationState.getLoggedInUserId()));
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
    initMatchMoreMessages(view);
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

  private void initMatchMoreMessages(View view) {
    /*
    Button clickButton = (Button) view.findViewById(R.id.match_more_messages);
    clickButton.setOnClickListener( new View.OnClickListener() {

      @Override
      public void onClick(View v) {
      }
    });
    */
  }

}
