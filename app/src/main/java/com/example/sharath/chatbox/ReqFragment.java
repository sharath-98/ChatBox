package com.example.sharath.chatbox;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
public class ReqFragment extends Fragment {

    private View mView;
    RecyclerView request_list;
    DatabaseReference mReference,mFriend_Ref;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    String my_id,name,status;
    FirebaseAuth.AuthStateListener mAuthListener;



    public ReqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_req, container, false);
        request_list=mView.findViewById(R.id.chat_request_list);
        request_list.setHasFixedSize(true);
        mAuth=FirebaseAuth.getInstance();
        mFriend_Ref=FirebaseDatabase.getInstance().getReference().child("Users");
        my_id=mAuth.getCurrentUser().getUid();
        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference().child("Friend_req").child(my_id);
        request_list.setLayoutManager(new LinearLayoutManager(getActivity()));





        Request_Friend_list();

        return mView;
    }

    private void Request_Friend_list() {

        FirebaseRecyclerAdapter<Users,search_Holder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, search_Holder>(

                Users.class,
                R.layout.user_displays_layout,
                search_Holder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(final search_Holder viewHolder, Users model, int position) {

                final String friend_key=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                mFriend_Ref.child(friend_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        name=dataSnapshot.child("name").getValue().toString();
                                        status=dataSnapshot.child("status").getValue().toString();

                                        viewHolder.setName(name);
                                        viewHolder.setStatus(status);
                                    }






                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        request_list.setAdapter(firebaseRecyclerAdapter);

    }

    public static class search_Holder extends RecyclerView.ViewHolder{
        View mView;

        public search_Holder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setStatus(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_status);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.cars).into(userImageView);

        }





    }
}
