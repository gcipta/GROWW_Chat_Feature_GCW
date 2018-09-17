package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button helperActButton;
    Button helpeeActButton;
    Button signUpButton;

    public void goToMap() {

        Intent intent = new Intent(this, HelperMapsActivity.class);

        startActivity(intent);
    }

    public void goToSignUp() {

        Intent intent = new Intent(this, SignUpActivity.class);

        startActivity(intent);
    }

    public void goToHelpeeAct() {

        Intent intent = new Intent(this, HelpeeMapsActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helperActButton = (Button) findViewById(R.id.helperActButton);

        helpeeActButton = (Button) findViewById(R.id.helpeeActButton);

        signUpButton = (Button) findViewById(R.id.signUpButton);

        helperActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        helpeeActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHelpeeAct();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

    }

}
