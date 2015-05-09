package com.launch.twinkle.twinkle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.FirebaseRef;
import com.launch.twinkle.twinkle.models.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {
  private static final String TAG = MainFragment.class.getSimpleName();
  Firebase firebaseRef;
  private UiLifecycleHelper uiHelper;
  private Map<String, Object> facebookProfile;
  private int taskCount = 0;

  public MainFragment() {
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main2, container, false);
    getActivity().getActionBar().hide();

    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setBackgroundColor(Color.parseColor("#E91E63"));
    authButton.setAllCaps(false);
    authButton.setFragment(this);
    authButton.setReadPermissions(
        Arrays.asList("public_profile,user_birthday,email,user_friends,user_photos,user_status"));
    authButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
      @Override
      public void onUserInfoFetched(GraphUser user) {
        if (user != null) {
          showMatchFragment();
        }
      }
    });
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    // For scenarios where the main activity is launched and user
    // session is not null, the session state change notification
    // may not be triggered. Trigger it if it's open/closed.
    Session session = Session.getActiveSession();
    if (session != null &&
        (session.isOpened() || session.isClosed())) {
      onSessionStateChange(session, session.getState(), null);
    }

    uiHelper.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

  private Session.StatusCallback callback = new Session.StatusCallback() {
    @Override
    public void call(Session session, SessionState state,
                     Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

  private void onSessionStateChange(Session session, SessionState state,
                                    Exception exception) {

    if (state.isOpened()) {
      firebaseRef.authWithOAuthToken("facebook", session.getAccessToken(), new Firebase.AuthResultHandler() {
        @Override
        public void onAuthenticated(AuthData authData) {
          // The Facebook user is now authenticated with Firebase.
          Map<String, Object> providerData = authData.getProviderData();
          ApplicationState.setLoggedInUserId((String)authData.getProviderData().get("id"));
          facebookProfile = (Map<String, Object>) providerData.get("cachedUserProfile");
          showMatchFragment();
        }
        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
        }
      });
    } else if (state.isClosed()) {
      firebaseRef.unauth();
    }
  }

  private void showMatchFragment() {
    taskCount++;
    if (taskCount != 2) {
      // Wait until we are both logged in and authenticated with firebase.
      return;
    }
    firebaseRef.child("user/" + ApplicationState.getLoggedInUserId()).addListenerForSingleValueEvent(
        new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        if (snapshot.exists()) {
          Fragment matchFragment = new MatchFragment();
          FragmentTransaction transaction = getFragmentManager().beginTransaction();
          transaction.replace(R.id.container, matchFragment);
          transaction.addToBackStack(null);
          transaction.commit();
        } else {
          storeUserProfile();
        }
      }
      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });
  }

  public void storeUserProfile() {
    Session session = Session.getActiveSession();
    new Request(session, "/me/albums", null, HttpMethod.GET, new Request.Callback() {
      public void onCompleted(Response response) {
        try {
          JSONArray albumArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
          for (int i = 0; i < albumArr.length(); i++) {
            JSONObject item = albumArr.getJSONObject(i);
            if (item.getString("type").equals("profile")) {
              new Request(Session.getActiveSession(), "/" + item.getString("id") + "/photos", null, HttpMethod.GET, new Request.Callback() {
                public void onCompleted(Response response) {
                  try {
                    JSONArray photoArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    List<String> pictures = new ArrayList<String>();
                    for (int i = 0; i < photoArr.length(); i++) {
                      JSONObject item = photoArr.getJSONObject(i);
                      String url = item.getJSONArray("images").getJSONObject(0).getString("source");
                      pictures.add(url);
                    }

                    Users user = new Users(facebookProfile, pictures);
                    new FirebaseRef().storeUser(user);

                    Fragment matchFragment = new MatchFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, matchFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }).executeAsync();
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).executeAsync();
  }
}
