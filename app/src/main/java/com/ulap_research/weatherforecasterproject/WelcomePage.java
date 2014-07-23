package com.ulap_research.weatherforecasterproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        final CheckBox iAgree = (CheckBox) findViewById(R.id.checkBox_agreement);
        Button signUp = (Button) findViewById(R.id.button_sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iAgree.isChecked()){
                    startLogin();
                }
                else{
                    Toast.makeText(WelcomePage.this,R.string.hello_world,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startLogin(){
        Intent main_intent = new Intent(WelcomePage.this, Login.class);
        startActivity(main_intent);
        this.finish();
    }

}
