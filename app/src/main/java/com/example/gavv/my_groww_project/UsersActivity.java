package com.example.gavv.my_groww_project;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private RecyclerView mUsersList;

    private DatabaseReference mUserDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appBar);
        mUsersList = findViewById(R.id.users_list);
        mUsersList.hasFixedSize();
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUserDatabase, Users.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options){

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UsersViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false));

            }
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);
                    }
                });




//                holder.setThumb_image(model.getThumb_image(),getApplicationContext());
//                final String user_id=getRef(position).getKey();
//                holder.mview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent9=new Intent(UsersActivity.this,ProfileActivity.class);
//                        intent9.putExtra("user_id",user_id);
//                        startActivity(intent9);
//                    }
//                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setName(String name){
            TextView mUserNameView = mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            //should be .with(ctx)
            Picasso.get().load(thumb_image).placeholder(R.drawable.avatar_gcw).into(userImageView);
        }


    }
}
