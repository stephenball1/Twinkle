package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.net.URL;

public class PictureLoaderTask extends AsyncTask<Object, Void, Bitmap> {

  private ImageView view;

  @Override
  protected Bitmap doInBackground(Object... args) {
    String url = (String)args[0];
    view = (ImageView)args[1];

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
    view.setImageBitmap(bitmap);
  }

}
