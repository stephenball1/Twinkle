package com.launch.twinkle.twinkle.models;

public class Message extends AbstractModel {

  private String message;
  private String userId;

  // Required default constructor for Firebase object mapping
  private Message() {
  }

  @Override protected String getTableName() {
    return "messages";
  }

}
