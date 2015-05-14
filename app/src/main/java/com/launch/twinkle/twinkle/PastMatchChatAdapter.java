package com.launch.twinkle.twinkle;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.models.PastMatchChatSummary;
import com.launch.twinkle.twinkle.models.Users;

import java.util.Calendar;
import java.util.Locale;

public class PastMatchChatAdapter extends FirebaseListAdapter2<PastMatchChatSummary> {

  private String mUsername;
  private Firebase firebase;

  public PastMatchChatAdapter(Query ref, Activity activity, int layout, String mUsername) {
    super(ref, PastMatchChatSummary.class, layout, activity);
    this.mUsername = mUsername;
    this.firebase = new Firebase(Constants.FIREBASE_URL);

  }

  @Override
  protected void populateView(View view, PastMatchChatSummary pastMatchChatSummary) {
    final View finalView = view;
    new PictureLoaderTask(new BitmapRunnable() {
      public void run() {
        ((ImageView) finalView.findViewById(R.id.matched_profile_picture)).setImageBitmap(getBitmap());
      }
    }).execute(Utils.getProfileUrl(pastMatchChatSummary.getMatchId()));

    new PictureLoaderTask(new BitmapRunnable() {
      public void run() {
        ((ImageView) finalView.findViewById(R.id.initial_profile_picture)).setImageBitmap(getBitmap());
      }
    }).execute(Utils.getProfileUrl(mUsername));

    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(pastMatchChatSummary.getMatchedTimeMilli());
    java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(view.getContext());
    TextView messageTime = (TextView) view.findViewById(R.id.match_time);
    messageTime.setVisibility(View.VISIBLE);
    messageTime.setText("Matched on " + dateFormat.format(cal.getTime()));

    firebase.child("user/" + pastMatchChatSummary.getMatchId()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Users user = dataSnapshot.getValue(Users.class);
        ((TextView) finalView.findViewById(R.id.message)).setText(user.getFirstName());
      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {

      }
    });
  }
}