package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.User;

import android.graphics.Bitmap;

/**
 * Created by sball on 2/28/15.
 */
public class ChatPreview {
  private Message lastMessage;
  private User matchedUser;
  private User initialUser;
  private Bitmap matchedUserBitmap;
  private Bitmap initialUserBitmap;

  public ChatPreview() {
  }

  public ChatPreview(Message message) {
    this.lastMessage = message;
  }

  public User getInitialUser() {
    return initialUser;
  }

  public void setInitialUser(User u) {
    initialUser = u;
  }

  public User getMatchedUser() {
    return matchedUser;
  }

  public void setMatchedUser(User u) {
    matchedUser = u;
  }

  public Bitmap getMatchedUserBitmap() {
    return matchedUserBitmap;
  }

  public void setMatchedUserBitmap(Bitmap b) {
    matchedUserBitmap = b;
  }

  public Bitmap getInitialUserBitmap() {
    return initialUserBitmap;
  }

  public void setInitialUserBitmap(Bitmap b) {
    initialUserBitmap = b;
  }

  public Message getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(Message m) {
    lastMessage = m;
  }

}
