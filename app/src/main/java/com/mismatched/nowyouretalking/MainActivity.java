package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d("testing", "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            //go to sign in
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }

        //animation
        // set walk picture
        ImageView boy_walkImage = (ImageView) findViewById(R.id.boy_walk_image);
        ImageView girl_walkImage = (ImageView) findViewById(R.id.girl_walk_image);
        boy_walkImage.setBackgroundResource(R.drawable.boy_walk_animate);
        girl_walkImage.setBackgroundResource(R.drawable.girl_walk_animate);


        //start animation of images
        AnimationDrawable boy_walk = (AnimationDrawable) boy_walkImage.getBackground();
        AnimationDrawable girl_walk = (AnimationDrawable) girl_walkImage.getBackground();
        boy_walk.start();
        girl_walk.start();

        //set directions
        TranslateAnimation moveLefttoRight = new TranslateAnimation(0, 250, 0, 0);
        TranslateAnimation moveRighttoLeft = new TranslateAnimation(250, 0, 0 , 0);

        //durations
        moveLefttoRight.setDuration(4000);
        moveLefttoRight.setFillAfter(true);
        moveRighttoLeft.setDuration(4000);
        moveRighttoLeft.setFillAfter(true);

        //start the moving along the screen
        boy_walkImage.startAnimation(moveLefttoRight);
        girl_walkImage.startAnimation(moveRighttoLeft);

        moveLefttoRight.setAnimationListener(new  Animation.AnimationListener() {

          @Override
           public void onAnimationStart(Animation animation) {

          }

            @Override
            public void onAnimationEnd(Animation animation) {
                //set pose at end of animation
                ImageView boy_walkImage = (ImageView) findViewById(R.id.boy_walk_image);
                ImageView girl_walkImage = (ImageView) findViewById(R.id.girl_walk_image);
                boy_walkImage.setBackgroundResource(R.drawable.boy_pose);
                girl_walkImage.setBackgroundResource(R.drawable.girl_pose);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
        });



        //activity buttons
        Button ProfileActivityButton = (Button) findViewById(R.id.ProfileActivityButton);
        ProfileActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);

                startActivity(intent);
            }
        });

        Button AddMeetActivityButton = (Button) findViewById(R.id.AddMeetActivityButton);
        AddMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeetingActivity.class);

                startActivity(intent);
            }
        });

        Button FindMeetActivityButton = (Button) findViewById(R.id.FindMeetActivityButton);
        FindMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });

        Button ManageMeetActivityButton = (Button) findViewById(R.id.ManageMeetActivityButton);
        ManageMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageActivity.class);

                startActivity(intent);
            }
        });

        Button TranslateActivityButton = (Button) findViewById(R.id.TranslateActivityButton);
        TranslateActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TranslateActivity.class);

                startActivity(intent);
            }
        });

    }

    public void onBackPressed(){
        //exit app on back button
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}