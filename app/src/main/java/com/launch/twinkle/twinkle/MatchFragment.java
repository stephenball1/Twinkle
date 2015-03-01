package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.User;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchFragment extends Fragment {
  private static final String TAG = MatchFragment.class.getSimpleName();
  // match user id, Name, age, comment, commenter user id, number of messages.
  private List<String> templateData = new ArrayList<String>();
  private View view;

  public MatchFragment() {
    templateData.add("10153082238072156");
    templateData.add("");
    templateData.add("");
    templateData.add("Ian, she seems like a really nice girl.");
    templateData.add("10153082238072156");
    templateData.add("6 more messages");
  }

  @Override
  public void onStart() {
    super.onStart();

    String idKey = "users/" + ApplicationState.getLoggedInUserId() + "/matchId";
    Firebase idFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(idKey);
    idFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        String matchId = (String) snapshot.getValue();
        String matchKey = "matches/" + matchId + "/matchedUserId";
        Firebase matchFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(matchKey);
        matchFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            String matchedUserId = (String) snapshot.getValue();
            String userKey = "users/" + matchedUserId;
            Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(userKey);
            userFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot snapshot) {
                User matchedUser = snapshot.getValue(User.class);
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

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }

  public void setMatchingPage(User user) {
    TextView matchName = (TextView) view.findViewById(R.id.match_name);
    matchName.setText(user.getFirstName() + " " + user.getLastName().charAt(0));
    TextView matchAge = (TextView) view.findViewById(R.id.match_age);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    try {
      Date birthday = sdf.parse(user.getBirthday());
      Date today = new Date();
      long diff = today.getYear() - birthday.getYear();
      matchAge.setText(diff + " yrs old");
    } catch (Exception e) {
      e.printStackTrace();
    }

    TextView matchMessage = (TextView) view.findViewById(R.id.message);
    matchMessage.setText(templateData.get(3));
    TextView matchMoreMessages = (TextView) view.findViewById(R.id.match_more_messages);
    matchMoreMessages.setText(templateData.get(5));
    setPage(templateData.get(0), (ImageView) view.findViewById(R.id.match_picture));
    setPage(templateData.get(4), (ImageView) view.findViewById(R.id.profile_picture));
  }

  public void setPage(String userId, final ImageView imageView) {

    // Proof of concept for binding to picture
    String pictureKey = "users/" + userId + "/profilePictureUrl";
    Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL);
    userFirebaseRef.child(pictureKey).

    addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        String url = (String) snapshot.getValue();
        try {
          new PictureLoaderTask(new BitmapRunnable() {
            public void run() {
              imageView.setImageBitmap(getBitmap());
            }
          }).execute(url);

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

    getActivity().getActionBar().setTitle("Today's Match");
    getActivity().invalidateOptionsMenu();

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

        Bundle bundle = new Bundle();
        waitingFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, waitingFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
}
