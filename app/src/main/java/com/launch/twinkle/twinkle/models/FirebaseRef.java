package com.launch.twinkle.twinkle.models;

import com.firebase.client.Firebase;
import com.launch.twinkle.twinkle.Constants;

public class FirebaseRef {

  private Firebase firebaseRef;
  private String id;

  public FirebaseRef() {
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
    id = (String) firebaseRef.getAuth().getProviderData().get("id");
  }

  public void storeUser(Users user) {
    firebaseRef.child("user/" + id).setValue(user);
  }

  public String getId() {
    return id;
  }
}
