package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class MessageList extends AbstractModel {

  private Map<String, Boolean> messageIds;

  // Required default constructor for Firebase object mapping
  private MessageList() {
  }

  @Override protected String getTableName() {
    return "messageLists";
  }

}
