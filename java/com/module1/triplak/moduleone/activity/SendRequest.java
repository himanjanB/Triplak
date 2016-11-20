package com.module1.triplak.moduleone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.service.UpdateFriendsDetails;

import java.util.ArrayList;

public class SendRequest extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = SendRequest.class.getSimpleName();
    ArrayList<SelectUser> selectedUsers;
    //String[] updateFriendsArray;
    ArrayList<String> updateFriendsList;
    SessionManager sessionManager;
    private Button btnSend, btnBack;
    private EditText etRequestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSend = (Button) findViewById(R.id.btnRequestSend);
        btnBack = (Button) findViewById(R.id.btnRequestBack);
        etRequestInfo = (EditText) findViewById(R.id.etRequestInfo);

        String requestMessage = etRequestInfo.getText().toString().trim();
        requestMessage += " Zxd453w";
        etRequestInfo.setText(requestMessage);
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        sessionManager = new SessionManager(getApplicationContext());
        String phoneNumber = sessionManager.getMobileNumber();

        Log.i(TAG, "Phone number picked is: " + phoneNumber);

        selectedUsers = getIntent().getParcelableArrayListExtra("key");

        //updateFriendsArray = new String[10];
        updateFriendsList = new ArrayList<>();
        int count = 0;
        for (SelectUser s : selectedUsers) {
            Log.i(TAG, "Name: " + s.getName());
            Log.i(TAG, "Phone: " + s.getPhone());
            //updateFriendsArray[count] = s.getName();
            updateFriendsList.add(s.getName());
            updateFriendsList.add(s.getPhone());
            count += 1;
            //updateFriendsArray[count] = s.getPhone();
            count += 1;
        }
        //updateFriendsArray[count] = phoneNumber;
        updateFriendsList.add(phoneNumber);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRequestSend) {
            //updateFriendDetails(updateFriendsArray);
            updateFriendDetails(updateFriendsList);
            finish();
        } else if (v.getId() == R.id.btnRequestBack) {
            Intent intent = new Intent(this, AccessContacts.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateFriendDetails(ArrayList<String> userArray) {
        String firstUser = "";
        String firstUsersPhone = "";
        String secondUser = "";
        String secondUsersPhone = "";
        String userPhoneNumber = "";

        if (userArray.size() == 5) {
            firstUser = userArray.get(0);
            firstUsersPhone = userArray.get(1);
            secondUser = userArray.get(2);
            secondUsersPhone = userArray.get(3);
            userPhoneNumber = userArray.get(4);
        } else if (userArray.size() == 3) {
            firstUser = userArray.get(0);
            firstUsersPhone = userArray.get(1);
            userPhoneNumber = userArray.get(2);
        }

        Intent intent = new Intent(getApplicationContext(), UpdateFriendsDetails.class);
        intent.putExtra("firstUser", firstUser);
        intent.putExtra("firstUsersPhone", firstUsersPhone);
        intent.putExtra("secondUser", secondUser);
        intent.putExtra("secondUsersPhone", secondUsersPhone);
        intent.putExtra("userPhoneNumber", userPhoneNumber);
        startService(intent);
    }
}

