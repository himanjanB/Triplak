package com.module1.triplak.moduleone.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.module1.triplak.moduleone.activity.VerifyLoginOTP;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.config.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Himanjan on 4/22/2016.
 */
public class LoginService extends IntentService {
    private static String TAG = LoginService.class.getSimpleName();

    public LoginService() {
        super(LoginService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String mobileNumber = intent.getStringExtra("mobileNumber");
            String password = intent.getStringExtra("password");
            Log.i(TAG, "In LoginService class");
            userLogin(mobileNumber, password);
        }
    }

    /**
     * Posting the Authorization code to server
     *
     * @param mobileNumber received in the SMS
     */
    private void userLogin(final String mobileNumber, final String password) {
        Log.i(TAG, "Mobile Number: " + mobileNumber);
        Log.i(TAG, "Password: " + password);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

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
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        String name = profileObj.getString("name");
                        String email = profileObj.getString("email");
                        String mobile = profileObj.getString("mobile");
                        String otp = profileObj.getString("otp");

                        Toast.makeText(getApplicationContext(), message + otp, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginService.this, VerifyLoginOTP.class);
                        intent.putExtra("OTP", otp);
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
                params.put("mobileNumber", mobileNumber);
                params.put("password", password);

                Log.e(TAG, "Posting params: " + params.toString());
                Log.i(TAG, "Posting params i: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
