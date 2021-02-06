package com.example.sharath.chatbox;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle toggle;

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUuserRef;

    private TabLayout mTablayout;

    //new line
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SkyWay");
        //-------------------------------------------------------------------------------------------new line
firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        // mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTablayout = findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPager);


//            return super.onOptionsItemSelected(item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

         toggle = new ActionBarDrawerToggle(

                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();
        getSupportActionBar().setTitle("ChatBox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
           sendToStart();


        }
       // else {
         //  mUuserRef.child("online").setValue(true);
        //}
    }



//any error occurs remove this first
  /*
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){


            mUuserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        //    mUuserRef.child("online").setValue(false);
        }


    }
*/
    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);

         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.main_settings_btn) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if (item.getItemId() == R.id.main_all_user_btn) {
            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return true;



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        //handle the sections



        if(id==R.id.post){

            Intent intent = new Intent(getApplicationContext(),MainActivity1.class);

            startActivity(intent);

        }


        else if (id == R.id.remainder) {

            Intent remainderpage = new Intent(getApplicationContext(), remainder_page.class);

            startActivity(remainderpage);

        }

        else if(id==R.id.theme){

            Intent intent = new Intent(getApplicationContext(),theme.class);

            startActivity(intent);

        }

        else if(id==R.id.about){

            Intent intent = new Intent(getApplicationContext(),about_page.class);

            startActivity(intent);

        }
        else if(id==R.id.camera){

            Intent intent = new Intent(getApplicationContext(),camera.class);

            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

//new line added
    private void status1(String status){
        mUuserRef=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status1",status);
        mUuserRef.updateChildren(hashMap);
    }
    @Override
    protected void onResume(){
        super.onResume();
        status1("online");
    }
    @Override
    protected void onPause(){
        super.onPause();
        status1("offline");
    }

}
