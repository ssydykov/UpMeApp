package com.example.saken.upmeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends ActionBarActivity {

    // Urls
    private static String url = "http://10.0.3.2:8000/mainapp/getmycategorylist/";
    private static String url2 = "http://10.0.3.2:8000/mainapp/getmyprofile/";

    // JSON Node names
    private static final String TAG_MESSAGE = "Message";
    private static final String TAG_ID = "subcategory";
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_SUBCATEGORY_NAME = "subcategory_name";
    private static final String TAG_FIRST_NAME = "first_name";
    private static final String TAG_LAST_NAME = "last_name";

    JSONArray categories = null;
    JSONObject profile = null;

    final String ATTRIBUTE_NAME_CATEGORY_NAME = "category_name";
    final String ATTRIBUTE_NAME_SUBCATEGORY_NAME = "subcategory_name";
    final String ATTRIBUTE_NAME_CATEGORY_IMAGE = "category_image";
    String categoryNames[];
    String subcategoryNames[];
    String categoryImages[] = {
            "http://icons.iconarchive.com/icons/alecive/flatwoken/512/Apps-Libreoffice-Math-icon.png",
            "https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/256/physics.png"
    };
    Drawable categoryDrawable[];
    int categoryIds[];

    TextView textViewUserName, textViewLevel, textViewCity;
    String userName, level, city, avatar;
    SimpleAdapter adapter;
    GridView gv;
    Intent intent;
    ProgressDialog pDialog;
    List categoriesList;
    ImageView imageViewCategory;
    ImageView imageViewAvatar;
    Bitmap bitmap;
    String token, sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent = getIntent();
        token = intent.getStringExtra("csrftoken");
        sessionId = intent.getStringExtra("sessionid");

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withSelectedItem(0)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_user).withIcon(FontAwesome.Icon.faw_user),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_categories).withIcon(FontAwesome.Icon.faw_list),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_friends).withIcon(FontAwesome.Icon.faw_users),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_games).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(1),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_sign_out)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        if (position == 1) {

                        } else if (position == 2) {
                            Context context = getApplicationContext();
                            Intent intent = new Intent(context, CategoriesListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle b = new Bundle();
                            b.putString("csrftoken", token);
                            b.putString("sessionid", sessionId);
                            intent.putExtras(b);
                            context.startActivity(intent);
                        }
                    }
                })
                .build();

        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewCity = (TextView) findViewById(R.id.textViewCity);
        textViewLevel = (TextView) findViewById(R.id.textViewLevel);
        imageViewCategory = (ImageView) findViewById(R.id.categoryImage);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        new GetProfile().execute();
    }

    private class GetProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage("Загрузка Категорий...");
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
                    categories = jsonObj.getJSONArray(TAG_MESSAGE);
                    categoryNames = new String[categories.length()];
                    subcategoryNames = new String[categories.length()];
                    categoryDrawable = new Drawable[categories.length()];
                    categoryIds = new int[categories.length()];

                    // looping through All Contacts
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject c = categories.getJSONObject(i);

                        categoryIds[i] = c.getInt(TAG_ID);
                        categoryNames[i] = c.getString(TAG_CATEGORY_NAME);
                        subcategoryNames[i] = c.getString(TAG_SUBCATEGORY_NAME);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            String[] arrayListResponse2 = sh.makeServiceCall(url2, ServiceHandler.GET,
                    null, token, sessionId);
            String jsonStr2 = arrayListResponse2[2];
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr2 != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr2);

                    // Getting JSON Array node
                    profile = jsonObj.getJSONObject(TAG_MESSAGE);
                    userName = profile.getString(TAG_FIRST_NAME) +
                            " " + profile.getString(TAG_LAST_NAME);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            try {
                for (int i = 0; i < categoryImages.length; i++){

                    URL url = new URL(categoryImages[i]);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    categoryDrawable[i] = new BitmapDrawable(getResources(), bitmap);
                    connection.disconnect();
                    Log.d("Hello", "Hello");
                }

                URL url = new URL("http://st-im.kinopoisk.ru/im/wallpaper/1/6/8/kinopoisk.ru-Million-Dollar-Baby-168559--w--800.jpg");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                connection.disconnect();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            textViewUserName.setText(userName);
            imageViewAvatar.setImageBitmap(bitmap);

            // Setting Values to List View From Database: **************
            categoriesList = new ArrayList();
            for (int i = 0; i < categoryNames.length; i++){

                categoriesList.add(new CategoryModel(categoryNames[i], subcategoryNames[i], categoryImages[i]));
            }

            gv = (GridView) findViewById(R.id.gridViewCategories);
            gv.setAdapter(new CategoryListAdapter(ProfileActivity.this, categoriesList));

//            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
//                    categoryNames.length);
//            Map<String, Object> m;
//
//            for (int i = 0; i < categoryNames.length; i++) {
//                m = new HashMap<String, Object>();
//                m.put(ATTRIBUTE_NAME_CATEGORY_NAME, categoryNames[i]);
//                m.put(ATTRIBUTE_NAME_SUBCATEGORY_NAME, subcategoryNames[i]);
//                m.put(ATTRIBUTE_NAME_CATEGORY_IMAGE, categoryDrawable[i]);
//                data.add(m);
//            }
//
//            String[] from = { ATTRIBUTE_NAME_CATEGORY_NAME, ATTRIBUTE_NAME_SUBCATEGORY_NAME,
//                    ATTRIBUTE_NAME_CATEGORY_IMAGE };
//            int[] to = { R.id.categoryName, R.id.subcategoryName, R.id.categoryImage };
//
//            adapter = new SimpleAdapter(ProfileActivity.this, data,
//                    R.layout.activity_categories_list_item, from, to);
//            gv.setAdapter(adapter);

            // Item selected:

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Object activity = ChooseOpponentActivity.class;
//                    Intent intent = new Intent(getApplicationContext(),
//                            (Class<?>) activity);
//                    intent.putExtra("categoryId", categoriesId[position]);
//                    intent.putExtra("categoryName", categoriesName[position]);
//                    intent.putExtra("token", token);
//                    intent.putExtra("sessionId", sessionId);
//                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
