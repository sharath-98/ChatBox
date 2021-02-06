package com.example.sharath.chatbox;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;


    //-----------------------------
    private Button mRegBtn;
    private Button mLoginBn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //--------SPLASH------
 /*       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent homeintent = new Intent(StartActivity.this,HomeActivity.class);
                startActivity(homeintent);
                finish();
            }
        },SPLASH_TIME_OUT);


*/
        //----------------------
        mRegBtn = findViewById(R.id.start_reg_btn);
        mLoginBn=findViewById(R.id.start_login_btn);

        mLoginBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login_intent);
            }
        });


        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });

    }
}
