package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.MessageList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.database.DataSetObserver;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.User;

/**
 * Created by sball on 2/27/15.
 */
public class ChatListFragment extends ListFragment {
  private String mUsername;
  private Firebase mFirebaseRef;
  private ChatListAdapter mListAdapter;
  private LayoutInflater mInflater;
  private ValueEventListener mConnectedListener;
  private String userId;

  static ChatListFragment newInstance() {
    ChatListFragment f = new ChatListFragment();
    Bundle args = new Bundle();
    args.putString("userId", ApplicationState.getLoggedInUserId());
    f.setArguments(args);

    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userId = getArguments() != null ? getArguments().getString("userId") : "ERRRRRRR";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    String messageKey = "users/" + userId + "/chatIds";
    this.mFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(messageKey);
    // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
    final ListView listView = getListView();
    // Tell our list adapter that we only want 50 messages at a time
    mListAdapter = new ChatListAdapter(mFirebaseRef.limitToFirst(50), getActivity(), R.layout.chat_preview);
    listView.setAdapter(mListAdapter);
    mListAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        listView.setSelection(mListAdapter.getCount() - 1);
      }
    });

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String chatId = (String) listView.getItemAtPosition(position);
        ChatPreview chatPreview = mListAdapter.getSecondaryValue(mListAdapter.getItem(position));
        Fragment chatListFragment = null;
        if (chatPreview != null) {
          User user = chatPreview.getInitialUser();
          if (user != null) {
            chatListFragment =
                ChatFragment.newInstance(chatId, user.getFirstName(), user.getLastName());
          } else {
            chatListFragment = ChatFragment.newInstance(chatId);
          }
        } else {
          chatListFragment = ChatFragment.newInstance(chatId);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, chatListFragment);

        transaction.addToBackStack(null);
        transaction.commit();
      }
    });

    // Finally, a little indication of connection status
    mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        boolean connected = (Boolean) dataSnapshot.getValue();
        if (connected) {
          Toast.makeText(getActivity(), "Connected to Firebase", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(getActivity(), "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
        // No-op
      }
    });
  }

  @Override
  public void onStop() {
    super.onStop();
    mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    mListAdapter.cleanup();
  }
}
