package com.launch.twinkle.twinkle.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Users {
  private String firstName;
  private String lastName;
  private String birthday;
  private int age;
  private String gender;
  private String profileUrl;

  public Users() {
  }

  public Users(Map<String, Object> facebookProfile) {

    firstName = (String) facebookProfile.get("first_name");
    lastName = (String) facebookProfile.get("last_name");
    birthday = (String) facebookProfile.get("birthday");
    age = calculateAge();
    gender = (String) facebookProfile.get("gender");
    profileUrl = "https://graph.facebook.com/" + facebookProfile.get("id") + "/picture?type=large";
/*
    firstName = "Tuling";
    lastName = "Ma";
    birthday = "10/01/1990";
    age = calculateAge();
    gender = "male";
*/
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

  public int getAge() {
    return age;
  }

  public String getGender() {
    return gender;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  private int calculateAge() {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    try {
      Date birthday = sdf.parse(getBirthday());
      Date today = new Date();
      return today.getYear() - birthday.getYear();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return -1;
  }
}
