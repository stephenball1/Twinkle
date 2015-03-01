package com.launch.twinkle.twinkle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

import java.util.List;

public class PickFBFriendsActivity extends FragmentActivity {
  FriendPickerFragment friendPickerFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActionBar().setTitle("Build Your Circle");
    setContentView(R.layout.activity_pick_fbfriends);

    FragmentManager fm = getSupportFragmentManager();

    if (savedInstanceState == null) {
      // First time through, we create our fragment programmatically.

      Bundle bundle = new Bundle();
      bundle.putBoolean(FriendPickerFragment.MULTI_SELECT_BUNDLE_KEY, true);
      bundle.putBoolean(FriendPickerFragment.SHOW_TITLE_BAR_BUNDLE_KEY, false);

      Bundle args = getIntent().getExtras();
      friendPickerFragment = new FriendPickerFragment(args);
      fm.beginTransaction()
          .replace(R.id.friend_picker_fragment, friendPickerFragment)
          .commit();
    } else {
      friendPickerFragment = (FriendPickerFragment) fm.findFragmentById(R.id.friend_picker_fragment);
    }

    friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
      @Override
      public void onError(PickerFragment<?> fragment, FacebookException error) {
        PickFBFriendsActivity.this.onError(error);
      }
    });

    friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
      @Override
      public void onDoneButtonClicked(PickerFragment<?> fragment) {
        List<GraphUser> selectedUsers = ((FriendPickerFragment)fragment).getSelection();
        for (GraphUser selectedUser : selectedUsers) {
          // TODO(holman): store circle of trust user id.
          System.out.println("selectedUser: " + selectedUser.getId());
        }

        setResult(RESULT_OK, null);
        finish();
      }
    });
  }

  private void onError(Exception error) {
    System.out.println("Friend picker doesn't work");
  }


  @Override
  protected void onStart() {
    super.onStart();
    friendPickerFragment.loadData(false);
  }

}
