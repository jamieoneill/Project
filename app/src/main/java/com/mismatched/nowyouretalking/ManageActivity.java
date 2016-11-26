package com.mismatched.nowyouretalking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by jamie on 31/10/2016.
 */

public class ManageActivity extends AppCompatActivity {

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        //database ref
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Meetings");

        //set dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);

        //TODO add loading bar as indicator because database can take time

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //get users attending
                    String Attending =  child.child("Attending").getValue().toString();

                    //if user is attending display it
                    if(Attending.contains(getUserProfile.uid)) {

                        // create a new textview
                        final TextView rowTextView = new TextView(ManageActivity.this);

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

                        String UserKey = null;
                        for (final DataSnapshot attendees : child.child("Attending").getChildren()) {

                            //get the users in attending
                            String name = attendees.getValue().toString();

                            //get the user's push key, used to remove them from meet up
                            if(name.equals(getUserProfile.uid)){
                                UserKey = attendees.getKey();
                            }
                        }

                        // set text
                        rowTextView.setText(Titles + "\n" + MeetingTime + " - " + MeetingDate);
                        rowTextView.setTypeface(Typeface.DEFAULT_BOLD);

                        // set properties of rowTextView
                        rowTextView.setBackgroundResource(R.drawable.border);
                        rowTextView.layout(10, 0, 0, 0);
                        rowTextView.setPadding(30, 30, 0, 30);

                        //set image based on language
                        if (Language.equals("French")) {
                            rowTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.franceicon, 0, 0, 0);
                        } else if (Language.equals("Spanish")) {
                            rowTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.spainicon, 0, 0, 0);
                        } else if (Language.equals("German")) {
                            rowTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.germanyicon, 0, 0, 0);
                        }
                        rowTextView.setCompoundDrawablePadding(50);

                        //initialize final userkey
                        final String finalUserKey = UserKey;

                        //set on click
                        rowTextView.setClickable(true);
                        rowTextView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                //get views for dialog.. using same layout as shown on maps
                                View dialogView = View.inflate(ManageActivity.this, R.layout.markinfo_layout, null);
                                final AlertDialog alertDialog = new AlertDialog.Builder(ManageActivity.this).create();

                                // set dialog
                                alertDialog.setView(dialogView);
                                alertDialog.show();

                                //set texts
                                TextView title = (TextView) dialogView.findViewById(R.id.TitleLable);
                                title.setText(Titles);
                                title.setHeight(150);
                                title.setGravity(Gravity.CENTER);

                                TextView info = (TextView) dialogView.findViewById(R.id.MeetingText);
                                info.setText("Address: " + Locations + "\nTime: " + MeetingTime + "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + " - " + MaxLevel + "\nSeats: " + NumGuests + "\nNote: " + Note);

                                //hide on cancel button
                                Button Cancel = (Button) dialogView.findViewById(R.id.CancelButton);
                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        //close dialog
                                        alertDialog.dismiss();
                                    }
                                });

                                //get directions
                                Button Directions = (Button) dialogView.findViewById(R.id.DirectionsButton);
                                Directions.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Locations);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        startActivity(mapIntent);
                                    }
                                });

                                //join to add user to meet up
                                Button Remove = (Button) dialogView.findViewById(R.id.JoinButton);
                                Remove.setText("Remove");
                                Remove.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        if (Host.equals(getUserProfile.name)){
                                            //delete whole meet up
                                            Toast.makeText(ManageActivity.this, "Meet up has been deleted" , Toast.LENGTH_SHORT).show();
                                            myRef.child(meetup).removeValue();

                                            //hide meetup for now. will not display on next load
                                            rowTextView.setVisibility(View.GONE);
                                        }
                                        else {

                                            //remove the user from selected meet up
                                            Toast.makeText(ManageActivity.this, "Removed from meet up", Toast.LENGTH_SHORT).show();
                                            myRef.child(meetup).child("Attending").child(finalUserKey).removeValue();

                                            //hide meetup for now. will not display on next load
                                            rowTextView.setVisibility(View.GONE);
                                        }
                                        //close dialog
                                        alertDialog.dismiss();

                                    }
                                });
                            }
                        });

                        //set in correct area and add to view layout
                        if (Host.equals(getUserProfile.name)){
                            LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.myMeetUpLayout);
                            myLinearLayout.addView(rowTextView);

                        }else{
                            LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.attendingMeetUpLayout);
                            myLinearLayout.addView(rowTextView);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("database ", "Failed to read value.", error.toException());
            }
        });

    }


    //Convert to get address
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }

    @Override
    public void onBackPressed(){
        //set to home screen on back button
        Intent intent = new Intent(ManageActivity.this, MainActivity.class);
        startActivity(intent);    }

}
