package com.launch.twinkle.twinkle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
  private MainFragment mainFragment;

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
    int id = item.getItemId();

    if (id == R.id.action_heart) {
      Fragment matchFragment = new MatchFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, matchFragment);
      transaction.addToBackStack(null);
      transaction.commit();
      return true;
    } else if (id == R.id.circle_of_trust) {
      getActionBar().setTitle("Circles of trust");
      Fragment CircleOfTrustListFragment = new CircleOfTrustListFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, CircleOfTrustListFragment);
      transaction.addToBackStack(null);
      transaction.commit();
      return true;
    } else if (id == R.id.action_chat) {
      getActionBar().setTitle("Past Matches");
      Fragment pastMatchChatListFragment = new PastMatchChatListFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, pastMatchChatListFragment);
      transaction.addToBackStack(null);
      transaction.commit();
      return true;
    } else if (id == R.id.action_setting) {
      getActionBar().setTitle("Settings");
      Fragment settingFragment = new SettingFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.container, settingFragment);
      transaction.addToBackStack(null);
      transaction.commit();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
