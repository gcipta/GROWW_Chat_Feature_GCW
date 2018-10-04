package com.example.gavv.my_groww_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;

    private Toolbar mToolbar;

    //Database
    private DatabaseReference mDatabase;

    //Progress Dialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Setting the toolbar
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Android Fields
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateBtn = findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Getting the string from the items
                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                // initiate a Switch
                Switch simpleSwitch = (Switch) findViewById(R.id.helper_switch);
                // check current state of a Switch (true or false).
                Boolean switchState = simpleSwitch.isChecked();


                // Ensure that the field are all being filled up to register
                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Account will be ready in a while, Please Wait...");
                    //Keep on proceeding the process even though user is touching on outside part
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    // Proceed to registration process from the provided information
                    register_user(display_name, email, password, switchState);
                }


            }
        });
    }

    private void register_user(final String display_name, final String email, String password, final Boolean switchState){

        // Will listen to the registration till completed
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // User is registered
                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    // Getting the Directory for Database
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    //HashMap is needed to create complex data
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("device_token",device_token);
                    userMap.put("name", display_name);
                    userMap.put("status", "Hi There, I Love this GROWW App");
                    userMap.put("image", "default");
                    userMap.put("email", email);

                    // to prevent user on loading high resolution data
                    userMap.put("thumb_image", "default");

                    // Set role as helper
                    if (switchState){
                        userMap.put("role", "helper");
                        boolean isHelping = false;
                        userMap.put("isHelping", isHelping);
                    }

                    else{
                        userMap.put("role", "helpee");
                        boolean makingRequest = false;
                        userMap.put("makingRequest", makingRequest);
                    }


                    // Setting the value to the database
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                //Main Functionality for registration
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                    // Verified the account through email
                    current_user.sendEmailVerification();
                }

                // Generate Toast error
                else {
                    // If error occured, just hide the progess bar
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Unable to Sign In, Please Check the Form and Try Again.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


}
