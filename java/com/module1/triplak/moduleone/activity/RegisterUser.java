package com.module1.triplak.moduleone.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.config.AppController;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.service.VerifyOTPService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = RegisterUser.class.getSimpleName();
    private static final String EMAIL_KEY = "SAVED_EMAIL";

    //Declaring the necessary variables.
    private EditText inputEmail, inputMobile, inputOtp;
    private Button btnRegister, btnVerifyOtp;
    private SessionManager sessionManager;
    private TextView tvPhoneCode;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.title_activity_register_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputEmail = (EditText) findViewById(R.id.etEmail);
        inputMobile = (EditText) findViewById(R.id.etMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        tvPhoneCode = (TextView) findViewById(R.id.tvPhoneCode);

        btnRegister.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);
        tvPhoneCode.setOnClickListener(this);

        sessionManager = new SessionManager(this);

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (sessionManager.isWaitingForSms()) {
            viewPager.setCurrentItem(0);
        }

        if (savedInstanceState != null) {
            inputEmail.setText(savedInstanceState.getString(EMAIL_KEY));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String savedEmail = inputEmail.getText().toString();
        outState.putString(EMAIL_KEY, savedEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        String possibleEmail = "";
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
                Log.i("Some", "Possible email: " + possibleEmail);
            }
        }
        Log.i(TAG, "Input email value is: " + inputEmail.getText().toString());
        if (inputEmail.getText().toString().equals("")) {
            inputEmail.setText(possibleEmail);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                if (counter <= 3) {
                    counter++;
                    validateForm();
                } else {
                    //Need to write a database function where it will log the time till 24 hours.
                    Toast.makeText(this, "Cannot generate OTP for more than 3 times. Please try after 24 hours", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_verify_otp:
                verifyOtp();
                break;
            case R.id.tvPhoneCode:
                Intent intent = new Intent(getApplicationContext(), CountryCode.class);
                startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == 2) {
                String code = data.getStringExtra("Code");
                Log.i("Some", "Code is : " + code);
                tvPhoneCode = (TextView) findViewById(R.id.tvPhoneCode);
                tvPhoneCode.setText(code.toString());
            }
        }
    }

    /**
     * Validating user details form
     */
    private void validateForm() {
        String email = inputEmail.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();

        // validating empty name and email
        if (email.length() == 0 || mobile.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmailID(email)) {
            Toast.makeText(getApplicationContext(), "Invalid email ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(mobile)) {
            // saving the mobile number in shared preferences
            sessionManager.setMobileNumber(mobile);

            // requesting for sms
            requestForSMS(email, mobile);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmailID(String email) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Log.i("Some", "" + emailPattern);
        if (emailPattern.matcher(email).matches()) {
            Log.i("Some", "Good email: ");
            return true;
        } else {
            Log.i("Some", "Bad email: ");
            return false;
        }
    }

    /**
     * Method initiates the SMS request on the server
     *
     * @param email  user email address
     * @param mobile user valid mobile number
     */
    private void requestForSMS(final String email, final String mobile) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REQUEST_SMS_TEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms
                        sessionManager.setIsWaitingForSms(true);

                        // moving the screen to next pager item i.e otp screen
                        viewPager.setCurrentItem(1);
                        inputOtp.setText(message);
                        Toast.makeText(getApplicationContext(), "OTP is: " + message, Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
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

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("mobile", mobile);

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = inputOtp.getText().toString().trim();
        Log.i(TAG, "Calling Verify OTP ");

        if (!otp.isEmpty()) {
            Log.i(TAG, "Inside the calling method");
            Intent intent = new Intent(getApplicationContext(), VerifyOTPService.class);
            intent.putExtra("otp", otp);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(ViewGroup collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    getSupportActionBar().setTitle(R.string.title_activity_register_user);
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    getSupportActionBar().setTitle(R.string.title_activity_register_user);
                    break;
            }
            return findViewById(resId);
        }
    }
}

