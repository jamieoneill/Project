package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocialFragment extends Fragment implements View.OnClickListener {

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View v = inflater.inflate(R.layout.social_fragment, null);

        //set title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.Social));

        //set buttons
        ImageButton AddMeetActivityButton = (ImageButton) v.findViewById(R.id.addBtn);
        AddMeetActivityButton.setOnClickListener(this);

        ImageButton FindMeetActivityButton = (ImageButton) v.findViewById(R.id.findBtn);
        FindMeetActivityButton.setOnClickListener(this);

        ImageButton ManageMeetActivityButton = (ImageButton) v.findViewById(R.id.manageBtn);
        ManageMeetActivityButton.setOnClickListener(this);

        //database ref
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Meetings");

        //set images(country flags) depending on language
       final int German = R.drawable.germanyicon;
       final int French = R.drawable.franceicon;
       final int Spanish = R.drawable.spainicon;

        final LinearLayout nextMeetUpLayout = (LinearLayout) v.findViewById(R.id.nextLayout);
        final ImageView flagimage = (ImageView) v.findViewById(R.id.flagImage);
        final TextView meetTitle = (TextView) v.findViewById(R.id.meetupTitleLbl);
        final TextView meetDate = (TextView) v.findViewById(R.id.meetupDateLbl);
        final TextView meetLocation = (TextView) v.findViewById(R.id.meetupLocationLbl);
        final TextView meetTime = (TextView) v.findViewById(R.id.meetupTimeLbl);

        final Date[] PrevMeeting = {new Date()};
        final Date[] theMeetingDate = {null};
        final int[] imageHolder = new int[1];
        final String[] titleHolder = {null};
        final String[] timeHolder = {null};
        final String[] locationHolder = {null};

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    //find out if spaces are available
                    String MeetingDate = child.child("MeetingDate").getValue(String.class);

                    //get users attending
                    String Attending =  child.child("Attending").getValue().toString();

                    //check the dates
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        theMeetingDate[0] = sdf.parse(MeetingDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //if user is attending display it
                    if(Attending.contains(getUserProfile.uid)) {

                        if (theMeetingDate[0].compareTo(PrevMeeting[0]) > 0) {
                            //set this as the next meet up
                            PrevMeeting[0] = theMeetingDate[0];
                            String Language = child.child("Language").getValue().toString();
                            titleHolder[0] = child.child("Title").getValue(String.class);
                            locationHolder[0] = child.child("Location").getValue(String.class);
                            timeHolder[0] = child.child("MeetingTime").getValue(String.class);
                            switch (Language){
                                case "German":
                                    imageHolder[0] = German;
                                    break;
                                case "Spanish":
                                    imageHolder[0] = Spanish;
                                    break;
                                case "French":
                                    imageHolder[0] = French;
                                    break;
                            }
                            //update the screen
                            flagimage.setBackgroundResource(imageHolder[0]);
                            String dateCut = String.valueOf(theMeetingDate[0]).substring(0, 10);
                            meetTitle.setText(String.valueOf(titleHolder[0]));
                            meetDate.setText(dateCut);
                            meetLocation.setText(String.valueOf(locationHolder[0]));
                            meetTime.setText(String.valueOf(timeHolder[0]));

                        }

                    }else if (!Attending.contains(getUserProfile.uid) && titleHolder[0] == null){
                        nextMeetUpLayout.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //database ref
        DatabaseReference myRef2 = database.getReference("PastMeetings");

        final int[] numOfMeetings = {0};

        // Read from the database
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //get users attending
                    String Attending =  child.child("Attending").getValue().toString();

                    //if user is attending display it
                    if(Attending.contains(getUserProfile.uid)) {

                        for (final DataSnapshot attendees : child.child("Attending").getChildren()) {
                            //get the users in attending
                            String name = attendees.getValue().toString();
                            //get the num of meetings this user has attended
                            if(name.equals(getUserProfile.uid)){
                                numOfMeetings[0] = numOfMeetings[0] + 1;
                            }
                        }//end for
                    }
                }
                //check Achievements
                if(numOfMeetings[0] >=3){
                        AchievementHelper AchievementHelperClass = new AchievementHelper();
                        AchievementHelperClass.UnlockAchievement("Attend3MeetUps", getActivity());
                }
                if(numOfMeetings[0] >=10){
                    AchievementHelper AchievementHelperClass = new AchievementHelper();
                    AchievementHelperClass.UnlockAchievement("Attend10MeetUps", getActivity());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("database ", "Failed to read value.", error.toException());
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        switch (v.getId()) {
            case R.id.addBtn:
                intent = new Intent(getActivity(), MeetingActivity.class);
                startActivity(intent);
                break;
            case R.id.findBtn:
                intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.manageBtn:
                intent = new Intent(getActivity(), ManageActivity.class);
                startActivity(intent);
                break;
        }
    }
}