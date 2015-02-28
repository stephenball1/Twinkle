package com.launch.twinkle.twinkle.models;

import java.util.HashMap;
import java.util.Map;

public class User extends AbstractModel {

  private String firstName;
  private String lastName;
  private String profilePictureUrl;

  public User(String id) {
    this.id = id;
  }

  public User(String id, Map<String, String> facebookProfile) {
    this.id = id;
    firstName = facebookProfile.get("first_name");
    lastName = facebookProfile.get("last_name");
  }

  public String getLastName() {
    return lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  public void updateInfo() {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("firstName", firstName);
    values.put("lastName", lastName);
    update(values);
  }

  @Override protected String getTableName() {
    return "users";
  }

}
