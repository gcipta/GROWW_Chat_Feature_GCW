package com.example.gavv.my_groww_project;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;

    private DatabaseReference mChatDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mChatView;


    public ChatsFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatList = (RecyclerView) mChatView.findViewById(R.id.chat_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mChatDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mChatView;
    }




    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mChatDatabase, Friends.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter<Friends, ChatsFragment.ChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, ChatsFragment.ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsFragment.ChatViewHolder chatViewHolder, int position, @NonNull Friends friends) {

                        // UNDER CONSTRUCTION
                        chatViewHolder.setDate(friends.getDate());

                        final String list_user_id = getRef(position).getKey();

                        mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                    if (dataSnapshot.hasChild("online")) {

                                        String userOnline = dataSnapshot.child("online").getValue().toString();
                                        chatViewHolder.setUserOnline(userOnline);

                                    }

                                    chatViewHolder.setName(userName);
                                    chatViewHolder.setUserImage(userThumb, getContext());

                                    chatViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);


                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsFragment.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        return new ChatsFragment.ChatViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.users_single_layout, viewGroup, false));
                    }
                };

        mChatList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.avatar_gcw).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }


}


