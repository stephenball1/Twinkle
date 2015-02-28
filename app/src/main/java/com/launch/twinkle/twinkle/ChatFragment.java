package com.launch.twinkle.twinkle;

import com.launch.twinkle.twinkle.models.Message;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by sball on 2/27/15.
 */
public class ChatFragment extends Fragment {

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

      input.setText("");
    }
  }
}
