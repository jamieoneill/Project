package com.mismatched.nowyouretalking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatsActivity extends AppCompatActivity {

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //layouts
        final TextView pastMeetups = (TextView) findViewById(R.id.PastMeetupsResult);

        //database ref
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PastMeetings");

        final int[] numOfMeetings = {0};

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //get users attending
                    String Attending =  child.child("Attending").getValue().toString();

                    //if user is attending display it
                    if(Attending.contains(getUserProfile.uid)) {

                        //get info from DB
                        final String Host = child.child("Host").getValue(String.class);
                        final String Titles = child.child("Title").getValue(String.class);
                        final String Locations = child.child("Location").getValue(String.class);
                        final String MeetingTime = child.child("MeetingTime").getValue(String.class);
                        final String MeetingDate = child.child("MeetingDate").getValue(String.class);
                        final String Language = child.child("Language").getValue(String.class);
                        final int MinLevel = child.child("MinLevel").getValue(int.class);
                        final int MaxLevel = child.child("MaxLevel").getValue(int.class);
                        final int NumGuests = child.child("NumGuests").getValue(int.class);
                        final String Note = child.child("Note").getValue(String.class);
                        final String meetup = child.getKey();

                        for (final DataSnapshot attendees : child.child("Attending").getChildren()) {

                            //get the users in attending
                            String name = attendees.getValue().toString();

                            //get the num of meetings this user has attended
                            if(name.equals(getUserProfile.uid)){
                                numOfMeetings[0] = numOfMeetings[0] + 1;
                            }
                            Log.d("num of meet", String.valueOf(numOfMeetings[0]));

                        }//end for

                    }
                }
                //add past meets to screen
                pastMeetups.setText(String.valueOf(numOfMeetings[0]));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("database ", "Failed to read value.", error.toException());
            }
        });
    }
}
