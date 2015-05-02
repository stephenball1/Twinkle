package com.launch.twinkle.twinkle;

public class ApplicationState {
  private static String loggedInUserId;
  private static String chatRoomId;
  private static String topicUser;

  public static String getLoggedInUserId() {
    return loggedInUserId;
  }

  public static void setLoggedInUserId(String id) {
    loggedInUserId = id;
  }

  public static String getChatRoomId() {
    //return chatRoomId;
    return "testchatroom1";
  }

  public static void setChatRoomId(String id) {
    chatRoomId = id;
  }

  public static String getTopicUser() {
    return "10155209831365591";
  }

  public static void setTopicUser(String id) {
    topicUser = id;
  }
}
