package com.module1.triplak.moduleone.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.config.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Himanjan on 4/23/2016.
 */
public class UpdateMoneyService extends IntentService {
    Context context;

    private static String TAG = UpdateMoneyService.class.getSimpleName();

    public UpdateMoneyService() {
        super(UpdateMoneyService.class.getSimpleName());
    }

    public final class Constants {
        // Defines a custom Intent action
        public static final String BROADCAST_ACTION = "triplak.com.trio.BROADCAST";
        // Defines the key for the status "extra" in an Intent
        public static final String EXTENDED_DATA_STATUS = "triplak.com.trio.STATUS";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String phoneNumber = intent.getStringExtra("phoneNumber");
            Log.i(TAG, "In updateMoneyService class");
            Log.i(TAG, "The phone Number: " + phoneNumber);
            getUpdateMoney(phoneNumber);
        }
    }

    /**
     * Posting the Authorization code to server
     *
     * @param phoneNumber received in the SMS
     */
    private void getUpdateMoney(final String phoneNumber) {
        Log.i(TAG, "Phone Number: " + phoneNumber);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.UPDATE_MONEY_URL, new Response.Listener<String>() {

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
                        Log.i(TAG, "Inside non error block. Update money service");
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        String money = profileObj.getString("status");
                        Log.i(TAG, "Value of money in the service class: " + money);

                        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
                        localIntent.putExtra("Money", money);
                        // Broadcasts the Intent to receivers in this app.
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", phoneNumber);

                Log.e(TAG, "Posting params: " + params.toString());
                Log.i(TAG, "Posting params i: " + params.toString());
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}

