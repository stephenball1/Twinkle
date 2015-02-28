package com.launch.twinkle.twinkle;

import com.firebase.client.Firebase;

public class TwinkleApplication extends android.app.Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Firebase.setAndroidContext(this);
  }
}
