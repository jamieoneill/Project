package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
        TranslateAnimation moveLefttoRight = new TranslateAnimation(0, 300, 0, 0);
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

        //set social view on load
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SocialFragment social = new SocialFragment();
        fragmentTransaction.add(R.id.fragment_container, social, "Social");
        fragmentTransaction.commit();


        Button socialButton = (Button) findViewById(R.id.SocialButton);
        socialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //change fragment view
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SocialFragment social = new SocialFragment();
                fragmentTransaction.replace(R.id.fragment_container, social, "Social");
                fragmentTransaction.commit();

                //get previous fragment
                Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                String fragTag = previousFragment.getTag();

                //set image and animation
                ImageView highligher = (ImageView) findViewById(R.id.highligherImage);
                TranslateAnimation highligherAnimation;

                //animate slider image
                switch (fragTag){
                    case "Learn":
                        highligherAnimation = new TranslateAnimation(480, 0, 0 , 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                        break;
                    case "Profile":
                        highligherAnimation = new TranslateAnimation(962, 0, 0 , 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                }
            }
        });

        Button learnButton = (Button) findViewById(R.id.LearnButton);
        learnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //change fragment view
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LearnFragment learn = new LearnFragment();
                fragmentTransaction.replace(R.id.fragment_container, learn, "Learn");
                fragmentTransaction.commit();

                //get previous fragment
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                String fragTag = currentFragment.getTag();

                //set image and animation
                ImageView highligher = (ImageView) findViewById(R.id.highligherImage);
                TranslateAnimation highligherAnimation;

                //animate slider image
                switch (fragTag){
                    case "Social":
                        highligherAnimation = new TranslateAnimation(0, 480, 0 , 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                        break;
                    case "Profile":
                        highligherAnimation = new TranslateAnimation(962, 480, 0 , 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                }
            }
        });

        Button profileButton = (Button) findViewById(R.id.ProfileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfileFragment profile = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragment_container, profile, "Profile");
                fragmentTransaction.commit();

                //get previous fragment
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                String fragTag = currentFragment.getTag();

                //set image and animation
                ImageView highligher = (ImageView) findViewById(R.id.highligherImage);
                TranslateAnimation highligherAnimation;

                //animate slider image
                switch (fragTag){
                    case "Social":
                        highligherAnimation = new TranslateAnimation(0, 962, 0 , 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                        break;
                    case "Learn":
                        highligherAnimation = new TranslateAnimation(480, 962, 0, 0);
                        highligherAnimation.setDuration(200);
                        highligherAnimation.setFillAfter(true);
                        highligherAnimation.setFillEnabled(true);

                        highligher.startAnimation(highligherAnimation);
                }
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