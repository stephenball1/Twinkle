package com.launch.twinkle.twinkle;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.launch.twinkle.twinkle.models.PastMatchChatSummary;


public class PastMatchChatListFragment extends ListFragment {
  private String mUsername;
  private Firebase firebaseRef;
  private Firebase firebasePastMatchChat;
  private PastMatchChatAdapter pastMatchChatAdapter;
  private View view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUsername = ApplicationState.getLoggedInUserId();
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
    firebasePastMatchChat = firebaseRef.child("pastMatchesChatList/" + mUsername + "/" + "pastMatchChatSummary");
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
    pastMatchChatAdapter = new PastMatchChatAdapter(firebasePastMatchChat.limitToLast(50), getActivity(), R.layout.past_match_chat_preview, ApplicationState.getLoggedInUserId());
    listView.setAdapter(pastMatchChatAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PastMatchChatSummary pastMatchChatSummary = (PastMatchChatSummary) pastMatchChatAdapter.getItem(position);
        ApplicationState.setTopicUser(pastMatchChatSummary.getMatchId());
        ApplicationState.setChatRoomId(pastMatchChatSummary.getChatRoomId());
        getActivity().getActionBar().setTitle("Past Matches");
        Fragment chatListFragment = new ChatListFragment2();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, chatListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
      }
    });
    pastMatchChatAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        listView.setSelection(pastMatchChatAdapter.getCount() - 1);
      }
    });
  }

  @Override
  public void onStop() {
    super.onStop();
    pastMatchChatAdapter.cleanup();
  }
}
