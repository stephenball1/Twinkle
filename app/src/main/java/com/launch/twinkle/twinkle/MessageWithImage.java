package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;

import com.launch.twinkle.twinkle.models.Message;

/**
 * Created by sball on 2/28/15.
 */
public class MessageWithImage {
  private Message message;
  private Bitmap bitmap;

  public MessageWithImage(Message message, Bitmap bitmap) {
    this.message = message;
    this.bitmap = bitmap;
  }

  public MessageWithImage(Message message) {
    this(message, null);
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

}