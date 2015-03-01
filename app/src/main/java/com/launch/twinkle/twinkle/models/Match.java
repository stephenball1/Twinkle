package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class Match extends AbstractModel {

  private String matchedUserId;

  // Required default constructor for Firebase object mapping
  private Match() {
  }

  public String getMatchedUserId() {
    return matchedUserId;
  }

  @Override protected String getTableName() {
    return "matches";
  }

}
