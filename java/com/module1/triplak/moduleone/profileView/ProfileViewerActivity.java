package com.module1.triplak.moduleone.profileView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.utils.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewerActivity extends AppCompatActivity {
    ListView listView;
    private CircleImageView profilePic;
    private ImageLoader imageLoader;
    private String url = "http://localhost/triplak/Contacts-48.png";
    //private String url = "https://upload.wikimedia.org/wikipedia/commons/4/4b/Everest_kalapatthar_crop.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.profileInfoList);
        profilePic = (CircleImageView) findViewById(R.id.profileImage);
        imageLoader = new ImageLoader(this);

        //Defined Array values to show in ListView
        String[] values = new String[]{"Account",
                "Transactions",
                "Password",
                "Settings",
                "Notification",
                "About and help"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.profile_list_layout, R.id.profileList, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        imageLoader.displayImage(url, profilePic, this);
    }
}
