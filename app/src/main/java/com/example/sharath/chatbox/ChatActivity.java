package com.example.sharath.chatbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;






import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private ImageButton mChatAddBtn;

    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private String mChatUser;
    private FirebaseAuth mAuth;

    private android.support.v7.widget.Toolbar mChatToolbar;

    private DatabaseReference mRootRef,reference;

   // APIService apiService;
    //boolean notify=false;

    private TextView mTitleView;
   // private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private String mCurrentUserId;

    private RecyclerView mMessagesList;

    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;
    private int itemPos = 0;

    private String mLastKey="";
    private String mPrevKey="";

    private StorageReference mImageStorage;
    private FirebaseUser firebaseUser;

ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();


        mChatToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        //25.2.19.....................new line
       // apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setDisplayShowCustomEnabled(true);

//new line---------------------------------------------------------------------------------
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);


        mTitleView = findViewById(R.id.custom_bar_title);
      //  mLastSeenView=findViewById(R.id.custom_bar_seen);
        mProfileImage=(CircleImageView) findViewById(R.id.custom_bar_image);

       // mChatAddBtn = findViewById(R.id.chat_add_btn);
        mChatSendBtn = findViewById(R.id.chat_send_btn);
        mChatMessageView = findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = (RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);

        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);
        loadMessages();

     /*   mChatAddBtn=findViewById(R.id.chat_add_btn);
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

            }
        });

*/




        mTitleView.setText(userName);

        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mChatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("isseen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+ mCurrentUserId + "/" + mChatUser,chatAddMap);
                    chatUserMap.put("Chat/"+ mChatUser + "/" + mCurrentUserId,chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());

                            }
                        }
                    });



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //chats butoon
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                           //   notify=true;
                sendMessage();
;
            }
        });


        /*
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });

        */

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;

                itemPos=0;


                loadMoreMessages();

            }
        });



    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode ==RESULT_OK){
            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final  String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                    if(task.isSuccessful()){
                        String download_url = task.getResult().getDownloadUrl().toString();

                        //similar to sending message


                        Map messageMap = new HashMap();
                        messageMap.put( "/message",download_url);
                        messageMap.put( "/seen",false);
                        messageMap.put( "/type","image");
                        messageMap.put( "/time",ServerValue.TIMESTAMP);


                        Map messageUserMap = new HashMap();
                        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/message",download_url);
                        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/seen",false);
                        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/type","image");
                        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/time",ServerValue.TIMESTAMP);

                        messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/from",mCurrentUserId);




                        messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/message",download_url);
                        messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/seen",false);
                        messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/type","image");
                        messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/time",ServerValue.TIMESTAMP);

                        messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/from",mCurrentUserId);

                        mChatMessageView.setText("");


                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){
                                    Log.d("CHAT_LOG",databaseError.getMessage().toString());

                                }

                            }
                        });




                    }

                }
            });



        }



    }

*/

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++ ,message);


                }else {

                    mPrevKey = mLastKey;
                }

                if (itemPos == 1){


                    mLastKey=messageKey;

                }


                Log.d("TOTALKEYS","Last key:" + mLastKey + "| Prev Key :" + mPrevKey + "|message key:" + messageKey);
                mAdapter.notifyDataSetChanged();


                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);


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

    private void loadMessages(){

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        //   mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey=messageKey;
                    mPrevKey=messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

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

    //-----------------------------------------------new fn
    private void seenMessage(final String userid){

        reference=FirebaseDatabase.getInstance().getReference("Chat");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatt chatt=snapshot.getValue(Chatt.class);
                    if(chatt.getReceiver().equals(firebaseUser.getUid()) && chatt.getSender().equals(userid)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(){
        final String message = mChatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){

         //   String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
           // String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put( "/message",message);
            messageMap.put( "/iseen",false);
            messageMap.put( "/type","text");
            messageMap.put( "/time",ServerValue.TIMESTAMP);
//
            Map messageUserMap = new HashMap();
            messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/message",message);
            messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/iseen",false);
            messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/type","text");
            messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/time",ServerValue.TIMESTAMP);

            messageUserMap.put("messages/" + mCurrentUserId + "/" + mChatUser + "/" + push_id +"/from",mCurrentUserId);




            messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/message",message);
            messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/iseen",false);
            messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/type","text");
            messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/time",ServerValue.TIMESTAMP);

            messageUserMap.put("messages/" + mChatUser + "/" + mCurrentUserId + "/" + push_id +"/from",mCurrentUserId);

            mChatMessageView.setText("");




            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());

                    }

                }
            });



        }




    }








































    //new line added
    private void status1(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status1",status);
        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume(){
        super.onResume();
        status1("online");
    }
    @Override
    protected void onPause(){
        super.onPause();

        //new for seen thing

        status1("offline");
    }
}
