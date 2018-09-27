package com.example.gavv.my_groww_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import models.Helper;

public class SignUpHelper extends Activity {

    private Button mButton;
    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_helper);

        mButton = findViewById(R.id.helperSignUp);
        mFname = findViewById(R.id.firstNameHelper);
        mLname = findViewById(R.id.lastNameHelper);
        mEmail = findViewById(R.id.signUpHelperEmail);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mPassword = findViewById(R.id.signUpHelperPassword);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName =  mFname.getText().toString();
                String lName = mLname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                Helper helper = new Helper(fName,lName,email);

                HashMap<String, Object> data = new HashMap<>();
                data.put("firstName", fName);
                data.put("lastName", lName);
                data.put("email", email);
                data.put("role", helper.getRole());

                createAccount(email,password);
                mDatabase.child("Users")
                        .child(FirebaseAuth.getInstance().getUid()).updateChildren(data);

            }
        });
    }
    // Function to create a firebase account
    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public static final String TAG = "GROWW_App" ;

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();
                } else{
                    Log.w(TAG, "createUserWithEmail:failure",task.getException());
                    Toast.makeText(SignUpHelper.this,"Authentication failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
