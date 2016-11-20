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
 * Created by Himanjan on 4/22/2016.
 */
public class LoginOTP extends IntentService {

    private static String TAG = LoginOTP.class.getSimpleName();

    public LoginOTP() {
        super(LoginOTP.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String loginOTP = intent.getStringExtra("loginOTP");
            String mobileNo = intent.getStringExtra("mobileNumber");
            Log.i(TAG, "In Login OTP class");
            verifyLoginOTP(loginOTP, mobileNo);
        }
    }

    /**
     * Posting the Authorization code to server
     *
     * @param loginOTP received in the SMS
     */
    private void verifyLoginOTP(final String loginOTP, final String mobile) {
        Log.i(TAG, "Authorization Code: " + loginOTP);
        Log.i(TAG, "Mobile Number sent: " + mobile);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.LOGIN_OTP_VERIFY_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.i(TAG, response.toString());

                try {
                    Log.i(TAG, "Inside try block Login Activity");
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        // parsing the user profile information
                        Log.i(TAG, "Inside non error block");
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        String name = profileObj.getString("name");
                        String email = profileObj.getString("email");
                        String mobile = profileObj.getString("mobile");

                        Intent intent = new Intent(LoginOTP.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                params.put("loginOTP", loginOTP);
                params.put("mobileNumber", mobile);

                Log.e(TAG, "Posting params: " + params.toString());
                Log.i(TAG, "Posting params i: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

}
