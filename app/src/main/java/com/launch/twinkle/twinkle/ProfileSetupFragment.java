package com.launch.twinkle.twinkle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileSetupFragment extends Fragment {
    private static final String TAG = ProfileSetupFragment.class.getSimpleName();

    private GridView gridView;
    private ProgressBar progressBar;
    private Button nextButton;
    private static List<Bitmap> pictures;
    private List<String> pictureUrls;
    private HashMap<Integer, Boolean> selectedPictures = new HashMap<Integer, Boolean>();

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
        gridView = (GridView)view.findViewById(R.id.gridview);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_panel);
        nextButton = (Button) view.findViewById(R.id.button);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedPictures.put(position, !selectedPictures.get(position));
                ImageView checkMark = (ImageView)v.findViewById(R.id.selected);
                if (selectedPictures.get(position)) {
                    checkMark.setVisibility(View.VISIBLE);
                } else {
                    checkMark.setVisibility(View.GONE);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO(holman): store selected pictures to firebase.
                // Perform action on click
            }
        });

        getFacebookProfilePictures();
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
    private class PictureGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public static List<Bitmap> getBitMaps(List<String> urls){
        List<Bitmap> pictures = new ArrayList<Bitmap>();
        try {
            for (String url : urls) {
                System.out.println("getBitMaps");
                URL imageURL = new URL(url);
                pictures.add(BitmapFactory.decodeStream(imageURL.openConnection().getInputStream()));
            }
        } catch (Exception e) {
            Log.i(TAG, "Cannot get profile picture.");
            e.printStackTrace();
        }
        return pictures;
    }

    public void getFacebookProfilePictures() {
        System.out.println("hiiiiiiiii");
        Session session = Session.getActiveSession();
        new Request(session, "/me/albums", null, HttpMethod.GET, new Request.Callback() {
            public void onCompleted(Response response) {
                try {
                    JSONArray albumArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                    System.out.println("hi2");
                    for (int i = 0; i < albumArr.length(); i++) {
                        JSONObject item = albumArr.getJSONObject(i);
                        System.out.println("type:" + item.getString("type"));
                        if (item.getString("type").equals("profile")) {
                            new Request(Session.getActiveSession(), "/" + item.getString("id") + "/photos", null, HttpMethod.GET, new Request.Callback() {
                                public void onCompleted(Response response) {
                                    System.out.println("hiii");
                                    try {
                                        JSONArray photoArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");

                                        List<String> urls = new ArrayList<String>();
                                        for (int i = 0; i < photoArr.length(); i++) {
                                            JSONObject item = photoArr.getJSONObject(i);
                                            String url = item.getJSONArray("images").getJSONObject(0).getString("source");
                                            urls.add(url);
                                            selectedPictures.put(i, true);
                                        }
                                        pictures = getBitMaps(urls);
                                        pictureUrls = urls;
                                        progressBar.setVisibility(View.GONE);
                                        gridView.setAdapter(new MyAdapter(getActivity()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).executeAsync();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();
    }

    private class MyAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Bitmap getItem(int i)
        {
            return pictures.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;

            if(v == null)
            {
                v = inflater.inflate(R.layout.profile_picture_view_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            picture.setImageBitmap(getItem(i));

            //ImageView checkmark = (ImageView)v.getTag(R.id.selected);
            return v;
        }
    }
}
