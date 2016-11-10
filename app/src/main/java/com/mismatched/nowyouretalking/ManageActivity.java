package com.mismatched.nowyouretalking;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jamie on 31/10/2016.
 */

public class ManageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        // get user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        //database ref
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Meetings");

        //set layout
        final LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout1);

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
                    if(Attending.contains(user.getUid())) {

                        // create a new textview
                        final TextView rowTextView = new TextView(ManageActivity.this);

                        //get info from DB
                        String Host = child.child("Host").getValue(String.class);
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
                            if(name.equals(user.getUid())){
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

                                builder.setTitle(Titles)
                                        .setMessage(Locations + "\n" + MeetingTime + "\n" + MeetingDate + "\n" + Language + "\n" + MinLevel + "\n" + MaxLevel + "\n" + NumGuests + "\n" + Note)
                                        .setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //remove the user from selected meet up
                                                Toast.makeText(ManageActivity.this, "Removed from meet up" , Toast.LENGTH_SHORT).show();
                                                myRef.child(meetup).child("Attending").child(finalUserKey).removeValue();

                                                //hide meetup for now. will not display on next load
                                                rowTextView.setVisibility(View.GONE);
                                            }
                                        });
                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    //cancel button, do nothing
                                    }
                                });

                                //show dialog
                                builder.show();

                            }
                        });

                        // add the textview to the linearlayout
                        myLinearLayout.addView(rowTextView);
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

}
