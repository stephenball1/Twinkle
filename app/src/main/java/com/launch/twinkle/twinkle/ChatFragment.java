package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.MessageList;

import android.support.v4.app.Fragment;
import android.database.DataSetObserver;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by sball on 2/27/15.
 */
public class ChatFragment extends ListFragment {
  private String mUsername;
  private Firebase mFirebaseRef;
  private MessageListAdapter mMessageListAdapter;
  private LayoutInflater mInflater;
  private ValueEventListener mConnectedListener;
  private String chatId;

  static ChatFragment newInstance(String chatId) {
    ChatFragment f = new ChatFragment();
    Bundle args = new Bundle();
    args.putString("chatId", chatId);
    f.setArguments(args);

    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    chatId = getArguments() != null ? getArguments().getString("chatId") : "";
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.chat_fragment, container, false);

    view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createMessage();
      }
    });

    return view;
  }

  private void createMessage() {
    EditText input = (EditText) getView().findViewById(R.id.messageInput);
    String value = input.getText().toString();
    if (!value.equals("")) {
      Message message = new Message("", ApplicationState.getLoggedInUserId(), value);
      message.create();

      // Add it to this list
      MessageList messageList = new MessageList(chatId);
      messageList.pushToChildList("messageIds", message.getId());

      input.setText("");
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    // TODO plug this in
    String messageKey = "messagesList/" + chatId;
    this.mFirebaseRef = new Firebase(Constants.FIREBASE_URL).child("messages");
    mUsername = "TEMP";
    // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
    final ListView listView = getListView();
    // Tell our list adapter that we only want 50 messages at a time
    mMessageListAdapter = new MessageListAdapter(mFirebaseRef.limit(50), getActivity(), R.layout.chat_message, mUsername);
    listView.setAdapter(mMessageListAdapter);
    mMessageListAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        listView.setSelection(mMessageListAdapter.getCount() - 1);
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
    mMessageListAdapter.cleanup();
  }
}
