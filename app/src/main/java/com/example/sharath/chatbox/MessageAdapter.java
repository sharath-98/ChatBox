package com.example.sharath.chatbox;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    public static final int MESSAGE_TYPE_LEFT=0;
    public static final int MESSAGE_TYPE_RIGHT=1;
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    //nwe line-----------------------------------
    private FirebaseUser fuser;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //--------------------------------------------------------------
        if(viewType==MESSAGE_TYPE_LEFT){

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout,parent,false);
            return new MessageViewHolder(v);
        }else {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout1,parent,false);
            return new MessageViewHolder(v);
        }
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
      //  public TextView messageReplText;
        public TextView timeText;
        public CircleImageView profileImage;
        public TextView txt_seen;

        public ImageView messageImage;

        public MessageViewHolder(View view){
            super(view);
            txt_seen=view.findViewById(R.id.txt_seen);
            messageText = view.findViewById(R.id.message_text_layout);
         //   messageReplText = view.findViewById(R.id.message_reply_layout);
          //  profileImage=(CircleImageView)view.findViewById(R.id.message_profile_layout);
         //   messageImage=(ImageView)view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i) {

        mAuth=FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
    //    String message_type = c.getType();



        if(from_user.equals(current_user_id)){

            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);


        }else {
            viewHolder.messageText.setBackgroundColor(Color.BLACK);
          //  viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);


        }

        viewHolder.messageText.setText(c.getMessage());

/*
        if(message_type.equals("text")){
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.messageText.setVisibility(View.INVISIBLE);

            Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.ic_file_download_black_24dp).into(viewHolder.messageImage);

        }
*/

    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
//-------------------------------------new code
@Override
    public int getItemViewType(int position){

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        if(mMessageList.get(position).getFrom().equals(fuser.getUid())){
            return MESSAGE_TYPE_RIGHT;
        }else {
            return MESSAGE_TYPE_LEFT;
        }
}
}
