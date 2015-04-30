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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.Chat2;
import com.launch.twinkle.twinkle.models.Users;


public class ChatListFragment2 extends ListFragment {
  private String mUsername;
  private String chatRoomId;
  private Firebase firebaseRefMessage;
  private Firebase firebaseRef;
  private ChatListAdapter2 mListAdapter;
  private View view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUsername = ApplicationState.getLoggedInUserId();
    chatRoomId = ApplicationState.getChatRoomId();
    firebaseRef = new Firebase(Constants.FIREBASE_URL);
    firebaseRefMessage = new Firebase(Constants.FIREBASE_URL).child("chat/" + chatRoomId + "/" + "messages");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.chat_fragment, container, false);

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
    view.findViewById(R.id.match_snippet).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO(judymou): Link to profile view.
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
    mListAdapter = new ChatListAdapter2(firebaseRefMessage.limit(50), getActivity(), R.layout.chat_message, ApplicationState.getLoggedInUserId());
    listView.setAdapter(mListAdapter);
    mListAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        System.out.println("mListAdapter.getCount()" + mListAdapter.getCount());
        listView.setSelection(mListAdapter.getCount() - 1);
      }
    });
    final View finalView = view;
    firebaseRef.child("chat/" + chatRoomId + "/topicuser").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        String topicUserId = (String) snapshot.getValue();
        firebaseRef.child("user/" + topicUserId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            Users topicUser = snapshot.getValue(Users.class);
            ((TextView) finalView.findViewById(R.id.match_name)).setText(topicUser.getFirstName());
            ((TextView) finalView.findViewById(R.id.match_age)).setText(topicUser.getAge() + " yrs old");
            new PictureLoaderTask(new BitmapRunnable() {
              public void run() {
                ImageView image = (ImageView) finalView.findViewById(R.id.snippet_profile_picture);
                image.setImageBitmap(getBitmap());
              }
            }).execute(topicUser.getProfileUrl());
          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {
          }
        });
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
      }
    });

    firebaseRef.child("user/" + mUsername).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        Users user = snapshot.getValue(Users.class);
        getActivity().getActionBar().setTitle(user.getFirstName());
      }
      @Override
      public void onCancelled(FirebaseError firebaseError) {
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
      firebaseRefMessage.push().setValue(chat);
      inputText.setText("");
    }
  }
}
