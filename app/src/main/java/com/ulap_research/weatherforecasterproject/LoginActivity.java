package com.ulap_research.weatherforecasterproject;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 *
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends PlusBaseActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private static final String TAG = "LoginActivity";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up SharedPreferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, MODE_PRIVATE);

        // Check if user already logged in or not (from api key)
        if(!sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "").equals("")) {
            startMain();
        }

        // Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message)); // message
        progressDialog.setCancelable(false);

        // TODO: add this back when enable G+ login
//        Find the Google+ sign in button.
//        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
//        if (supportsGooglePlayServices()) {
//            // Set a listener to connect the user when the G+ button is clicked.
//            mPlusSignInButton.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    signIn();
//                }
//            });
//        } else {
//            // Don't offer G+ sign in if the app's version is too low to support Google Play
//            // Services.
//            mPlusSignInButton.setVisibility(View.GONE);
//            return;
//        }


        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if(isNetworkAvailable()) {
                        attemptLogin();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    attemptLogin();
                }
                else {
                    Toast.makeText(LoginActivity.this, R.string.error_network_connection, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                LoginActivity.this.finish();
            }
        });
    }

    private void startMain(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        this.finish();
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
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
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showProgress(final boolean show) {
        if(show) {
            progressDialog.show();
        }
        else if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /*
     * Handle google plus sign in (successfully connected)
     */
    @Override
    protected void onPlusClientSignIn() {

    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    /**
     * Called after successfully connected
     */
    @Override
    protected void updateConnectButtonState() {

    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.

    }

    @Override
    protected void onPlusClientSignOut() {

    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            RestClient client = new RestClient(RestResources.LOGIN_URL);
            client.addParam("username", mUsername);
            client.addParam("password", mPassword);

            try {
                client.execute(RestClient.RequestMethod.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "LoginActivity code: " + client.getResponseCode() + " msg: " + client.getErrorMessage());

            try {
                JSONObject jObject = new JSONObject(client.getResponse());
                if(!jObject.getBoolean("error")) {
                    // get API key from the response
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_APIKEY, jObject.getString("apiKey")).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return client.getResponseCode();
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            mAuthTask = null;
            showProgress(false);

            if (responseCode == 200) {
                // success
                progressDialog.setMessage(getString(R.string.initializing_message));
                showProgress(true);

                InitializeTask task = new InitializeTask();
                task.execute((Void) null);
            } else if (responseCode == 403) {
                // forbidden
                mPasswordView.setError(getString(R.string.error_login_failed));
                mPasswordView.requestFocus();
            } else {
                mPasswordView.setError(getString(R.string.error_something_went_wrong));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * An asynchronous initialization task (fetch all data and save it to shared preferences)
     */
    public class InitializeTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            /*
             *   Set up REST request
             */

            // Get user info
            RestClient clientGetUserInfo = new RestClient(RestResources.GET_USER_INFO_URL);
            clientGetUserInfo.addHeader("Authorization", apiKey);

            // Get user rank
            RestClient clientGetUserRank = new RestClient(RestResources.GET_USER_RANK);
            clientGetUserRank.addHeader("Authorization", apiKey);

            // Get global rank
            RestClient clientGetUserGlobalRank = new RestClient(RestResources.GET_USER_GLOBAL_RANK);
            clientGetUserGlobalRank.addHeader("Authorization", apiKey);

            // Get all crops list
            RestClient clientGetCropsList = new RestClient(RestResources.GET_CROPS_LIST);
            clientGetCropsList.addHeader("Authorization", apiKey);

            // Get user's crops
            RestClient clientGetUserCrops = new RestClient(RestResources.GET_USER_CROPS);
            clientGetUserCrops.addHeader("Authorization", apiKey);

            // Get all achievement list
            RestClient clientGetAchievementsList = new RestClient(RestResources.GET_ACHIEVEMENTS_LIST);
            clientGetAchievementsList.addHeader("Authorization", apiKey);

            // Get user's completed achievements
            RestClient clientGetUserAchievements = new RestClient(RestResources.GET_USERS_ACHIEVEMENTS);
            clientGetUserAchievements.addHeader("Authorization", apiKey);

            // Get lasted sensors list
            RestClient clientGetSensorsList = new RestClient(RestResources.GET_SENSORS_LIST);
            clientGetSensorsList.addHeader("Authorization", apiKey);

            try {
                clientGetUserInfo.execute(RestClient.RequestMethod.GET);
                clientGetUserRank.execute(RestClient.RequestMethod.GET);
                clientGetUserGlobalRank.execute(RestClient.RequestMethod.GET);
                clientGetCropsList.execute(RestClient.RequestMethod.GET);
                clientGetUserCrops.execute(RestClient.RequestMethod.GET);
                clientGetAchievementsList.execute(RestClient.RequestMethod.GET);
                clientGetUserAchievements.execute(RestClient.RequestMethod.GET);
                clientGetSensorsList.execute(RestClient.RequestMethod.GET);

                String userInfo = clientGetUserInfo.getResponse();
                String userRank = clientGetUserRank.getResponse();
                String userGlobalRank = clientGetUserGlobalRank.getResponse();
                String cropsList = clientGetCropsList.getResponse();
                String userCrops = clientGetUserCrops.getResponse();
                String achievementsList = clientGetAchievementsList.getResponse();
                String userAchievements = clientGetUserAchievements.getResponse();
                String sensorsList = clientGetSensorsList.getResponse();

                if (new JSONObject(userInfo).getBoolean("error") ||
                        new JSONObject(userRank).getBoolean("error") ||
                        new JSONObject(userGlobalRank).getBoolean("error") ||
                        new JSONObject(cropsList).getBoolean("error") ||
                        new JSONObject(userCrops).getBoolean("error") ||
                        new JSONObject(achievementsList).getBoolean("error") ||
                        new JSONObject(userAchievements).getBoolean("error") ||
                        new JSONObject(sensorsList).getBoolean("error")) {
                    return true;
                }
                else {
                    // put data to shared preferences
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_INFO, userInfo).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_RANK, userRank).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_GLOBAL_RANK, userGlobalRank).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_CROPS_LIST, cropsList).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_CROPS, userCrops).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_ACHIEVEMENT_LIST, achievementsList).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_USER_ACHIEVEMENTS, userAchievements).commit();
                    sharedPref.edit().putString(SharedPrefResources.PREFERENCE_KEY_JSON_SENSORS_LIST, sensorsList).commit();

                    // debug
                    Log.d(TAG, userInfo);
                    Log.d(TAG, userRank);
                    Log.d(TAG, userGlobalRank);
                    Log.d(TAG, cropsList);
                    Log.d(TAG, userCrops);
                    Log.d(TAG, achievementsList);
                    Log.d(TAG, userAchievements);
                    Log.d(TAG, sensorsList);

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
                Toast.makeText(LoginActivity.this, R.string.error_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }
}