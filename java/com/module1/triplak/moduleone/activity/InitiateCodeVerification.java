package com.module1.triplak.moduleone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.service.HttpService;

public class InitiateCodeVerification extends AppCompatActivity {
    private static final String TAG = InitiateCodeVerification.class.getSimpleName();
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Calling onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_code_verification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_initiate_code_verification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText etAuthorisationCode = (EditText) findViewById(R.id.etActivationCode);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        sessionManager = new SessionManager(getApplicationContext());
        hideDialog();
        checkForUserLoginDetails();

        Button btnActivationCode = (Button) findViewById(R.id.btnCheckCode);
        btnActivationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authenticationCode = etAuthorisationCode.getText().toString().trim();
                // Check for empty data in the form
                if (!authenticationCode.isEmpty() && authenticationCode.length() == 7) {
                    // authorize code
                    checkAuthorization(authenticationCode);
                    progressDialog.setMessage("Verifying authentication code ...");
                    showDialog();
                } else {
                    // Prompt user to enter authorization code
                    Toast.makeText(getApplicationContext(),
                            "Please enter correct authorization code!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        hideDialog();
        checkForUserLoginDetails();
    }

    private void checkForUserLoginDetails() {
        if (sessionManager.isUserRegisteredSuccessfully()) {
            //Check if the user is registered successfully. Take the user to the Payment class directly.
            //User is successfully registered.
            Intent intent = new Intent(InitiateCodeVerification.this, StartActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initiate_code_verification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_faqAndHelp) {
            String url = AppConfig.FAQ_DETAILS;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAuthorization(final String authenticationCode) {
        String authCode = authenticationCode.trim();

        if (!authCode.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), HttpService.class);
            intent.putExtra("authCode", authCode);
            Log.i(TAG, "In checkAuthorization block");
            startService(intent);
            Log.i(TAG, "In checkAuthorization block 1");
            hideDialog();
            Log.i(TAG, "In checkAuthorization block 2");
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the authorization code", Toast.LENGTH_SHORT).show();
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
