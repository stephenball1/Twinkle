package com.launch.twinkle.twinkle;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.FirebaseRef;
import com.launch.twinkle.twinkle.models.Users;

import java.util.ArrayList;
import java.util.List;

// Match fragment runs assume there is a job that update the matchId for each user everyday.
public class PickCircleOfTrust extends Fragment {
  private static final String TAG = MatchFragment.class.getSimpleName();
  private View view;
  private Firebase firebase;
  private String[] mProjection;
  private ArrayList<String> mContacts;
  private ListView friendList;
  private ArrayList<String> friends;

  public PickCircleOfTrust() {
    firebase = new Firebase(Constants.FIREBASE_URL);
    friends = new ArrayList<String>();
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_pick_circle_of_trust, container, false);
    getActivity().getActionBar().setTitle("Invite Friends");
    getActivity().getActionBar().show();
    mProjection = new String[] {
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    final Cursor cursor = getActivity().getContentResolver().query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        mProjection,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    );

    int nameIndex = cursor.getColumnIndex(mProjection[0]);
    int numberIndex = cursor.getColumnIndex(mProjection[1]);

    mContacts = new ArrayList();

    int position = 0;
    boolean isSeparator = false;
    while(cursor.moveToNext()) {
      isSeparator = false;

      String name = cursor.getString(nameIndex);
      String number = cursor.getString(numberIndex);

      char[] nameArray;

      // If it is the first item then need a separator
      if (position == 0) {
        isSeparator = true;
        nameArray = name.toCharArray();
      }
      else {
        // Move to previous
        cursor.moveToPrevious();

        // Get the previous contact's name
        String previousName = cursor.getString(nameIndex);

        // Convert the previous and current contact names
        // into char arrays
        char[] previousNameArray = previousName.toCharArray();
        nameArray = name.toCharArray();

        // Compare the first character of previous and current contact names
        if (nameArray[0] != previousNameArray[0]) {
          isSeparator = true;
        }

        // Don't forget to move to next
        // which is basically the current item
        cursor.moveToNext();
      }

      // Need a separator? Then create a Contact
      // object and save it's name as the section
      // header while pass null as the phone number
      if (isSeparator) {
        //Contact contact = new Contact(String.valueOf(nameArray[0]), null, isSeparator);
        //mContacts.add( contact );
      }

      // Create a Contact object to store the name/number details
      Contact contact = new Contact(name, number, false);
      mContacts.add(name + " " + number);

      position++;
    }

    // List of items
    String[] items = new String[mContacts.size()];
    items = mContacts.toArray(items);
    // Create an array adapter
    final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mContacts);

    // set the adapter to the view
    ListView contactList = ((ListView) view.findViewById(R.id.listview));
    contactList.setAdapter(adapter);
    contactList.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> a, View v, int position,
                              long id) {
        String chosenContact = (String) adapter.getItem(position);
        friends.add(chosenContact);
        String[] tempArray = new String[friends.size()];
        tempArray = friends.toArray(tempArray);
        //ArrayAdapter friendListAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, tempArray);
        ArrayAdapter friendListAdapter = new MyCustomAdapter(getActivity(), R.layout.pick_contact_item, friends);
        friendList.setAdapter(friendListAdapter);
        friendListAdapter.notifyDataSetChanged();
      }
    });

    friendList = (ListView) view.findViewById(R.id.friendlistview);
    String[] tempArray = new String[friends.size()];
    tempArray = friends.toArray(tempArray);
    ArrayAdapter friendListAdapter = new MyCustomAdapter(getActivity(), R.layout.pick_contact_item, friends);
    friendList.setAdapter(friendListAdapter);

    EditText editText = (EditText) view.findViewById(R.id.editText);
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    ((Button) view.findViewById(R.id.send_invite)).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("4086279246", null, "Download Circl here!", null, null);
        Toast.makeText(getActivity(), "Invite send", Toast.LENGTH_SHORT).show();
        firebase.child("user/" + ApplicationState.getLoggedInUserId()).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                List<String> friendInvite =  user.getFriendInvite();
                friendInvite.addAll(friends);
                new FirebaseRef().storeUser(user);
              }
              @Override
              public void onCancelled(FirebaseError firebaseError) {
              }
            });
      }
    });
    return view;
  }

  class MyCustomAdapter extends ArrayAdapter<String> {
    private ArrayList<String> contactList;
    private LayoutInflater inflater;

    public MyCustomAdapter(Context context, int textViewResourceId,
                           ArrayList<String> contactList) {
      super(context, textViewResourceId, contactList);
      this.contactList = new ArrayList<String>();
      this.contactList.addAll(contactList);
      inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      View v = view;
      if (v == null) {
        v = inflater.inflate(R.layout.pick_contact_item, viewGroup, false);
      }

      TextView contactInfo = (TextView) v.findViewById(R.id.contact_info);
      CheckBox contactInfoCheckBox = (CheckBox) v.findViewById(R.id.contact_info_checkbox);
      contactInfo.setText((String) contactList.get(i));
      contactInfoCheckBox.setTag((String) contactList.get(i));
      contactInfoCheckBox.setChecked(true);
      contactInfoCheckBox.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          CheckBox cb = (CheckBox) v;
          String tag = (String) v.getTag();
          if (!cb.isChecked()) {
            friends.remove(tag);
            ArrayAdapter friendListAdapter = new MyCustomAdapter(getActivity(), R.layout.pick_contact_item, friends);
            friendList.setAdapter(friendListAdapter);
            friendListAdapter.notifyDataSetChanged();
          }
        }
      });
      return v;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  public class Contact {
    public String mName;
    public String mNumber;
    public boolean mIsSeparator;

    public Contact(String name, String number, boolean isSeparator) {
      mName = name;
      mNumber = number;
      mIsSeparator = isSeparator;
    }

    public void setName(String name) {
      mName = name;
    }

    public void setNumber(String number) {
      mNumber = number;
    }

    public void setIsSection(boolean isSection) {
      mIsSeparator = isSection;
    }
  }
}
