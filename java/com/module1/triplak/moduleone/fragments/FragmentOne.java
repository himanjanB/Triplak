package com.module1.triplak.moduleone.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.profileView.ProfileViewerActivity;
import com.module1.triplak.moduleone.service.UpdateMoneyService;
import com.module1.triplak.moduleone.utils.ImageLoader;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Himanjan on 4/23/2016.
 */
public class FragmentOne extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private String money = "";
    private final String TAG = FragmentOne.class.getSimpleName();
    protected ResponseReceiver mResponseReceiver;
    SessionManager sessionManager;
    TextView totalMoney;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView imageView1, imageView2, imageView3;
    private ImageLoader imageLoader;
    //private String url = "http://localhost/triplak/Contacts-48.png";
    private String url = "https://upload.wikimedia.org/wikipedia/commons/4/4b/Everest_kalapatthar_crop.jpg";

    public FragmentOne() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sessionManager = new SessionManager(getActivity());
        mResponseReceiver = new ResponseReceiver();
        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_access, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_account_settings) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.content_home_page, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        imageView1 = (CircleImageView) rootView.findViewById(R.id.imageView);
        imageView2 = (CircleImageView) rootView.findViewById(R.id.ChildOne);
        imageView3 = (CircleImageView) rootView.findViewById(R.id.ChildTwo);
        swipeRefreshLayout.setOnRefreshListener(this);

        totalMoney = (TextView) rootView.findViewById(R.id.tvTotalMoney);
        totalMoney.setText(money.trim());
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);

        /*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent =
                *//*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image*//**//*");
                getActivity().startActivityForResult(photoPickerIntent, 1);*//*

            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                Log.i(TAG, "Selected image: " + filePath);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                /*try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        //FINE
                    } else {
                        //NOT IN REQUIRED FORMAT
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    @Override
    public void onStart() {
        super.onStart();
        mResponseReceiver.updateMyMoney();
        imageLoader.displayImage(url, imageView1, getContext());
        imageLoader.displayImage(url, imageView2, getContext());
        imageLoader.displayImage(url, imageView3, getContext());
        Log.i(TAG, "In the onStart method of Fragment 1");
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResponseReceiver, new IntentFilter(UpdateMoneyService.Constants.BROADCAST_ACTION));
        super.onResume();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "In onRefresh Method");
        mResponseReceiver.updateMyMoney();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                /*Intent profileIntent = new Intent(getActivity(), ProfileViewerActivity.class);
                startActivity(profileIntent);*/
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                getActivity().startActivityForResult(photoPickerIntent, 1);
                break;
            case R.id.ChildOne:
                Intent profileOneIntent = new Intent(getActivity(), ProfileViewerActivity.class);
                startActivity(profileOneIntent);
                break;
            case R.id.ChildTwo:
                Intent profileTwoIntent = new Intent(getActivity(), ProfileViewerActivity.class);
                startActivity(profileTwoIntent);
                break;
        }

    }

    public class ResponseReceiver extends BroadcastReceiver {

        public void updateMyMoney() {
            String authCode = "Update";
            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
            if (!authCode.isEmpty()) {
                Intent intent = new Intent(getActivity(), UpdateMoneyService.class);
                String phoneNumber = sessionManager.getMobileNumber();
                Log.i(TAG, "Phone number is fragment: " + phoneNumber);
                intent.putExtra("phoneNumber", phoneNumber);
                Log.i(TAG, "In Update money block fragment one");
                getActivity().startService(intent);
                Log.i(TAG, "Control in here.. again after updating money fragment one");
            } else {
                Toast.makeText(getActivity(), "Cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Stopping refresh animation after making http call
            swipeRefreshLayout.setRefreshing(false);
            Log.i(TAG, "Control in onReceive method fragment one");
            String money = intent.getStringExtra("Money");
            Log.i(TAG, "Value of money fragment one: " + money);
            totalMoney.setText(money.trim());
            if (swipeRefreshLayout != null) {
                Log.i(TAG, "In onRefresh Method second");
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

