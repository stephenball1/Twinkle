package com.launch.twinkle.twinkle.models;

public class PastMatchChatSummary {
  private String matchId;
  private String chatRoomId;
  private long matchedTimeMilli;

  public PastMatchChatSummary() {
  }

  public PastMatchChatSummary(String matchId, String chatRoomId, long matchedTimeMilli) {
    this.matchId = matchId;
    this.chatRoomId = chatRoomId;
    this.matchedTimeMilli = matchedTimeMilli;
  }

  public String getMatchId() {
    return matchId;
  }

  public void setMatchId(String matchId) {
    this.matchId = matchId;
  }

  public String getChatRoomId() {
    return chatRoomId;
  }

  public void setChatRoomId(String chatRoomId) {
    this.chatRoomId = chatRoomId;
  }

  public long getMatchedTimeMilli() {
    return matchedTimeMilli;
  }

  public void setMatchedTimeMilli(long matchedTimeMilli) {
    this.matchedTimeMilli = matchedTimeMilli;
  }
}
