package com.example.gavv.my_groww_project;




import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import controllers.NotificationController;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    private String mRole;
    private boolean mMakingRequest;

    private boolean isHelperHelping;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase, Friends.class)
                .setLifecycleOwner(this)
                .build();

        // Get the role of the current user.
        DatabaseReference mRoleRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("role");

        mRoleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    mRole = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // If it is a helpee, get the status whether the helpee has made a help request or not.
        if (mRole != null && mRole.equals("helpee")) {
            DatabaseReference mIsMakingRequestRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("makingRequest");

            mIsMakingRequestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        mMakingRequest = dataSnapshot.getValue(boolean.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {

                        // UNDER CONSTRUCTION
                        friendsViewHolder.setDate(friends.getDate());

                        final String list_user_id = getRef(position).getKey();

                        mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("role")) {
                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                    final String userRole = dataSnapshot.child("role").getValue().toString();
                                    final String userDisplay = "[" + userRole + "] " + userName;

                                    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                                    if (dataSnapshot.hasChild("online")) {

                                        String userOnline = dataSnapshot.child("online").getValue().toString();
                                        friendsViewHolder.setUserOnline(userOnline);

                                    }

                                    friendsViewHolder.setName(userDisplay);
                                    friendsViewHolder.setUserImage(userThumb, getContext());

                                    friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            CharSequence options[] = new CharSequence[]{"Open Profile", "Send message", "Voice Call"};


                                            if (userRole.equals("helper") && mRole.equals("helpee") &&
                                                    !mMakingRequest) {

                                                options = new CharSequence[]{"Open Profile", "Send message", "Voice Call", "Send Help Request"};

                                            }


                                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                            builder.setTitle("Select Options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    //Click Event for each item.
                                                    //Go to profile
                                                    if (i == 0) {

                                                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                        profileIntent.putExtra("user_id", list_user_id);
                                                        startActivity(profileIntent);

                                                    }

                                                    // Send message
                                                    if (i == 1) {

                                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                        chatIntent.putExtra("user_id", list_user_id);
                                                        chatIntent.putExtra("user_name", userName);
                                                        startActivity(chatIntent);

                                                    }

                                                    // Send a help request
                                                    if (i == 3) {

                                                        // Check if the helper is helping others or not.
                                                        DatabaseReference mIsHelpingHelperRef =
                                                                FirebaseDatabase.getInstance()
                                                                        .getReference()
                                                                        .child("users")
                                                                        .child(list_user_id)
                                                                        .child("isHelping");


                                                        mIsHelpingHelperRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.exists()) {

                                                                    if (!dataSnapshot.getValue(boolean.class)) {
                                                                        isHelperHelping = dataSnapshot.getValue(boolean.class);

                                                                    } else {
                                                                        Log.d("Helper Status", "Your Helper is busy, please try to request help from another helper!");
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                        if (!isHelperHelping) {
                                                            // Set the making request flag on helpee's position to be true.
                                                            DatabaseReference mHelpeeRef = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrent_user_id);
                                                            mHelpeeRef.child("makingRequest").setValue(true);

                                                            // Include the helper UID on the helpee's position.
                                                            HashMap<String, Object> helperInfo = new HashMap<>();
                                                            helperInfo.put("helperUid", list_user_id);
                                                            mHelpeeRef.updateChildren(helperInfo);

                                                            // Set the helping flag on helper's position to be true
                                                            DatabaseReference mHelperRef = FirebaseDatabase.getInstance().getReference().child("users").child(list_user_id);
                                                            mHelperRef.child("isHelping").setValue(true);

                                                            // Include the helpee UID on the helper's position.
                                                            HashMap<String, Object> request = new HashMap<String, Object>();
                                                            request.put("requestHelpeeID", FirebaseAuth.getInstance().getCurrentUser()
                                                                    .getUid());
                                                            mHelperRef.updateChildren(request);

                                                            Intent requestIntent = new Intent(getContext(), HelpeeMapsActivity.class);
                                                            requestIntent.putExtra("helper_id", list_user_id);
                                                            startActivity(requestIntent);
                                                        }
                                                    }

                                                    // go to voice chat
                                                    if (i == 2) {
                                                        Intent voiceIntent = new Intent(getContext(), VoiceActivity.class);
                                                        voiceIntent.putExtra("user_id", mCurrent_user_id);
                                                        voiceIntent.putExtra("contact_id", list_user_id);
                                                        startActivity(voiceIntent);

                                                    }

                                                }
                                            });

                                            builder.show();
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
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        return new FriendsViewHolder(LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.users_single_layout, viewGroup, false));
                    }
                };

        mFriendsList.setAdapter(firebaseRecyclerAdapter);


    }

        public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
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
