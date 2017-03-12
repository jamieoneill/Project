package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Create the adapter that will return a fragment for each.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //returning current tabs

            switch (position) {
                case 0:
                    return new MessagingFragment();
                case 1:
                    return new FriendFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Get a string resource from Resources
            String messagesString =  getResources().getString(R.string.Message);
            String friendsString =  getResources().getString(R.string.Friends);

            switch (position) {
                case 0:
                    return messagesString;
                case 1:
                    return friendsString;
            }
            return null;
        }
    }

    public void onBackPressed(){
        //exit app on back button
        Intent intent = new Intent(MessagingActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
