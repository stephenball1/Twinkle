package com.launch.twinkle.twinkle;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.Firebase;


public class CircleOfTrustListFragment extends ListFragment {
  private String mUsername;
  private Firebase firebaseRef;
  private Firebase firebaseCirleOfTrustChatIds;
  private CircleOfTrustAdapter circleOfTrustAdapter;
  private View view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUsername = ApplicationState.getLoggedInUserId();
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
    firebaseCirleOfTrustChatIds = firebaseRef.child("circleOfTrustList/" + mUsername + "/" + "circleOfTrustChatId");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat_list, container, false);
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
    final ListView listView = getListView();
    // Tell our list adapter that we only want 50 messages at a time
    circleOfTrustAdapter = new CircleOfTrustAdapter(firebaseCirleOfTrustChatIds.limitToLast(50), getActivity(), R.layout.chat_preview, ApplicationState.getLoggedInUserId());
    listView.setAdapter(circleOfTrustAdapter);
    circleOfTrustAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        listView.setSelection(circleOfTrustAdapter.getCount() - 1);
      }
    });
  }

  @Override
  public void onStop() {
    super.onStop();
    circleOfTrustAdapter.cleanup();
  }
}
