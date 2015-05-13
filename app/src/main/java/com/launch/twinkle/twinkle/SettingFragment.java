package com.launch.twinkle.twinkle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.launch.twinkle.twinkle.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.launch.twinkle.twinkle.models.FirebaseRef;
import com.launch.twinkle.twinkle.models.Users;

import java.util.ArrayList;
import java.util.List;

// Match fragment runs assume there is a job that update the matchId for each user everyday.
public class SettingFragment extends Fragment {
  private static final String TAG = SettingFragment.class.getSimpleName();
  private View view;
  private GridView pictureView;
  private Firebase firebase;
  private List<String> pictureUrls = new ArrayList<String>();
  private List<String> selectedUrls;
  private List<Bitmap> pictures = new ArrayList<Bitmap>();
  private Users user;
  private SeekBar distancePreferenceBar;
  private RangeSeekBar<Integer> agePreferenceBar;
  private boolean isInterestedInWomen;
  private boolean isInterestedInMen;

  public SettingFragment() {
    firebase = new Firebase(Constants.FIREBASE_URL);
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
    view = inflater.inflate(R.layout.fragment_setting, container, false);

    getActivity().getActionBar().setTitle("Profile");
    getActivity().getActionBar().show();

    pictureView = (GridView) view.findViewById(R.id.profile_picture_view);
    pictureView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v,
                              int position, long id) {
        ImageView checkMark = (ImageView) v.findViewById(R.id.selected);
        if (checkMark.getVisibility() == View.VISIBLE) {
          checkMark.setVisibility(View.GONE);
          selectedUrls.remove(pictureUrls.get(position));
        } else {
          checkMark.setVisibility(View.VISIBLE);
          selectedUrls.add(pictureUrls.get(position));
        }
      }
    });

    Button saveButton = (Button) view.findViewById(R.id.save_button);
    saveButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        EditText description = (EditText) view.findViewById(R.id.description);
        user.setDescription(description.getText().toString());
        user.setPictures(selectedUrls);
        String distance = ((TextView) view.findViewById(R.id.distance_preference_prompt))
            .getText().toString().substring(21);
        user.setDistancePreferenceMiles(Integer.parseInt(distance));
        String age = ((TextView) view.findViewById(R.id.age_preference_prompt))
            .getText().toString().substring(5);
        String[] tokens = age.split("-");
        user.setAgePreferenceLow(Integer.parseInt(tokens[0]));
        user.setAgePreferenceHigh(Integer.parseInt(tokens[1]));
        user.setInterestedInMen(isInterestedInMen);
        user.setInterestedInWomen(isInterestedInWomen);
        new FirebaseRef().storeUser(user);
        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
      }
    });

    distancePreferenceBar = ((SeekBar) view.findViewById(R.id.distance_preference));
    distancePreferenceBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      int progress = 0;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
        progress = progressValue;
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        ((TextView) view.findViewById(R.id.distance_preference_prompt)).setText("Distance (in miles): " + progress);
      }
    });

    agePreferenceBar = new RangeSeekBar<Integer>(16, 80, getActivity());
    agePreferenceBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
      @Override
      public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
        ((TextView) view.findViewById(R.id.age_preference_prompt)).setText("Age: " + minValue + "-" + maxValue);
      }
    });
    ((ViewGroup)view.findViewById(R.id.age_preference_container)).addView(agePreferenceBar);

    ((Switch) view.findViewById(R.id.women)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isInterestedInWomen = isChecked;
      }
    });

    ((Switch) view.findViewById(R.id.men)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isInterestedInMen = isChecked;
      }
    });

    firebase.child("user/" + ApplicationState.getLoggedInUserId()).addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
            user = snapshot.getValue(Users.class);
            pictureUrls = user.getPictures();
            //pictureUrls.add(user.getPictures().get(0));
            //pictureUrls.add(user.getPictures().get(1));
            selectedUrls = new ArrayList<String>(pictureUrls);
            for (final String pictureUrl : pictureUrls) {
              new PictureLoaderTask(new BitmapRunnable() {
                public void run() {
                  pictures.add(getBitmap());
                  if (pictures.size() == pictureUrls.size()) {
                    pictureView.setAdapter(new MyAdapter(getActivity()));
                  }
                }
              }).execute(pictureUrl);
            }
            if (!user.getDescription().equals("")) {
              ((EditText) view.findViewById(R.id.description)).setText(user.getDescription());
            }
            ((TextView) view.findViewById(R.id.distance_preference_prompt))
                .setText("Distance (in miles): " + user.getDistancePreferenceMiles());
            distancePreferenceBar.setProgress(user.getDistancePreferenceMiles());
            ((TextView) view.findViewById(R.id.age_preference_prompt))
                .setText("Age: " + user.getAgePreferenceLow() + "-" + user.getAgePreferenceHigh());
            agePreferenceBar.setSelectedMinValue(user.getAgePreferenceLow());
            agePreferenceBar.setSelectedMaxValue(user.getAgePreferenceHigh());
            isInterestedInWomen = user.isInterestedInWomen();
            isInterestedInMen = user.isInterestedInMen();
            ((Switch) view.findViewById(R.id.women)).setChecked(isInterestedInWomen);
            ((Switch) view.findViewById(R.id.men)).setChecked(isInterestedInMen);
          }
          @Override
          public void onCancelled(FirebaseError firebaseError) {
          }
        });
    return view;
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

  private class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    public MyAdapter(Context context) {
      inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
      return pictures.size();
    }

    @Override
    public Bitmap getItem(int i) {
      return pictures.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      View v = view;
      ImageView picture;

      if (v == null) {
        v = inflater.inflate(R.layout.profile_picture_view_item, viewGroup, false);
        v.setTag(R.id.picture, v.findViewById(R.id.picture));
      }

      picture = (ImageView) v.getTag(R.id.picture);
      picture.setImageBitmap(getItem(i));
      return v;
    }
  }
}
