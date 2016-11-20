package com.module1.triplak.moduleone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.fragments.FragmentOne;
import com.module1.triplak.moduleone.fragments.FragmentThree;
import com.module1.triplak.moduleone.fragments.FragmentTwo;
import com.module1.triplak.moduleone.helper.SessionManager;
import com.module1.triplak.moduleone.menuActivities.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    /*private ImageView imageView1;
    private ImageLoader imageLoader;
    private String url = "http://localhost/triplak/IMG_7103.JPG";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        sessionManager = new SessionManager(this);
        sessionManager.setLogin(false);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        sessionManager = new SessionManager(this);

        /*imageView1 = (ImageView) findViewById(R.id.imageView);
        imageLoader = new ImageLoader(this);*/
        Log.i(TAG, "In onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "In onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentOne(), getResources().getString(R.string.home_tab_layout));
        adapter.addFragment(new FragmentTwo(), getResources().getString(R.string.contacts_tab_layout));
        adapter.addFragment(new FragmentThree(), getResources().getString(R.string.portal_tab_layout));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // To be implemented for other settings like portal and wait preferences
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        /*if (id == R.id.action_account_preferences) {
            return true;
        }
        if (id == R.id.action_faqAndHelp) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*public void showImage(View view) {
        Log.i(TAG, "Button clicked");
        *//*imageLoader.displayImage(url, imageView1);*//*
    }*/

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
