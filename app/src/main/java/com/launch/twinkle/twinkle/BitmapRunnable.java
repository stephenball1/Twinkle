package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;

/**
 * Created by sball on 2/28/15.
 */
public abstract class BitmapRunnable implements Runnable {
  private Bitmap bitmap;

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }
}
