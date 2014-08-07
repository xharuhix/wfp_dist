package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;

public class WelcomePage extends Activity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // check if user already accept the TOS or not
        if(sharedPref.getBoolean(SharedPrefResources.PREFERENCE_KEY_ACCEPT_TOS, false)) {
            startLogin();
        }

        final CheckBox iAgree = (CheckBox) findViewById(R.id.checkBox_agreement);
        Button signUp = (Button) findViewById(R.id.button_sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iAgree.isChecked()){
                    startLogin();
                    sharedPref.edit().putBoolean(SharedPrefResources.PREFERENCE_KEY_ACCEPT_TOS, true).commit();
                }
                else{
                    Toast.makeText(WelcomePage.this,R.string.welcome_accept_terms_warning,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startLogin(){
        Intent main_intent = new Intent(WelcomePage.this, LoginActivity.class);
        startActivity(main_intent);
        this.finish();
    }

}
