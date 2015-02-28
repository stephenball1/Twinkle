package com.launch.twinkle.twinkle.models;

import com.launch.twinkle.twinkle.Constants;

import com.firebase.client.Firebase;

public abstract class AbstractModel {

  protected String id;

  // Required default constructor for Firebase object mapping
  protected AbstractModel() {
  }

  public String getId() {
    return id;
  }

  public void create() {
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);
    firebaseRef = firebaseRef.child(getTableName()).push();

    this.id = firebaseRef.getKey();
    firebaseRef.setValue(this);
  }

  public void save() {
    String modelKey = getTableName() + "/" + id;
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL).child(modelKey);
    firebaseRef.setValue(this);
  }

  public void set(String key, Object value) {
    String attrKey = getTableName() + "/" + id + "/" + key;
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL).child(attrKey);
    firebaseRef.setValue(value);
  }

  public void pushToArrayAttr(String key, String value) {
    String attrKey = getTableName() + "/" + id + "/" + key;
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL).child(attrKey);
    firebaseRef.child(value).setValue(true);
  }

  public void removeFromArrayAttr(String key, String value) {
    String attrKey = getTableName() + "/" + id + "/" + key;
    Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL).child(attrKey);
    firebaseRef.child(value).removeValue();
  }

  protected abstract String getTableName();

}
