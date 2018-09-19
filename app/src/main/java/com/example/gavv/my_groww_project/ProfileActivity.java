package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button edit_button;

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
        edit_button = findViewById(R.id.editProfile_button);

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEditProfile();
            }
        });

    }

    private void updateUI(FirebaseUser currentUser){
        String email = currentUser.getEmail();
        String fname = currentUser.getDisplayName();

    }

    private void toEditProfile(){
        Intent intent  = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}
