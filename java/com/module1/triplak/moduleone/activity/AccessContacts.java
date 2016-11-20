package com.module1.triplak.moduleone.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.config.AppConfig;
import com.module1.triplak.moduleone.helper.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AccessContacts extends AppCompatActivity implements AsyncResponse, ActionMode.Callback {
    public final static String TAG = "TAG";

    ArrayList<SelectUser> selectUsers;
    Map<String, String> contactsMap = new HashMap<>();
    int mSelectedItem;

    //Contact List
    ListView listView;

    //Cursor to load contacts list
    Cursor phones;

    //Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;

    SessionManager sessionManager;
    SearchView searchView;
    ArrayList<SelectUser> selectedUsersList = new ArrayList<SelectUser>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(getApplicationContext());

        selectUsers = new ArrayList<>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        loadRefreshedContacts();
    }

    @Override
    public void onStart() {
        super.onStart();
        selectedUsersList.clear();
        // Check if user is already logged in or not
        if (sessionManager.isLoggedIn()) {
            //User is already logged in. Take him to main activity Home Page is yet to be done.
            //TODO Need to make the boolean isLoggedIn to true. For testing it is set to false.
            Intent intent = new Intent(AccessContacts.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    public void loadRefreshedContacts() {
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
        progressDialog.setMessage("Fetching Contacts...");
        showDialog();
    }

    @Override
    public void processFinish(String output) {
        Intent intent = new Intent(this, HomePage.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        searchInContacts(menu);
        /*//action_search_contacts
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search_contacts).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        showSoftKeyBoard(searchView);

        searchView.setQueryHint("Type a name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Some", "onQueryTextSubmit ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (adapter != null) {
                    adapter.filter(s);
                } else {
                }
                return false;
            }
        });*/

        return true;
    }

    private void searchInContacts(Menu menu) {
        //action_search_contacts
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search_contacts).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        showSoftKeyBoard(searchView);

        searchView.setQueryHint("Type a name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(TAG, "onQueryTextSubmit ");
                searchView.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (adapter != null) {
                    adapter.filter(s);
                } else {
                }
                return false;
            }
        });
    }

    private void showSoftKeyBoard(View searchView) {
        if (searchView.requestFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /*private void handleListItemClick(Menu menu, SearchView searchView) {
        if(searchView.isShown()){
            menu.findItem(R.id.action_search_contacts).collapseActionView();
            searchView.setQuery("", false);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_refresh) {
            loadRefreshedContacts();
            return true;
        }
        if (id == R.id.action_faqAndHelp) {
            String url = AppConfig.FAQ_DETAILS;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_add_contact) {
            Intent addAContactIntent = new Intent(Intent.ACTION_INSERT);
            addAContactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            startActivity(addAContactIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            //May be display a progress dialog..
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("Count", "Number of phone contacts: " + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(AccessContacts.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectUser selectUser = new SelectUser();

                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber.replace(" ", "").trim());
                    selectUser.setEmail(id);

                    //Removing duplicate contacts.
                    if (contactsMap.get(selectUser.getPhone()) == null) {
                        contactsMap.put(selectUser.getPhone(), selectUser.getName());
                        selectUsers.add(selectUser);
                    }
                }
            } else {
                Log.e("Cursor close", "No information available");
            }
            if (phones != null) {
                phones.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers, AccessContacts.this);
            listView.setAdapter(adapter);
            hideDialog();

            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                private int nr = 0;

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    if (null != getSupportActionBar())
                        getSupportActionBar().show();
                    adapter.clearSelection();
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    nr = 0;
                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.contextual_menu, menu);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_send:
                            /*nr = 0;
                            adapter.clearSelection();*/
                            for (int i = 0; i < selectUsers.size(); i++) {
                                Log.i(TAG, "User: " + selectUsers.get(i).getName());
                            }

                            HashMap<Integer, Boolean> selectedUserMap = new HashMap<Integer, Boolean>();
                            selectedUserMap = adapter.getSelectedUserMap();


                            Set<Integer> positions = selectedUserMap.keySet();

                            for (Integer i : positions) {
                                SelectUser selectUser = new SelectUser();
                                String name = selectUsers.get(i).getName();
                                String number = selectUsers.get(i).getPhone();
                                Log.i(TAG, "Name is: " + name + " and number is: " + number);
                                selectUser.setName(name);
                                selectUser.setPhone(number);
                                selectedUsersList.add(selectUser);
                            }

                            Intent intent = new Intent(getApplicationContext(), SendRequest.class);
                            //Put in any extra details needed like phone number of the selected contacts.
                            /*Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("selectedUserKey", (ArrayList<? extends Parcelable>)selectedUsersList);*/
                            intent.putParcelableArrayListExtra("key", selectedUsersList);
                            startActivity(intent);
                            mode.finish();
                            break;

                        case R.id.action_search_contacts:
                            Toast.makeText(getApplicationContext(), "To be done", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    if (checked) {
                        Log.i(TAG, "Selecting one contact");
                        if (nr > 1) {
                            Toast.makeText(getApplicationContext(), "Cannot select more than 2 contacts", Toast.LENGTH_SHORT).show();
                            //adapter.removeSelection(position);
                            return;
                        } else {
                            nr++;
                            adapter.setNewSelection(position, checked);

                        }
                    } else {
                        Log.i(TAG, "Un selecting one contact");
                        if (nr < 0 || nr == 0) {
                            Log.i(TAG, "Less than 0");
                            return;
                        }
                        Log.i(TAG, "Decreasing count");
                        nr--;
                        adapter.removeSelection(position);
                    }
                    mode.setTitle(nr + " selected");

                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                    return false;
                }
            });

            //Select item on list click
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    view.setSelected(true);
                    Log.i(TAG, "Here is the listener " + position);
                    adapter.notifyDataSetChanged();
                }
            });
            listView.setFastScrollEnabled(true);
        }
    }

    /**
     * Created by Himanjan on 14-11-2015.
     */
    public class SelectUserAdapter extends BaseAdapter {

        private static final String LOG_TAG = "SelectUserAdapter";
        public List<SelectUser> _data;
        Context _c;
        ViewHolder v;
        private ArrayList<SelectUser> arrayList;
        private HashMap<Integer, Boolean> mSelection = new HashMap<>();
        private HashMap<Integer, Boolean> selectedUserMap = new HashMap<>();
        //RoundImage roundedImage;

        public SelectUserAdapter(List<SelectUser> selectUsers, Context context) {
            _data = selectUsers;
            _c = context;
            this.arrayList = new ArrayList<SelectUser>();
            this.arrayList.addAll(_data);
        }

        public void clearSelection() {
            mSelection = new HashMap<Integer, Boolean>();
            notifyDataSetChanged();
        }

        public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            Log.i(TAG, "Setting the user to true: " + position + ": " + value);
            selectedUserMap.put(position, value);
            notifyDataSetChanged();
        }

        public HashMap getSelectedUserMap() {
            HashMap<Integer, Boolean> tempMap = new HashMap<>();
            Set<Integer> positionKeySet = selectedUserMap.keySet();
            for (Integer key : positionKeySet) {
                if (selectedUserMap.get(key) == true)
                    tempMap.put(key, true);
            }

            Set<Integer> tempMapKeySet = tempMap.keySet();
            for (Integer key : tempMapKeySet) {
                Log.i(TAG, key + "");
            }

            return tempMap;
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            Log.i(TAG, "Setting position " + position + " to be false.");
            selectedUserMap.put(position, false);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public Object getItem(int i) {
            return _data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Log.i(TAG, "The value of position received is: " + position);
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.contact_info, null);
                //Log.e("Inside", "Here in View 1: " + position);
            } else {
                view = convertView;
                //view.setBackgroundColor(_c.getResources().getColor(android.R.color.holo_blue_light));
                //Log.e("Inside", "Here in View 2: " + position);
            }

            v = new ViewHolder();

            v.title = (TextView) view.findViewById(R.id.name);
            v.phone = (TextView) view.findViewById(R.id.no);
            v.imageView = (ImageView) view.findViewById(R.id.pic);

            final SelectUser data = _data.get(position);
            v.title.setText(data.getName());
            v.phone.setText(data.getPhone());

            if (mSelection.get(position) != null) {
                Log.i(LOG_TAG, "Control in here..mSelection " + position + " " + mSelection.get(position).toString() + "View ID: " + view.getId());
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.holo_green_light));// this is a selected position so make it red
            } else {
                Log.i(LOG_TAG, "Control in here 2..mSelection " + position + "View ID 2: " + view.getId());
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.white));// this is a un selected position so make it white
            }

            view.setTag(data);

            if (position == mSelectedItem) {
                Log.i(LOG_TAG, "Control in here.. " + position);
                Log.i(LOG_TAG, "Control in here second. mSelectedItem. " + mSelectedItem);
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.white));
            }
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            _data.clear();
            if (charText.length() == 0) {
                _data.addAll(arrayList);
            } else {
                for (SelectUser wp : arrayList) {
                    if (wp.getName().toLowerCase(Locale.getDefault())
                            .startsWith(charText)) {
                        _data.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }
}