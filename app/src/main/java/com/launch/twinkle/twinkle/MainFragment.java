package com.launch.twinkle.twinkle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.launch.twinkle.twinkle.models.FirebaseRef;
import com.launch.twinkle.twinkle.models.Users;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {
  private static final String TAG = MainFragment.class.getSimpleName();

  private UiLifecycleHelper uiHelper;
  private TextView username;
  private LoginButton authButton;
  private final List<String> permissions;

  public MainFragment() {
    permissions = Arrays.asList("public_profile,user_birthday,email,user_friends,user_photos,user_status");
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

    authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setBackgroundColor(Color.parseColor("#E91E63"));
    authButton.setAllCaps(false);
    authButton.setFragment(this);
    authButton.setReadPermissions(permissions);

    authButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
      @Override
      public void onUserInfoFetched(GraphUser user) {

        if (user != null) {
          getActivity().getActionBar().show();
          //username.setText("You are currently logged in as " + user.getName());
          // Create new fragment and transaction.
/*
                ProfileSetupFragment profileFragment = new ProfileSetupFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, profileFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
*/


        } else {
          username.setText("");
        }
      }
    });

    username = (TextView) view.findViewById(R.id.username);

    getActivity().getActionBar().hide();
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

  private void onSessionStateChange(Session session, SessionState state,
                                    Exception exception) {
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);

    if (state.isOpened()) {
      firebaseRef.authWithOAuthToken("facebook", session.getAccessToken(), new Firebase.AuthResultHandler() {
        @Override
        public void onAuthenticated(AuthData authData) {
          // The Facebook user is now authenticated with Firebase.
          Map<String, Object> providerData = authData.getProviderData();
          Map<String, Object> facebookProfile = (Map<String, Object>) providerData.get("cachedUserProfile");
          //User user = new User((String) providerData.get("id"), facebookProfile);
          //user.updateInfo();
          Users user = new Users(facebookProfile);
          FirebaseRef firebaseRefWrapper = new FirebaseRef();
          firebaseRefWrapper.storeUser(user);

          ApplicationState.setLoggedInUserId(firebaseRefWrapper.getId());
          // TODO(judymou): Add a blocking task to load bitmaps into the cache.

          Fragment matchFragment = new MatchFragment();
          FragmentTransaction transaction = getFragmentManager().beginTransaction();
          transaction.replace(R.id.container, matchFragment);
          transaction.addToBackStack(null);
          transaction.commit();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
          // there was an error.
        }
      });

    } else if (state.isClosed()) {
      firebaseRef.unauth();
    }
  }

  private Session.StatusCallback callback = new Session.StatusCallback() {
    @Override
    public void call(Session session, SessionState state,
                     Exception exception) {
      onSessionStateChange(session, state, exception);
    }
  };

}
