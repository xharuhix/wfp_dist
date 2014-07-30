package com.ulap_research.weatherforecasterproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.R;
import com.ulap_research.weatherforecasterproject.Resources.SharedPreferencesResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mRegisterTask;

    private static final String TAG = "Register";

    // UI references.
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set back button at the app title
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up SharedPreferences
        sharedPref = this.getSharedPreferences(SharedPreferencesResources.PREFERENCE_FILE_KEY, MODE_PRIVATE);

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message)); // message
        progressDialog.setCancelable(false);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    attemptRegister();
                }
                else {
                    Toast.makeText(Register.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startMain(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        this.finish();
    }

    public void showProgress(final boolean show) {
        if(show) {
            progressDialog.show();
        }
        else if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if(!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        else if(!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegisterTask = new UserRegisterTask(username, email, password);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.matches("^[A-Za-z0-9]{6,}(?:[_-][A-Za-z0-9]+)*$") && username.length()<=20;
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent loginIntent = new Intent(this, Login.class);
                startActivity(loginIntent);
                this.finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    /**
     * An asynchronous registration task
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            RestClient client = new RestClient(RestResources.REGISTER_URL);
            client.addParam("username", mUsername);
            client.addParam("password", mPassword);
            client.addParam("email", mEmail);

            try {
                client.execute(RestClient.RequestMethod.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Register code: " + client.getResponseCode() + " msg: " + client.getErrorMessage());

            try {
                JSONObject jObject = new JSONObject(client.getResponse());

                if(!jObject.getBoolean("error")) {
                    // get API key from the response
                    sharedPref.edit().putString(SharedPreferencesResources.PREFERENCE_KEY_APIKEY, jObject.getString("apiKey")).commit();
                    Log.d(TAG, sharedPref.getString(SharedPreferencesResources.PREFERENCE_KEY_APIKEY,""));
                    return RestResources.USER_CREATED_SUCCESSFULLY;
                }
                else {
                    return jObject.getInt("responseCode");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return RestResources.USER_CREATE_FAILED;
        }

        @Override
        protected void onPostExecute(Integer state) {
            mRegisterTask = null;
            showProgress(false);

            if (state == RestResources.USER_CREATED_SUCCESSFULLY) {
                progressDialog.setMessage(getString(R.string.initializing_message));
                showProgress(true);

                // LOAD INITIAL DATA
                InitializeTask task = new InitializeTask();
                task.execute((Void) null);

            } else if (state == RestResources.ACCOUNT_ALREADY_EXISTED) {
                Toast.makeText(Register.this, R.string.error_account_already_exist, Toast.LENGTH_LONG).show();
            } else if (state == RestResources.USER_CREATE_FAILED) {
                Toast.makeText(Register.this, R.string.error_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }

    /**
     * An asynchronous initialization task
     */
    public class InitializeTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPreferencesResources.PREFERENCE_KEY_APIKEY, "");

            // Set up REST request
            RestClient clientGetUserInfo = new RestClient(RestResources.GET_USER_INFO_URL);
            clientGetUserInfo.addHeader("Authorization", apiKey);

            RestClient clientGetCropsList = new RestClient(RestResources.GET_CROPS_LIST);
            clientGetCropsList.addHeader("Authorization", apiKey);

            RestClient clientGetAchievementsList = new RestClient(RestResources.GET_ACHIEVEMENTS_LIST);
            clientGetAchievementsList.addHeader("Authorization", apiKey);

            try {
                clientGetUserInfo.execute(RestClient.RequestMethod.GET);
                clientGetCropsList.execute(RestClient.RequestMethod.GET);
                clientGetAchievementsList.execute(RestClient.RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "GetUserInfo code: " + clientGetUserInfo.getResponseCode() + " msg: " + clientGetUserInfo.getErrorMessage());
            Log.d(TAG, "GetCropsList code: " + clientGetCropsList.getResponseCode() + " msg: " + clientGetCropsList.getErrorMessage());
            Log.d(TAG, "GetAchievementsList code: " + clientGetAchievementsList.getResponseCode() + " msg: " + clientGetAchievementsList.getErrorMessage());

            try {
                String userInfo = clientGetUserInfo.getResponse();
                String cropsList = clientGetCropsList.getResponse();
                String achievementsList = clientGetAchievementsList.getResponse();

                if (userInfo ==  null || cropsList == null || achievementsList == null) {
                    return true;
                }
                else {
                    sharedPref.edit().putString(SharedPreferencesResources.PREFERENCE_KEY_JSON_USER_INFO, userInfo).commit();
                    sharedPref.edit().putString(SharedPreferencesResources.PREFERENCE_KEY_JSON_CROPS_LIST, cropsList).commit();
                    sharedPref.edit().putString(SharedPreferencesResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST, achievementsList).commit();

                    Log.d(TAG, sharedPref.getString(SharedPreferencesResources.PREFERENCE_KEY_JSON_USER_INFO,""));
                    Log.d(TAG, sharedPref.getString(SharedPreferencesResources.PREFERENCE_KEY_JSON_CROPS_LIST,""));
                    Log.d(TAG, sharedPref.getString(SharedPreferencesResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST,""));

                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            showProgress(false);
            if(!error){
                startMain();
            }
            else {
                Toast.makeText(Register.this, R.string.error_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }

}
