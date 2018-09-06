package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity {

    private Button helpeeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        helpeeButton = findViewById(R.id.helpeeButton);

        helpeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHelpeeActivity();
            }
        });
    }

    private void launchHelpeeActivity() {

        Intent intent =  new Intent(this, SignUpHelpee.class);
        startActivity(intent);
    }
}
