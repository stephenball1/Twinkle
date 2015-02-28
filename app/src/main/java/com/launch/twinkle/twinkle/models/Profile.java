package com.launch.twinkle.twinkle.models;

public class Profile extends AbstractModel {

  // Required default constructor for Firebase object mapping
  private Profile() {
  }

  @Override protected String getTableName() {
    return "profiles";
  }

}
