package com.launch.twinkle.twinkle;

import com.firebase.client.Firebase;

public class ApplicationState {
  public static String getLoggedInUserId() {
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);
    return (String) firebaseRef.getAuth().getProviderData().get("id");
  }
}
