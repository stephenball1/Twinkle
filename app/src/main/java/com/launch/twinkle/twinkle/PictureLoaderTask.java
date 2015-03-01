package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.net.URL;

public class PictureLoaderTask extends AsyncTask<String, Void, Bitmap> {

  private ImageView view;
  private BitmapRunnable bitmapRunnable;

  public PictureLoaderTask(BitmapRunnable bitmapRunnable) {
    this.bitmapRunnable = bitmapRunnable;
  }
  @Override
  protected Bitmap doInBackground(String... args) {
    String url = (String)args[0];

    try {
      URL imageURL = new URL(url);
      return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    if (bitmapRunnable != null) {
      bitmapRunnable.setBitmap(bitmap);
      bitmapRunnable.run();
    }
  }

}
