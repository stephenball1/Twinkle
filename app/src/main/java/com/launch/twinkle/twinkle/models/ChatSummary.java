package com.launch.twinkle.twinkle.models;

/**
 * Created by judymou on 5/2/15.
 */
public class ChatSummary {
  private String friend;
  private String friendMatch;
  private String chatRoomId;

  // Required default constructor for Firebase object mapping
  @SuppressWarnings("unused")
  private ChatSummary() {
  }

  public ChatSummary(String friend, String friendMatch, String chatRoomId) {
    this.friend = friend;
    this.friendMatch = friendMatch;
    this.chatRoomId = chatRoomId;
  }

  public String getFriend() {
    return friend;
  }

  public void setFriend(String friend) {
    this.friend = friend;
  }

  public String getFriendMatch() {
    return friendMatch;
  }

  public void setFriendMatch(String friendMatch) {
    this.friendMatch = friendMatch;
  }

  public String getChatRoomId() {
    return chatRoomId;
  }

  public void setChatRoomId(String chatRoomId) {
    this.chatRoomId = chatRoomId;
  }
}
