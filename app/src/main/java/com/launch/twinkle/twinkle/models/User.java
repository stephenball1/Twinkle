package com.launch.twinkle.twinkle.models;

public class User {

  private String id; // This is actually the facebook-id
  private String firstName;
  private String lastName;

  // Required default constructor for Firebase object mapping
  private User() {
  }

  public String getLastName() {
    return lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getId() {
    return id;
  }

}
