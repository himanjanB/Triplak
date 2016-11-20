package com.module1.triplak.moduleone.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.module1.triplak.moduleone.activity.InitiateCodeVerification;
import com.module1.triplak.moduleone.activity.RegisterUser;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.config.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Himanjan on 4/23/2016.
 */
public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String authCode = intent.getStringExtra("authCode");
            Log.i(TAG, "In httpService class");
            verifyAuthCode(authCode);
        }
    }

    /**
     * Posting the Authorization code to server
     *
     * @param authCode received in the SMS
     */
    private void verifyAuthCode(final String authCode) {
        Log.i(TAG, "Authorization Code: " + authCode);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.AUTHENTICATION_URL, new Response.Listener<String>() {

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

                        Intent intent = new Intent(HttpService.this, RegisterUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(HttpService.this, InitiateCodeVerification.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
                        "Error in Connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authCode", authCode);

                Log.e(TAG, "Posting params: " + params.toString());
                Log.i(TAG, "Posting params i: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

}
