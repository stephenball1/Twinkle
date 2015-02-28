package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class Chat extends AbstractModel {

  private Map<String, Boolean> userIds;
  private String matchId;
  private String messageListId;

  // Required default constructor for Firebase object mapping
  private Chat() {
  }

  @Override protected String getTableName() {
    return "chats";
  }

  public String getMatchId() { return matchId; }

  public String getMessageListId() { return messageListId; }

  public List<String> getUserIds() { return userIds; }
}
