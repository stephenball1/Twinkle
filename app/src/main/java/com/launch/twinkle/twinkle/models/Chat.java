package com.launch.twinkle.twinkle.models;

import java.util.List;

public class Chat extends AbstractModel {

  private List<String> userIds;
  private String matchId;
  private String messageListId;

  // Required default constructor for Firebase object mapping
  private Chat() {
  }

  @Override protected String getTableName() {
    return "chats";
  }

}
