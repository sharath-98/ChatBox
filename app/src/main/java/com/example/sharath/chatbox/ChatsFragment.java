package com.example.sharath.chatbox;




import android.content.Context;

import android.content.DialogInterface;

import android.content.Intent;

import android.graphics.Typeface;

import android.os.Bundle;

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

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;

import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;



import de.hdodenhof.circleimageview.CircleImageView;






public class ChatsFragment extends Fragment {



    private RecyclerView mConvList;



    private DatabaseReference mConvDatabase;

    private DatabaseReference mMessageDatabase;

    private DatabaseReference mUsersDatabase;



    private FirebaseAuth mAuth;



    private String mCurrent_user_id;



    private View mMainView;





    public ChatsFragment() {

        // Required empty public constructor

    }





    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {



        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);



        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);

        mAuth = FirebaseAuth.getInstance();



        mCurrent_user_id = mAuth.getCurrentUser().getUid();



        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);



        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        mUsersDatabase.keepSynced(true);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);

        linearLayoutManager.setStackFromEnd(true);



        mConvList.setHasFixedSize(true);

        mConvList.setLayoutManager(linearLayoutManager);





        // Inflate the layout for this fragment

        return mMainView;

    }





    @Override

    public void onStart() {

        super.onStart();



        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
       // Query conversationQuery = mMessageDatabase.orderByChild("time");



        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(

                Conv.class,

                R.layout.users_single_layout,

                ConvViewHolder.class,

                conversationQuery

        ) {

            @Override

            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {







                final String list_user_id = getRef(i).getKey();



                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);



                lastMessageQuery.addChildEventListener(new ChildEventListener() {

                    @Override

                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                        String data = dataSnapshot.child("message").getValue().toString();

                        convViewHolder.setMessage(data, conv.isSeen());



                    }



                    @Override

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {



                    }



                    @Override

                    public void onChildRemoved(DataSnapshot dataSnapshot) {



                    }



                    @Override

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {



                    }



                    @Override

                    public void onCancelled(DatabaseError databaseError) {



                    }

                });





                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {

                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {



                        final String userName = dataSnapshot.child("name").getValue().toString();

                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();



                        if(dataSnapshot.hasChild("status1")) {



                            String userOnline = dataSnapshot.child("status1").getValue().toString();

                            convViewHolder.setUserOnline(userOnline);



                        }



                        convViewHolder.setName(userName);

                        convViewHolder.setUserImage(userThumb, getContext());



                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {

                            @Override

                            public void onClick(View view) {





                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);

                                chatIntent.putExtra("user_id", list_user_id);

                                chatIntent.putExtra("user_name", userName);

                                startActivity(chatIntent);



                            }

                        });





                    }



                    @Override

                    public void onCancelled(DatabaseError databaseError) {



                    }

                });



            }

        };


        //25.2.19.....new line....
      //  updateToken(FirebaseInstanceId.getInstance().getToken());

        mConvList.setAdapter(firebaseConvAdapter);



    }

    //25.2.19....new func............
  /*  private void updateToken(String token){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        databaseReference.child(mCurrent_user_id).setValue(token1);
    }
*/






    public static class ConvViewHolder extends RecyclerView.ViewHolder {



        View mView;



        public ConvViewHolder(View itemView) {

            super(itemView);



            mView = itemView;



        }



        public void setMessage(String message, boolean isSeen){



            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);

            userStatusView.setText(message);



            if(isSeen){

                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);

            } else {

                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);

            }



        }



        public void setName(String name){



            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);

            userNameView.setText(name);



        }



        public void setUserImage(String thumb_image, Context ctx){



            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.cars).into(userImageView);



        }





        public void setUserOnline(String online_status) {



            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.img_on);
            ImageView userOfflineView = (ImageView) mView.findViewById(R.id.imf_of);


            if(online_status.equals("online")){



                userOnlineView.setVisibility(View.VISIBLE);
                userOfflineView.setVisibility(View.GONE);



            } else {


                userOnlineView.setVisibility(View.GONE);
                userOfflineView.setVisibility(View.VISIBLE);





            }



        }





    }







}
//---------------------------------------------------

/*
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;




    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_chats,container,false);
        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUserDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;


    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv,ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(

                Conv.class,
                R.layout.users_single_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {


                final String list_user_id = getRef(i).getKey();
                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }




    }
}
*/