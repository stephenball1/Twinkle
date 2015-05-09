package com.launch.twinkle.twinkle;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
  private Firebase firebase;
  private ChatListAdapter2 mListAdapter;

  public MatchFragment() {
    firebase = new Firebase(Constants.FIREBASE_URL);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_match, container, false);

    getActivity().getActionBar().setTitle("Today's Match");
    getActivity().getActionBar().show();

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
        Fragment waitingFragment = new WaitingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, waitingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
      }
    });

    String matchedUserIdKey = "matches/" + ApplicationState.getLoggedInUserId() + "/matchedUserId";
    firebase.child(matchedUserIdKey).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        final String matchedUserId = (String) snapshot.getValue();
        firebase.child("user/" + matchedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            Users matchedUser = snapshot.getValue(Users.class);
            setMatchingPage(matchedUser, matchedUserId);
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
    mListAdapter.cleanup();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  public void setMatchingPage(Users user, final String matchedUserId) {
    TextView matchName = (TextView) view.findViewById(R.id.match_name);
    matchName.setText(user.getFirstName());

    TextView matchAge = (TextView) view.findViewById(R.id.match_age);
    matchAge.setText(user.getAge() + " yrs old");

    firebase.child("circleOfTrustChat/" + ApplicationState.getLoggedInUserId()
        + "-" + matchedUserId + "/chatRoomId").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        final String chatRoomId = (String) snapshot.getValue();
        final ListView listView = (ListView) view.findViewById(R.id.chat_message);
        mListAdapter = new ChatListAdapter2(
            firebase.child("chat/" + chatRoomId + "/messages").limitToLast(1),
            getActivity(),
            R.layout.chat_message,
            ApplicationState.getLoggedInUserId());
        listView.setAdapter(mListAdapter);
        mListAdapter.registerDataSetObserver(new DataSetObserver() {
          @Override
          public void onChanged() {
            super.onChanged();
            System.out.println("mListAdapter.getCount()" + mListAdapter.getCount());
            listView.setSelection(mListAdapter.getCount() - 1);
          }
        });

        Button seeMoreMessage = ((Button) view.findViewById(R.id.match_more_messages));
        seeMoreMessage.setText("See More Messages");
        seeMoreMessage.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            ApplicationState.setChatRoomId(chatRoomId);
            ApplicationState.setTopicUser(matchedUserId);

            Fragment chatListFragment = new ChatListFragment2();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, chatListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
          }
        });
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });

    final ImageView imageView = (ImageView) view.findViewById(R.id.match_picture);
    new PictureLoaderTask(new BitmapRunnable() {
      public void run() {
        imageView.setImageBitmap(getBitmap());
      }
    }).execute(Utils.getProfileUrl(matchedUserId));
  }
}
