package com.launch.twinkle.twinkle.models;

public class Profile extends AbstractModel {

  private String userId;

  // Required default constructor for Firebase object mapping
  private Profile() {
  }

  @Override protected String getTableName() {
    return "profiles";
  }

}
