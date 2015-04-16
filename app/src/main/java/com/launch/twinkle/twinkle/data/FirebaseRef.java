package com.launch.twinkle.twinkle.data;

import com.firebase.client.Firebase;
import com.launch.twinkle.twinkle.Constants;

public abstract class FirebaseRef {

  private Firebase firebaseRef;

  protected FirebaseRef() {
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
  }

  protected abstract String getTableName();
}
