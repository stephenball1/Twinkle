package com.launch.twinkle.twinkle;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

public class ProfileSetupFragment extends Fragment {
    private static final String TAG = ProfileSetupFragment.class.getSimpleName();

    private String id;
    private ImageView userImage;

    public ProfileSetupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setup, container, false);
        userImage = (ImageView) view.findViewById(R.id.userPicture);

        Bundle bundle = this.getArguments();
        id = bundle.getString("id");

        PictureGetter pictureGetter = new PictureGetter();
        pictureGetter.execute(id);
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
    private class PictureGetter extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmap;

        @Override
        protected Bitmap doInBackground(String... userID) {
            bitmap = getFacebookProfilePicture(userID[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            userImage.setImageBitmap(result);
        }
    }

    public static Bitmap getFacebookProfilePicture(String userID){
        System.out.println("user id: " + userID);
        Bitmap bitmap = null;
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (Exception e) {
            Log.i(TAG, "Cannot get profile picture.");
            e.printStackTrace();
        }
        return bitmap;
    }
}
