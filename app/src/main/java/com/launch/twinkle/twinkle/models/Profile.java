package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class Profile extends AbstractModel {

  private Map<String, String> pictureUrls;

  // Required default constructor for Firebase object mapping
  private Profile() {
  }

  public Profile(String id) {
    this.id = id;
  }

  @Override protected String getTableName() {
    return "profiles";
  }

}
