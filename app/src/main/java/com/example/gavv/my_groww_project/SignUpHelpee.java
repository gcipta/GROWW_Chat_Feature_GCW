package com.example.gavv.my_groww_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import models.Helpee;

public class SignUpHelpee extends Activity {

    Button mButton;
    EditText mFname;
    EditText mLname;
    EditText mEmail;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_helpee);

        mButton = findViewById(R.id.helpeeSignUp);
        mFname = findViewById(R.id.firstName);
        mLname = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.email);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = mFname.getText().toString();
                String lName = mLname.getText().toString();
                String email = mEmail.getText().toString();

                Helpee helpee = new Helpee(fName, lName, email);

                mDatabase.child("helpee").push().setValue(helpee);
            }
        });

    }

}
