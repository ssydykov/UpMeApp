package com.example.saken.upmeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegistrationActivity extends ActionBarActivity {

    String email, password, userName, token, sessionId;
    EditText editTextEmail, editTextPassword, editTextUserName;
    Button btnSignUp;
    boolean success;

    private ProgressDialog pDialog;

    // URL to get contacts JSON
//    private static String url = "http://192.168.137.1:8000/mainapp/registration/";
    private static String url = "http://10.0.3.2:8000/mainapp/registration/";

    // JSON Node names
    private static final String TAG_MESSAGE = "Message";
    private static final String TAG_SUCCESS = "Success";
    private static final String TAG_TEXT = "Text";

    // URL to get contacts JSON
    String message_text = "";
    JSONObject messages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextEmail = (EditText)findViewById(R.id.editTextEmail);
                editTextPassword = (EditText)findViewById(R.id.editTextPassword);
                editTextUserName = (EditText)findViewById(R.id.editTextUserName);
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                userName = editTextUserName.getText().toString();

                Context context = getApplicationContext();
                new CreateNewUser(context).execute();
            }
        });
    }

    private class CreateNewUser extends AsyncTask<String, String, String> {

        Context context;
        private CreateNewUser(Context _context){ this.context = _context; }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RegistrationActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            ServiceHandler sh = new ServiceHandler();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("username", userName));

            String[] arrayListResponse = sh.makeServiceCall(url, ServiceHandler.SIGNUP,
                    params, null, null);

            token = arrayListResponse[0];
            sessionId = arrayListResponse[1];
            String jsonStr = arrayListResponse[2];

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    messages = jsonObj.getJSONObject(TAG_MESSAGE);

                    message_text = messages.getString(TAG_TEXT);
                    success = messages.getBoolean(TAG_SUCCESS);
                    Log.d("Success", Boolean.toString(success));

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

            if (success == false) {
                Toast.makeText(getBaseContext(), message_text,
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, CategoriesListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b = new Bundle();
                b.putString("csrftoken", token);
                b.putString("sessionid", sessionId);
                Log.d("token", token);
                Log.d("session id", sessionId);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

