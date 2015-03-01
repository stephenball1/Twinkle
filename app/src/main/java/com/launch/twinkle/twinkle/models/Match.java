package com.launch.twinkle.twinkle.models;

import java.util.Map;

public class Match extends AbstractModel {

  private Map<String, String> userIds;

  // Required default constructor for Firebase object mapping
  private Match() {
  }

  @Override protected String getTableName() {
    return "matches";
  }

}
