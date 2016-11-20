package com.module1.triplak.moduleone.fragments;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.activity.SelectUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Himanjan on 4/23/2016.
 */
public class FragmentTwo extends Fragment {
    Cursor phones;
    ArrayList<SelectUser> selectUsers;
    ContentResolver resolver;
    int mSelectedItem;

    //List View to populate Contact List
    ListView listView;
    SelectFragmentUserAdapter adapter;

    public FragmentTwo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add this line in order for the fragment to display menu items.
        setHasOptionsMenu(true);

        selectUsers = new ArrayList<>();
        resolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment_two, container, false);

        //Read the phone contacts and populate the details in the Cursor, phones
        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        FetchContacts fetchContacts = new FetchContacts();
        fetchContacts.execute();

        listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class FetchContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            adapter = new SelectFragmentUserAdapter(selectUsers, getActivity());
            listView.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Get Contact list from Phone
            if (phones != null) {
                Log.e("Contacts Count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getActivity(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
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
                            Log.e("No Image Thumb", "No Image Available");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectUser selectUser = new SelectUser();

                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "No information available");
            }
            phones.close();
            return null;
        }
    }

    /**
     * Created by Himanjan on 31-01-2016.
     */
    public class SelectFragmentUserAdapter extends BaseAdapter {
        public List<SelectUser> _data;
        private ArrayList<SelectUser> selectUserArrayList;
        Context _c;
        ViewHolder v;
        private HashMap<Integer, Boolean> mSelection = new HashMap<>();

        public SelectFragmentUserAdapter(List<SelectUser> selectUsers, Context context) {
            _data = selectUsers;
            _c = context;
            this.selectUserArrayList = new ArrayList<>();
            this.selectUserArrayList.addAll(_data);
        }

        public void clearSelection() {
            mSelection = new HashMap<Integer, Boolean>();
            notifyDataSetChanged();
        }

        public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            notifyDataSetChanged();
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            notifyDataSetChanged();
        }

        public boolean isPositionChecked(int position) {
            Boolean result = mSelection.get(position);
            return result == null ? false : result;
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
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.contact_info, null);
                Log.e("Inside", "Here in View 1: " + i);
            } else {
                view = convertView;
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.holo_blue_light));
                Log.e("Inside", "Here in View 2: " + i);
            }

            v = new ViewHolder();

            v.title = (TextView) view.findViewById(R.id.name);
            v.phone = (TextView) view.findViewById(R.id.no);
            v.imageView = (ImageView) view.findViewById(R.id.pic);

            final SelectUser data = _data.get(i);
            v.title.setText(data.getName());
            v.phone.setText(data.getPhone());

            if (mSelection.get(i) != null) {
                //This is a selected position so make it red
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.holo_blue_light));
            }

            view.setTag(data);

            if (i == mSelectedItem) {
                view.setBackgroundColor(_c.getResources().getColor(android.R.color.holo_blue_light));
            }
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            _data.clear();
            if (charText.length() == 0) {
                _data.addAll(selectUserArrayList);
            } else {
                for (SelectUser wp : selectUserArrayList) {
                    if (wp.getName().toLowerCase(Locale.getDefault())
                            .startsWith(charText)) {
                        _data.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        ImageView imageView;
        TextView title, phone;
    }
}

