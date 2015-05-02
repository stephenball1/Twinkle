package com.launch.twinkle.twinkle.models;

/**
 * Created by judymou on 5/2/15.
 */
public class CircleOfTrustChatId {
  private String key;

  // Required default constructor for Firebase object mapping
  @SuppressWarnings("unused")
  private CircleOfTrustChatId() {
  }

  public CircleOfTrustChatId(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
