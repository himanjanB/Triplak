package com.module1.triplak.moduleone.activity;

/*
 This is the start class of the app.

 TODO In this activity, there are the following things left to do.
 Verify the phone number according to the country code selected. Probably need to use a library.
 The changes where it checks if the user is already logged in.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.service.LoginService;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = StartActivity.class.getSimpleName();

    private SessionManager sessionManager;
    private Button btnStart;
    private ImageButton btnPasswordVisible;
    private TextView tvLogin, tvForgotPassword, tvPhoneCode, privacyPolicy;
    private EditText etPhoneNumber, etPassword;
    private ProgressDialog progressDialog;
    private boolean passwordVisibilityFlag = true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sessionManager = new SessionManager(getApplicationContext());

        checkForUserLoginDetails();
        Log.i(TAG, "Checking for successful registration..");
        checkForSuccessfulRegistration();

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnPasswordVisible = (ImageButton) findViewById(R.id.btnPasswordVisible);
        privacyPolicy = (TextView) findViewById(R.id.tvPrivacyPolicy);
        btnStart = (Button) findViewById(R.id.btnSignIn);
        btnStart.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);


        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                //TODO Need to verify if a given phone number is valid or not depending on the country phone code.
                //TODO Now testing only for Indian mobile numbers. 10 digits starting with +91
                if (phoneNumber.length() != 10) {
                    disableButton();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                //TODO Need to change this condition. The button should be enabled only when the user enters a correct phone number.
                //TODO Now testing only for Indian mobile numbers. 10 digits starting with +91
                if (phoneNumber.length() == 10) {
                    enableButton();
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    disableButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    btnStart.setEnabled(false);
                } else if (!password.isEmpty()) {
                    enableButton();
                }

            }
        });


        TextView textView = (TextView) findViewById(R.id.tvPrivacyPolicy);
        String text = textView.getText().toString();
        Spannable wordToSpan = new SpannableString(text);
        int color;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = getColor(R.color.LinkColor);
        } else {
            color = getResources().getColor(R.color.LinkColor);
        }
        wordToSpan.setSpan(new ForegroundColorSpan(color), 30, 44,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(wordToSpan);

        SpannableString content = new SpannableString(wordToSpan);
        content.setSpan(new UnderlineSpan(), 30, 44, 0);
        textView.setText(content);

        btnStart = (Button) findViewById(R.id.btnSignIn);
        tvLogin = (TextView) findViewById(R.id.tvAuthentication);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvPhoneCode = (TextView) findViewById(R.id.tvPhoneCode);

        btnStart.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvPhoneCode.setOnClickListener(this);
        btnPasswordVisible.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
    }

    private void checkForUserLoginDetails() {
        // Check if user is already logged in or not
        if (sessionManager.isLoggedIn()) {
            //User is already logged in. Take him to main activity Home Page is yet to be done.
            //TODO Need to make the boolean isLoggedIn to true. For testing it is set to false.
            Intent intent = new Intent(StartActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkForSuccessfulRegistration() {
        if (sessionManager.isUserRegisteredSuccessfully()) {
            //Check if the user is registered successfully. Take the user to the Payment class directly.
            //User is successfully registered.
            Intent intent = new Intent(StartActivity.this, PaymentGateway.class);
            startActivity(intent);
            finish();
        }
    }

    private void disableButton() {
        btnStart.setEnabled(false);
    }

    private void enableButton() {
        if (etPhoneNumber.getText().toString().trim().length() == 10 && etPassword.getText().toString().trim().length() > 0) {
            btnStart.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideDialog();
    }

    /**
     * Validating user details form
     */
    private void validateMobileNumber() {
        String mobile = etPhoneNumber.getText().toString().trim();

        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(mobile)) {
            // saving the mobile number in shared preferences
            sessionManager.setMobileNumber(mobile);
            String mobileNumber = etPhoneNumber.getText().toString();
            String password = etPassword.getText().toString();
            Intent intent = new Intent(getApplicationContext(), LoginService.class);
            intent.putExtra("mobileNumber", mobileNumber);
            intent.putExtra("password", password);
            startService(intent);
            hideDialog();
        } else {
            hideDialog();
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            validateMobileNumber();
            progressDialog.setMessage("Logging in...");
            showDialog();
        } else if (v.getId() == R.id.tvAuthentication) {
            Intent intent = new Intent(getApplicationContext(), InitiateCodeVerification.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tvForgotPassword) {
            Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tvPhoneCode) {
            Intent intent = new Intent(getApplicationContext(), CountryCode.class);
            startActivityForResult(intent, 2);
            //Try to call a fragment later on
            //getSupportFragmentManager().beginTransaction().add(R.id.svMain, new CountryCodes()).commit();
        } else if (v.getId() == R.id.btnPasswordVisible) {
            if (passwordVisibilityFlag) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPassword.setSelection(etPassword.length());
                passwordVisibilityFlag = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.setSelection(etPassword.length());
                passwordVisibilityFlag = true;
            }
        } else if (v.getId() == R.id.tvPrivacyPolicy) {
            String url = AppConfig.PRIVACY_POLICY_URL;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == 2) {
                String code = data.getStringExtra("Code");
                Log.i("Some", "Code is : " + code);
                tvPhoneCode = (TextView) findViewById(R.id.tvPhoneCode);
                assert tvPhoneCode != null;
                tvPhoneCode.setText(code);
            }
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

    @Override
    public void onStart(){
        hideDialog();
        super.onStart();
        checkForUserLoginDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Go to the settings tab
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
