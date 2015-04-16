package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class Users {
  private String firstName;
  private String lastName;
  private String birthday;

  public Users() {
  }

  public Users(Map<String, Object> facebookProfile) {
    firstName = (String) facebookProfile.get("first_name");
    lastName = (String) facebookProfile.get("last_name");
    birthday = (String) facebookProfile.get("birthday");
  }

  public String getBirthday() {
    return birthday;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
