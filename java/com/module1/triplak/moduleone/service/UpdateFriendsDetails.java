package com.module1.triplak.moduleone.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.module1.triplak.moduleone.activity.HomePage;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.config.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Himanjan on 01-05-2016.
 */
public class UpdateFriendsDetails extends IntentService {
    private static String TAG = LoginService.class.getSimpleName();

    public UpdateFriendsDetails() {
        super(UpdateFriendsDetails.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String userOne = intent.getStringExtra("firstUser");
            String userPhoneOne = intent.getStringExtra("firstUsersPhone");
            String userTwo = intent.getStringExtra("secondUser");
            String userPhoneTwo = intent.getStringExtra("secondUsersPhone");
            String phoneNumber = intent.getStringExtra("userPhoneNumber");
            Log.i(TAG, "In LoginService class");
            friendsDetailsUpdate(userOne, userPhoneOne, userTwo, userPhoneTwo, phoneNumber);
        }
    }

    /**
     * Posting the Authorization code to server
     *
     * @param userOne received in the SMS
     */
    private void friendsDetailsUpdate(final String userOne, final String userOnePhone, final String userTwo, final String userTwoPhone, final String phoneNumber) {
        Log.i(TAG, "User One: " + userOne);
        Log.i(TAG, "User One Phone: " + userOnePhone);
        Log.i(TAG, "User Two: " + userTwo);
        Log.i(TAG, "User Two Phone: " + userTwoPhone);
        Log.i(TAG, "Saved Mobile number is : " + phoneNumber);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_FRIENDS_DETAILS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.i(TAG, response.toString());

                try {
                    Log.i(TAG, "Inside try block");
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        // parsing the user profile information
                        Log.i(TAG, "Inside non error block");

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(UpdateFriendsDetails.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userOne", userOne);
                params.put("userOnePhone", userOnePhone);
                params.put("userTwo", userTwo);
                params.put("userTwoPhone", userTwoPhone);
                params.put("userMobileNumber" , phoneNumber);

                Log.e(TAG, "Posting params: " + params.toString());
                Log.i(TAG, "Posting params i: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
