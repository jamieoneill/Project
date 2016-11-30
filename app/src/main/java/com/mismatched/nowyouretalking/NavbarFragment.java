package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by jamie on 27/11/2016.
 */

public class NavbarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        final View view = inflater.inflate(R.layout.navbar_fragment, null);


    Button socialButton = (Button) view.findViewById(R.id.SocialButton);
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
            ImageView highligher = (ImageView) view.findViewById(R.id.highligherImage);
            TranslateAnimation highligherAnimation;

            //animate slider image based on previous frag
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

    Button learnButton = (Button) view.findViewById(R.id.LearnButton);
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
            ImageView highligher = (ImageView) view.findViewById(R.id.highligherImage);
            TranslateAnimation highligherAnimation;

            //animate slider image based on previous frag
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

    Button profileButton = (Button) view.findViewById(R.id.ProfileButton);
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
            ImageView highligher = (ImageView) view.findViewById(R.id.highligherImage);
            TranslateAnimation highligherAnimation;

            //animate slider image based on previous frag
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


        return view;
    }



}