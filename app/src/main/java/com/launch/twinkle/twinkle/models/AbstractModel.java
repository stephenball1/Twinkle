package com.launch.twinkle.twinkle.models;

import com.launch.twinkle.twinkle.Constants;

import com.firebase.client.Firebase;

public abstract class AbstractModel {

  protected String id;

  // Required default constructor for Firebase object mapping
  protected AbstractModel() {
  }

  public void set(String key, Object value) {
    String attrKey = getTableName() + "/" + id + "/" + key;
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL).child(attrKey);
    firebaseRef.setValue(value);
  }

  protected abstract String getTableName();

}
