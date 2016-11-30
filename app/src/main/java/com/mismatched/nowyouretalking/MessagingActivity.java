package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jamie on 29/11/2016.
 */

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Messages");

        // get user info from profile class
        final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //if user is in this coversation
                    if(child.getKey().toString().contains(getUserProfile.uid)){

                        final String conversationkey = child.getKey();
                        Log.i("conversationkey ", conversationkey);
                        Log.i("children ", String.valueOf(child.getChildrenCount()));

                        //get the other user's name
                        final String participant = conversationkey.replace(getUserProfile.uid, "");

                        final DatabaseReference getusername = database.getReference("Users/"+participant);
                        getusername.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.i("child key111 ", dataSnapshot.child("Name").getValue().toString());

                                //get participant's name
                                String participantName = dataSnapshot.child("Name").getValue().toString();
                                TextView allMessagesView = new TextView(MessagingActivity.this);
                                allMessagesView.setText(participantName);

                                //add to all messages view here
                                final LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.messagesLayout);

                                //set participent name
                                allMessagesView.setText(participantName);
                                allMessagesView.setTypeface(Typeface.DEFAULT_BOLD);
                                allMessagesView.setBackgroundResource(R.drawable.border);
                                allMessagesView.layout(10, 0, 0, 0);
                                allMessagesView.setPadding(30, 30, 0, 30);

                                //add to view
                                myLinearLayout.addView(allMessagesView);

                                //set on click
                                allMessagesView.setClickable(true);
                                allMessagesView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        //send intent with participant name to message class
                                        Intent intent = new Intent(MessagingActivity.this, Message.class);
                                        intent.putExtra("conversation", conversationkey);
                                        intent.putExtra("Participant", participant);
                                        startActivity(intent);

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    } //end if user message

                } // end database loop
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
