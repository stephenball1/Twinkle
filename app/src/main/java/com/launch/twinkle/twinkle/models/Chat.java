package com.launch.twinkle.twinkle.models;

import java.util.List;

public class Chat {

  private String id;
  private List<String> userIds;
  private String matchId;
  private String messageListId;

  // Required default constructor for Firebase object mapping
  private Chat() {
  }

  public String getId() {
    return id;
  }

  public String getMatchId() { return matchId; }

  public String getMessageListId() { return messageListId; }

  public List<String> getUserIds() { return userIds; }
}
