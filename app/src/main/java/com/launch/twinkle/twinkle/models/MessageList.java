package com.launch.twinkle.twinkle.models;

import java.util.Map;
import java.util.Set;

public class MessageList extends AbstractModel {
  public static String tableName = "messageLists";

  private Map<String, String> messageIds;

  // Required default constructor for Firebase object mapping
  private MessageList() {
  }

  public MessageList(String id) {
    this.id = id;
  }

  @Override protected String getTableName() {
    return MessageList.tableName;
  }

  public Set<String> getMessageIds() {
    if (messageIds != null) messageIds.keySet();
    return null;
  }
}
