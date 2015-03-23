package com.launch.twinkle.twinkle.models;

public class Message extends AbstractModel {
  public static String tableName = "messages";
  private String id;
  private String message;
  private String userId;
  private long timestamp;

  // Required default constructor for Firebase object mapping
  private Message() {
  }

  public Message(String id, String userId, String message) {
    this.id = id;
    this.userId = userId;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  protected String getTableName() {
    return Message.tableName;
  }

  public String getUserId() {
    return userId;
  }

  public String getMessage() {
    return message;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
