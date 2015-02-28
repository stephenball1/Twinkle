package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.*;

import android.app.Activity;
import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.facebook.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends FragmentActivity {
  private static final String FIREBASE_URL =
      "https://blinding-fire-9025.firebaseio.com/";

  private MainFragment mainFragment;
  private Firebase firebaseRef;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    User user = new User("more-fake", "Alan", "Turing");
    user.save();

    firebaseRef = new Firebase(Constants.FIREBASE_URL).child("Holman");
    firebaseRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        System.out.println(snapshot.getValue());
      }

      @Override public void onCancelled(FirebaseError error) { }
    });

    if (savedInstanceState == null) {
      // Add the fragment on initial activity setup
      mainFragment = new MainFragment();
      getSupportFragmentManager()
              .beginTransaction()
              .add(android.R.id.content, mainFragment)
              .commit();
    } else {
      // Or set the fragment from restored state info
      mainFragment = (MainFragment) getSupportFragmentManager()
              .findFragmentById(android.R.id.content);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
