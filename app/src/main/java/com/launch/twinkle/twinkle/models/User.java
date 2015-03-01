package com.launch.twinkle.twinkle.models;

import java.util.HashMap;
import java.util.Map;

public class User extends AbstractModel {

  private String firstName;
  private String lastName;
  private String profilePictureUrl;
  private String matchId;
  private String birthday = "03/11/1980";
  private Map<String, String> chatIds;

  // Required default constructor for Firebase object mapping
  protected User() {
  }

  public User(String id) {
    this.id = id;
  }

  public User(String id, Map<String, Object> facebookProfile) {
    this.id = id;
    firstName = (String) facebookProfile.get("first_name");
    lastName = (String) facebookProfile.get("last_name");
    birthday = (String) facebookProfile.get("birthday");
  }

  public User(String id, String firstName, String lastName, String profilePictureUrl,
      String matchId) {

    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.profilePictureUrl = profilePictureUrl;
    this.matchId = matchId;
  }

  public String getDisplayName() {
    return firstName + " " + lastName.charAt(0);
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

  public String getMatchId() {
    return matchId;
  }

  public String getBirthday() {
    return birthday;
  }

  public Map<String, String> getChatIds() {
    if (chatIds != null) {
      return chatIds;
    } else {
      return new HashMap<String, String>();
    }
  }

  public void updateInfo() {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("id", id);
    values.put("firstName", firstName);
    values.put("lastName", lastName);
    values.put("birthday", birthday);
    update(values);
  }

  @Override protected String getTableName() {
    return "users";
  }

}
