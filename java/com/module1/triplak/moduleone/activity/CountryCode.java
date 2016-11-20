package com.module1.triplak.moduleone.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.module1.triplak.moduleone.R;
import com.module1.triplak.moduleone.config.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CountryCode extends AppCompatActivity {
    private ArrayAdapter<String> mCountryCodeAdapter;
    private static final String LOG_TAG = "CountryCode";
    CountryCodeAdapter newCountryCodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] countryCodes = {};

        List<String> countryCodesList = new ArrayList<>(Arrays.asList(countryCodes));

        mCountryCodeAdapter = new ArrayAdapter<String>(this, R.layout.list_item_country_code, R.id.list_item_country_code_textView, countryCodesList);
        ListView listView = (ListView) findViewById(R.id.countryCodeListView);
        listView.setAdapter(newCountryCodeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryAndPhoneCodes mCountryAndPhoneCodes = (CountryAndPhoneCodes) newCountryCodeAdapter.getItem(position);
                String mPhoneCode = mCountryAndPhoneCodes.getPhoneCode();
                Log.i(LOG_TAG, "Phone code picked is: " + mPhoneCode);
                Log.i(LOG_TAG, "Code is : " + mPhoneCode);
                Intent intent = new Intent();
                intent.putExtra("Code", mPhoneCode);
                setResult(2, intent);
                finish();
                Toast.makeText(getApplicationContext(), mPhoneCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    // To be implemented for other settings like portal and wait preferences
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_country_code, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search_country).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        searchView.setQueryHint("Enter country name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Some", "onQueryTextSubmit ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (newCountryCodeAdapter != null) {
                    newCountryCodeAdapter.filter(s);
                } else {
                }
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCountryWithPhoneCodesList();
    }

    private void getCountryWithPhoneCodesList() {
        FetchCountryCodes fetchCountryCodes = new FetchCountryCodes();
        fetchCountryCodes.execute();
    }

    public class FetchCountryCodes extends AsyncTask<Void, Void, List<CountryAndPhoneCodes>> {

        @Override
        protected List<CountryAndPhoneCodes> doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            String jsonDataString = null;

            try {
                Uri uri = Uri.parse(AppConfig.COUNTRY_PHONE_CODES);
                URL url = new URL(uri.toString());
                Log.v(LOG_TAG, "Built URI: " + uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                jsonDataString = stringBuffer.toString();
                Log.i(LOG_TAG, "JSON String: " + jsonDataString);

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                return getFormattedJSONData(jsonDataString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CountryAndPhoneCodes> result) {
            if (result != null) {
                mCountryCodeAdapter.clear();
                newCountryCodeAdapter = new CountryCodeAdapter(result, CountryCode.this);
                ListView listView = (ListView) findViewById(R.id.countryCodeListView);
                listView.setAdapter(newCountryCodeAdapter);
            }
        }

        private List<CountryAndPhoneCodes> getFormattedJSONData(String jsonDataString) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonDataString);
            JSONArray jsonArray = jsonObject.getJSONArray("codes");

            String[] resultData = new String[jsonArray.length()];
            List<CountryAndPhoneCodes> countryAndPhoneCodesList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                CountryAndPhoneCodes countryAndPhoneCodes = new CountryAndPhoneCodes();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String name = jsonObject1.getString("name");
                countryAndPhoneCodes.setCountryName(name);
                String phone_code = jsonObject1.getString("dial_code");
                countryAndPhoneCodes.setPhoneCode(phone_code);
                countryAndPhoneCodesList.add(countryAndPhoneCodes);

                resultData[i] = name + " - " + phone_code;
            }
            return countryAndPhoneCodesList;
        }
    }


    public class CountryCodeAdapter extends BaseAdapter {
        public List<CountryAndPhoneCodes> _data;
        private ArrayList<CountryAndPhoneCodes> array_list;
        Context _c;
        ViewHolder v;
        private HashMap<Integer, Boolean> mSelection = new HashMap<>();

        public CountryCodeAdapter(List<CountryAndPhoneCodes> selectUsers, Context context) {
            _data = selectUsers;
            _c = context;
            this.array_list = new ArrayList<>();
            this.array_list.addAll(_data);
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
                view = li.inflate(R.layout.country_code_info, null);
            } else {
                view = convertView;
            }

            v = new ViewHolder();

            v.countryName = (TextView) view.findViewById(R.id.countryName);

            v.phone = (TextView) view.findViewById(R.id.countryPhoneCode);


            final CountryAndPhoneCodes data = (CountryAndPhoneCodes) _data.get(i);
            v.countryName.setText(data.getCountryName());
            v.phone.setText(data.getPhoneCode());

            view.setTag(data);
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            _data.clear();
            if (charText.length() == 0) {
                _data.addAll(array_list);
            } else {
                for (CountryAndPhoneCodes wp : array_list) {
                    if (wp.getCountryName().toLowerCase(Locale.getDefault())
                            .startsWith(charText)) {
                        _data.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView countryName, phone;
    }
}