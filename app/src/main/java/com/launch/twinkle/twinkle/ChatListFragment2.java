package com.launch.twinkle.twinkle;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.launch.twinkle.twinkle.models.Chat2;


public class ChatListFragment2 extends ListFragment {
  private String mUsername;
  private Firebase mFirebaseRef;
  private ChatListAdapter2 mListAdapter;
  private View view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseRef = new Firebase(Constants.FIREBASE_URL).child("chat");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat_list2, container, false);

    // Setup our input methods. Enter key on the keyboard or pushing the send button
    EditText inputText = (EditText) view.findViewById(R.id.messageInput);
    inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
          sendMessage();
        }
        return true;
      }
    });
    view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        sendMessage();
      }
    });
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
    final ListView listView = getListView();
    // Tell our list adapter that we only want 50 messages at a time
    mListAdapter = new ChatListAdapter2(mFirebaseRef.limit(50), getActivity(), R.layout.chat_message2, ApplicationState.getLoggedInUserId());
    listView.setAdapter(mListAdapter);
    mListAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        System.out.println("mListAdapter.getCount()" + mListAdapter.getCount());
        listView.setSelection(mListAdapter.getCount() - 1);
      }
    });
  }

  @Override
  public void onStop() {
    super.onStop();
    mListAdapter.cleanup();
  }

  private void sendMessage() {
    EditText inputText = (EditText) view.findViewById(R.id.messageInput);
    String input = inputText.getText().toString();
    if (!input.equals("")) {
      // Create our 'model', a Chat object
      Chat2 chat = new Chat2(input, mUsername);
      // Create a new, auto-generated child of that chat location, and save our chat data there
      mFirebaseRef.push().setValue(chat);
      inputText.setText("");
    }
  }
}
