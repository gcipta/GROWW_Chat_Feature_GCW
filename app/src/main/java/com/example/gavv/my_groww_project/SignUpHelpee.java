package com.example.gavv.my_groww_project;

import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
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

import models.Helpee;

public class SignUpHelpee extends Activity {

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
        setContentView(R.layout.activity_sign_up_helpee);

        mButton = findViewById(R.id.helpeeSignUp);
        mFname = findViewById(R.id.firstName);
        mLname = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mPassword = findViewById(R.id.signUpHelpeePassword);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = mFname.getText().toString();
                String lName = mLname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                Helpee helpee = new Helpee(fName, lName, email);

                HashMap<String, Object> data = new HashMap<>();
                data.put("firstName", fName);
                data.put("lastName", lName);
                data.put("email", email);
                data.put("role", helpee.getRole());

                createAccount(email, password);
                mDatabase.child("Users").child("Helpees")
                        .child(FirebaseAuth.getInstance().getUid()).updateChildren(data);
            }
        });

    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public static final String TAG = "GROWW_App" ;

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();
                    Toast.makeText(SignUpHelpee.this, "User" + user.getEmail() + "created" , Toast.LENGTH_LONG).show();
                } else{
                    Log.w(TAG, "createUserWithEmail:failure",task.getException());
                    Toast.makeText(SignUpHelpee.this,"Authentication failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
