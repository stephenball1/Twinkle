package com.launch.twinkle.twinkle.models;

import com.firebase.client.Firebase;
import com.launch.twinkle.twinkle.Constants;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRef {

  private Firebase firebaseRef;
  private String id;

  public FirebaseRef() {
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
    id = (String) firebaseRef.getAuth().getProviderData().get("id");
  }

  public void storeUser(Users user) {
    Map<String, Users> users = new HashMap<String, Users>();
    users.put(id, user);
    firebaseRef.child("user").setValue(users);
  }
}
