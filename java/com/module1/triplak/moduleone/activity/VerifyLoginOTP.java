package com.module1.triplak.moduleone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.service.LoginOTP;


public class VerifyLoginOTP extends AppCompatActivity {
    private static final String TAG = VerifyLoginOTP.class.getSimpleName();
    private ProgressDialog progressDialog;
    private EditText etLoginOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.otp_submit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String otp = intent.getStringExtra("OTP");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        SessionManager sessionManager = new SessionManager(this);
        final String mobileNumber = sessionManager.getMobileNumber();

        Log.i(TAG, "Inside the Verify OTP class");

        etLoginOTP = (EditText) findViewById(R.id.etLoginOTP);
        etLoginOTP.setText(otp);
        Button btnVerifyLoginOTP = (Button) findViewById(R.id.btnVerifyLoginOTP);

        btnVerifyLoginOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Verifying OTP...");
                showDialog();
                Log.i(TAG, "Inside the Verify OTP class. Try block");
                String loginOTP = etLoginOTP.getText().toString().trim();

                // Check for empty data in the form
                if (!loginOTP.isEmpty() && loginOTP.length() == 6) {
                    // authorize code
                    checkLoginOTP(loginOTP, mobileNumber);
                } else {
                    // Prompt user to enter authorization code
                    Toast.makeText(getApplicationContext(),
                            "Please enter correct OTP!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void checkLoginOTP(String loginOTP, String mobile) {
        String otpLogin = loginOTP.trim();
        String mobileNumber = mobile.trim();
        if (!otpLogin.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), LoginOTP.class);
            intent.putExtra("loginOTP", otpLogin);
            intent.putExtra("mobileNumber", mobileNumber);
            Log.i(TAG, "In Login OTP block");
            startService(intent);
            hideDialog();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the correct OTP", Toast.LENGTH_SHORT).show();
        }

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
