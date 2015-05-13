package com.launch.twinkle.twinkle.models;

import com.launch.twinkle.twinkle.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Users {
  private String firstName;
  private String lastName;
  private String birthday;
  private int age;
  private String gender;
  private String profileUrl;
  private List<String> pictures;
  private String description;
  private int agePreferenceLow;
  private int agePreferenceHigh;
  private int distancePreferenceMiles;
  private boolean interestedInWomen;
  private boolean interestedInMen;

  public Users() {
  }

  public Users(Map<String, Object> facebookProfile, List<String> p) {

    firstName = (String) facebookProfile.get("first_name");
    lastName = (String) facebookProfile.get("last_name");
    birthday = (String) facebookProfile.get("birthday");
    age = calculateAge();
    gender = (String) facebookProfile.get("gender");
    profileUrl = Utils.getProfileUrl((String) facebookProfile.get("id"));
    pictures = p;
    description = "";
    agePreferenceLow = age - 10;
    agePreferenceHigh = age + 20;
    distancePreferenceMiles = 20;
    if (gender.equals("male")) {
      interestedInWomen = true;
      interestedInMen = false;
    } else if (gender.equals("female")) {
      interestedInWomen = false;
      interestedInMen = true;
    } else {
      interestedInWomen = true;
      interestedInMen = true;
    }
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

  public List<String> getPictures() {
    return pictures;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String d) {
    description = d;
  }

  public void setPictures(List<String> p) {
    pictures = p;
  }

  public int getAgePreferenceLow() {
    return agePreferenceLow;
  }

  public void setAgePreferenceLow(int agePreferenceLow) {
    this.agePreferenceLow = agePreferenceLow;
  }

  public int getAgePreferenceHigh() {
    return agePreferenceHigh;
  }

  public void setAgePreferenceHigh(int agePreferenceHigh) {
    this.agePreferenceHigh = agePreferenceHigh;
  }

  public int getDistancePreferenceMiles() {
    return distancePreferenceMiles;
  }

  public void setDistancePreferenceMiles(int distancePreferenceMiles) {
    this.distancePreferenceMiles = distancePreferenceMiles;
  }

  public boolean isInterestedInWomen() {
    return interestedInWomen;
  }

  public void setInterestedInWomen(boolean interestedInWomen) {
    this.interestedInWomen = interestedInWomen;
  }

  public boolean isInterestedInMen() {
    return interestedInMen;
  }

  public void setInterestedInMen(boolean interestedInMen) {
    this.interestedInMen = interestedInMen;
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
