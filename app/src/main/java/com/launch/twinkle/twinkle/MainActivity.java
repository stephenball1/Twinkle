package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.*;

import android.app.Activity;
import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
  private MainFragment mainFragment;
  private Firebase firebaseRef;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      // Add the fragment on initial activity setup
      mainFragment = new MainFragment();
      getSupportFragmentManager()
              .beginTransaction()
              .add(R.id.container, mainFragment)
              .commit();
    } else {
      // Or set the fragment from restored state info
      mainFragment = (MainFragment) getSupportFragmentManager()
              .findFragmentById(R.id.container);
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
    if (id == R.id.action_heart) {
      System.out.println("action heart");
      Fragment matchFragment = new MatchFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(android.R.id.content, matchFragment);
      transaction.addToBackStack(null);
      transaction.commit();
      return true;
    } else if (id == R.id.action_chat) {
      Fragment chatListFragment = ChatListFragment.newInstance();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, chatListFragment);

      transaction.addToBackStack(null);
      transaction.commit();

      return true;
    } else if (id == R.id.action_setting) {
      // TODO(judy): setting;
      System.out.println("action setting");
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
