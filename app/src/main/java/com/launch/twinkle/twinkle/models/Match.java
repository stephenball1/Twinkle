package com.launch.twinkle.twinkle.models;

import java.util.List;

public class Match {

  private String id;
  private List<String> userIds;

  // Required default constructor for Firebase object mapping
  private Match() {
  }

  public String getId() {
    return id;
  }

}
