package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class MessageList extends AbstractModel {

  private Map<String, Boolean> messageIds;

  // Required default constructor for Firebase object mapping
  private MessageList() {
  }

  public MessageList(String id) {
    this.id = id;
  }

  @Override protected String getTableName() {
    return "messageLists";
  }

}
