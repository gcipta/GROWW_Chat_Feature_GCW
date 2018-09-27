package com.example.gavv.my_groww_project;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    //Include the pager in the main activity
    private ViewPager mViewPager;
    // Can be done in the bottom, but do not fuss things so make a new class
    private SectionsPagerAdapter mSectionPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    private Button mNavigationButton;

    private static final String HELPEE = "helpee";
    private String role;
    private boolean isMakingRequest;
    private String helperUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("GROWW Chat");


        if(mAuth.getCurrentUser() != null){
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            Log.d("MAIN ACTIVITY USER", mAuth.getCurrentUser().getUid());

        }

        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionPagerAdapter = new SectionsPagerAdapter((getSupportFragmentManager()));

        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendToStart();
        }

        else{
            mUserRef.child("online").setValue(true);

            Log.d("MY USER ID", FirebaseAuth.getInstance().getUid());
            initialiseButtonandParameters();
        }

    }

    protected void onStop(){
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Set the status of the user to offline
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    // This will update the current state of the users (logged in/logged out)
    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    /**
     * A helper function to initialise the functionality of the buttons and parameters.
     */
    private void initialiseButtonandParameters() {

        // Get the user's role
        DatabaseReference mRoleRef = mUserRef.child("role");
        mRoleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    role = dataSnapshot.getValue(String.class);

                    // In case of the Helpee, check if the helpee has made a request or not.

                    if (role.equals(HELPEE)) {

                        DatabaseReference mMakingReqRef = mUserRef.child("makingRequest");

                        mMakingReqRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()) {

                                    isMakingRequest = dataSnapshot.getValue(boolean.class);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // Get the helper UID if exists.
                        DatabaseReference mHelperUid = mUserRef.child("helperUid");
                        mHelperUid.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    helperUid = dataSnapshot.getValue(String.class);
                                    Log.d("HELPER UID", dataSnapshot.getValue(String.class));
                                } else {
                                    Log.d("HELPER UID", " IS NULL!!");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // Add navigation button
        mNavigationButton = (Button) findViewById(R.id.nav_button);

        mNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                boolean isStartActivity = true;
                intent = new Intent(getApplicationContext(), HelperMapsActivity.class);

                Log.d("YOUR ROLE IS", role);

                if (role.equals(HELPEE) && isMakingRequest) {
                    intent = new Intent (getApplicationContext(), HelpeeMapsActivity.class);
                    intent.putExtra("helper_id", helperUid);
                } else if (role.equals(HELPEE) && !isMakingRequest) {
                    isStartActivity = false;
                    Toast.makeText(MainActivity.this, "Please make a help request " +
                            "first before using this feature!", Toast.LENGTH_LONG).show();
                }

                if (isStartActivity) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    //Action for selecting certain menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // Case: the menu selected is logout button
        if(item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        //Feature that let user to go to account settings
        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        //Users Activity Related Task
        if(item.getItemId() == R.id.main_all_btn){
            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }

        return true;
    }
}
