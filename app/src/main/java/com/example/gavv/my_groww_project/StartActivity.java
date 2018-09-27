package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if ( FirebaseAuth.getInstance().getUid() != null) {
            Log.d("Firebase UID", FirebaseAuth.getInstance().getUid());
        } else {
            Log.d("USER ID", "IS NULL");
        }

        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent registration_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(registration_intent);
            }
        });


        mLoginBtn = (Button) findViewById(R.id.start_login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }
}
