package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;
    Button signUpButton;
    Button helpeeButton;

    public void goToMap() {

        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }

    public void goToSignUp() {

        Intent intent = new Intent(this, SignUpActivity.class);

        startActivity(intent);
    }

    public void goToSignUpAsHelpee() {

        Intent intent = new Intent(this, SignUpHelpee.class);

        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        signUpButton = (Button) findViewById(R.id.signUpButton);

        helpeeButton = (Button) findViewById(R.id.helpeeButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

        helpeeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToSignUpAsHelpee();
            }
        });
    }

}
