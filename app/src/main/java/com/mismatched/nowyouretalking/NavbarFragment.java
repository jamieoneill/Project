package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by jamie on 27/11/2016.
 */

public class NavbarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        final View view = inflater.inflate(R.layout.navbar_fragment, null);

        //buttons
        final ImageButton SocialButton = (ImageButton) view.findViewById(R.id.SocialButton);
        final ImageButton LearnButton = (ImageButton) view.findViewById(R.id.LearnButton);
        final ImageButton profileButton = (ImageButton) view.findViewById(R.id.ProfileButton);
        ImageButton messagingButton = (ImageButton) view.findViewById(R.id.MessagingActivityButton);

        SocialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //set buttons
                SocialButton.setBackgroundResource(R.drawable.ic_social_selected);
                LearnButton.setBackgroundResource(R.drawable.ic_learn);
                profileButton.setBackgroundResource(R.drawable.ic_profile);

                //change fragment view
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SocialFragment social = new SocialFragment();
                fragmentTransaction.replace(R.id.fragment_container, social, "Social");
                fragmentTransaction.commit();
            }
        });

        LearnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //set buttons
                SocialButton.setBackgroundResource(R.drawable.ic_social);
                LearnButton.setBackgroundResource(R.drawable.ic_learn_selected);
                profileButton.setBackgroundResource(R.drawable.ic_profile);

                //change fragment view
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LearnFragment learn = new LearnFragment();
                fragmentTransaction.replace(R.id.fragment_container, learn, "Learn");
                fragmentTransaction.commit();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //set buttons
                SocialButton.setBackgroundResource(R.drawable.ic_social);
                LearnButton.setBackgroundResource(R.drawable.ic_learn);
                profileButton.setBackgroundResource(R.drawable.ic_profile_selected);

                //change fragment view
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfileFragment profile = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragment_container, profile, "Profile");
                fragmentTransaction.commit();
            }
        });

        messagingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //start message activity
                Intent intent = new Intent(getActivity(), MessagingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}