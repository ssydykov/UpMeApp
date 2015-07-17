package com.example.saken.upmeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CategoriesListActivity extends ActionBarActivity {

    SimpleAdapter adapter;
    GridView gv;
    Intent intent;
    private ProgressDialog pDialog;
    String token, sessionId;

    // URL to get contacts JSON
    // private static String url = "http://192.168.137.1:8000/mainapp/categories/";
    // private static String url2 = "http://192.168.137.1:8000/mainapp/logout/";

    private static String url = "http://10.0.3.2:8000/mainapp/categories/";
    // JSON Node names
    private static final String TAG_CATEGORIES = "Categories";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    JSONArray categories = null;

    final String ATTRIBUTE_NAME_TEXT = "text";
    String categoriesName[];
    int categoriesId[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        intent = getIntent();
        token = intent.getStringExtra("csrftoken");
        sessionId = intent.getStringExtra("sessionid");

        new GetCategoriesList().execute();
    }

    private class GetCategoriesList extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CategoriesListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            ServiceHandler sh = new ServiceHandler();
            String[] arrayListResponse = sh.makeServiceCall(url, ServiceHandler.GET,
                    null, token, sessionId);
            String jsonStr = arrayListResponse[2];
            Log.d("Response: ", "> " + jsonStr);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    categories = jsonObj.getJSONArray(TAG_CATEGORIES);
                    categoriesName = new String[categories.length()];
                    categoriesId = new int[categories.length()];

                    // looping through All Contacts
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject c = categories.getJSONObject(i);

                        categoriesId[i] = c.getInt(TAG_ID);
                        categoriesName[i] = c.getString(TAG_NAME);
//                        Log.d("language", Integer.toString(language));
//                        Log.d("subject", Integer.toString(subject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Setting Values to List View From Database: **************

            gv = (GridView) findViewById(R.id.gridViewCategories);

            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                    categoriesName.length);
            Map<String, Object> m;

            for (int i = 0; i < categoriesName.length; i++) {
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_TEXT, categoriesName[i]);
                data.add(m);
            }

            String[] from = { ATTRIBUTE_NAME_TEXT };
            int[] to = { R.id.categoryName };

            adapter = new SimpleAdapter(CategoriesListActivity.this, data,
                    R.layout.activity_categories_list_item, from, to);
            gv.setAdapter(adapter);

            // Item selected:

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object activity = CategoryActivity.class;
                    Intent intent = new Intent(getApplicationContext(),
                            (Class<?>) activity);
                    intent.putExtra("categoryId", categoriesId[position]);
                    intent.putExtra("categoryName", categoriesName[position]);
                    intent.putExtra("token", token);
                    intent.putExtra("sessionId", sessionId);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories_list, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}