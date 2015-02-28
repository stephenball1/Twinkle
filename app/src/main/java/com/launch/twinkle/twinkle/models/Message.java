package com.launch.twinkle.twinkle.models;

public class Message extends AbstractModel {

  private String id;
  private String message;
  private String userId;

  // Required default constructor for Firebase object mapping
  private Message() {
  }

  public Message(String id, String userId, String message) {
    this.id = id;
    this.userId = userId;
    this.message = message;
  }

  @Override
  protected String getTableName() {
    return "messages";
  }

  public String getUserId() { return userId; }

  public String getMessage() { return message; }
}
