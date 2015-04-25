package com.launch.twinkle.twinkle;

public class ApplicationState {
  private static String loggedInUserId;

  public static String getLoggedInUserId() {
    return loggedInUserId;
  }
  public static void setLoggedInUserId(String id) {
    loggedInUserId = id;
  }
}
