package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Message;
import com.launch.twinkle.twinkle.models.MessageList;
import com.launch.twinkle.twinkle.models.User;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.database.DataSetObserver;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
  private View view;

  static ChatFragment newInstance(String chatId) {
    ChatFragment f = new ChatFragment();
    Bundle args = new Bundle();
    args.putString("chatId", chatId);
    args.putString("firstName", "");
    args.putString("lastName", "");
    f.setArguments(args);

    return f;
  }

  static ChatFragment newInstance(String chatId, String firstName, String lastName) {
    ChatFragment f = new ChatFragment();
    Bundle args = new Bundle();
    args.putString("chatId", chatId);
    args.putString("firstName", firstName);
    args.putString("lastName", lastName);
    f.setArguments(args);

    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      chatId = getArguments().getString("chatId");
      String firstName = getArguments().getString("firstName");
      String lastName = getArguments().getString("lastName");
      getActivity().getActionBar().setTitle(firstName + " " + lastName);
      System.out.println("Name: " + firstName + " " + lastName);
    } else {
      chatId = "ERRRRRR";
    }

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.chat_fragment, container, false);

    view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createMessage();
      }
    });

    view.findViewById(R.id.match_snippet).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment matchFragment = new MatchFragment(chatId);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, matchFragment);

        transaction.addToBackStack(null);
        transaction.commit();
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

    String messageKey = MessageList.tableName + "/" + chatId + "/messageIds";
    this.mFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(messageKey);
    mUsername = "TEMP";
    // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
    final ListView listView = getListView();
    // Tell our list adapter that we only want 50 messages at a time
    mMessageListAdapter = new MessageListAdapter(mFirebaseRef.limitToFirst(50), getActivity(), R.layout.chat_message, mUsername);
    listView.setAdapter(mMessageListAdapter);
    mMessageListAdapter.registerDataSetObserver(new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        listView.setSelection(mMessageListAdapter.getCount() - 1);
      }
    });

    String matchKey = "matches/" + chatId + "/matchedUserId";
    Firebase matchFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(matchKey);

    final View finalView = view;
    matchFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        String matchedUserId = (String) snapshot.getValue();
        String userKey = "users/" + matchedUserId;
        Firebase userFirebaseRef = new Firebase(Constants.FIREBASE_URL).child(userKey);
        userFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            User matchedUser = snapshot.getValue(User.class);

            TextView matchName = (TextView) finalView.findViewById(R.id.match_name);
            matchName.setText(matchedUser.getDisplayName());

            TextView matchAge = (TextView) finalView.findViewById(R.id.match_age);
            matchAge.setText(matchedUser.getAge() + " yrs old");

            String url = matchedUser.getProfilePictureUrl();

            new PictureLoaderTask(new BitmapRunnable() {
              public void run() {
                ImageView image = (ImageView) finalView.findViewById(R.id.snippet_profile_picture);
                image.setImageBitmap(getBitmap());
              }
            }).execute(url);
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
  }

  @Override
  public void onStop() {
    super.onStop();
    mMessageListAdapter.cleanup();
  }
}
