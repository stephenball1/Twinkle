package com.launch.twinkle.twinkle.models;

import java.util.List;

public class Match extends AbstractModel {

  private List<String> userIds;

  // Required default constructor for Firebase object mapping
  private Match() {
  }

  @Override protected String getTableName() {
    return "matches";
  }

}
