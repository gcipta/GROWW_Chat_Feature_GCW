package com.example.gavv.my_groww_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView displayEmail;
    private TextView displayFName;
    private TextView displayLName;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        displayEmail = findViewById(R.id.userEmail);
        displayFName = findViewById(R.id.userFirstName);
        displayLName = findViewById(R.id.userLastName);
    }

    private void updateUI(FirebaseUser currentUser){
        String email = currentUser.getEmail();
        String fname = currentUser.getDisplayName();

        displayFName.setText("First Name: " + fname);
        displayEmail.setText("Email: " + email);

    }
}
