package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;

import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.User;

/**
 * Created by sball on 3/22/15.
 */
public class MessageWithUser {
  private Message message;
  private User user;
  private Bitmap userImage;

  public MessageWithUser(Message message, User user) {
    this.message = message;
    this.user = user;
  }

  public MessageWithUser(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Bitmap getUserImage() {
    return userImage;
  }

  public void setUserImage(Bitmap userImage) {
    this.userImage = userImage;
  }
}
