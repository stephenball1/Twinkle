package com.launch.twinkle.twinkle.models;

public class User extends AbstractModel {

  private String firstName;
  private String lastName;

  public User(String id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
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

  @Override protected String getTableName() {
    return "users";
  }

}
