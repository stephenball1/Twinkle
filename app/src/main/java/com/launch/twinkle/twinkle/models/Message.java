package com.launch.twinkle.twinkle.models;

public class Message {

  private String id;
  private String message;
  private String userId;

  // Required default constructor for Firebase object mapping
  private Message() {
  }

  public String getUserId() { return userId; }

  public String getMessage() { return message; }
}
