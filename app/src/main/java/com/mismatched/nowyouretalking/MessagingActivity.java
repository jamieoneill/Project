package com.mismatched.nowyouretalking;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

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

                        //get conversation
                        final String conversationkey = child.getKey();

                        //get the other user's name
                        final String participant = conversationkey.replace(getUserProfile.uid, "");

                        final DatabaseReference getusername = database.getReference("Users/"+participant);
                        getusername.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //get participant's name
                                final String participantName = dataSnapshot.child("Name").getValue().toString();
                                TextView allMessagesView = new TextView(MessagingActivity.this);
                                allMessagesView.setText(participantName);


                                final ImageView profileImage = new ImageView(MessagingActivity.this);

                                // path to /data/data/nowyourtalking/app_data/imageDir
                                ContextWrapper cw = new ContextWrapper(MessagingActivity.this.getApplicationContext());
                                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                                File otheruser = new File(directory,participant); //file name of other user using the "from" id
                                Activity activity = MessagingActivity.this;

                                ProfileFragment profileFragment = new ProfileFragment();
                                //get image
                                if(otheruser.length() != 0){
                                    //if there is a image on device load it
                                    profileFragment.loadImageFromStorage(participant, profileImage, activity);
                                }
                                else{
                                    //else pull image from online storage
                                    profileFragment.loadProfileImage(participant, profileImage, activity);
                                }

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,200);
                                params.gravity = Gravity.START;
                                profileImage.setLayoutParams(params);
                                profileImage.setPadding(0,20,0,20);

                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                LinearLayout LL = new LinearLayout(MessagingActivity.this);
                                LL.setOrientation(LinearLayout.HORIZONTAL);
                                LL.setLayoutParams(params2);

                                //add to all messages view here
                                final LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.messagesLayout);

                                //set participent name
                                allMessagesView.setText(participantName);
                                allMessagesView.setTypeface(Typeface.DEFAULT_BOLD);
                                allMessagesView.layout(10, 0, 0, 0);
                                allMessagesView.setPadding(30, 50, 0, 30);

                                //add to view
                                LL.addView(profileImage);
                                LL.addView(allMessagesView);
                                LL.setBackgroundResource(R.drawable.border);

                                myLinearLayout.addView(LL);

                                //set on click
                                allMessagesView.setClickable(true);
                                allMessagesView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        //send intent with participant name to message class
                                        Intent intent = new Intent(MessagingActivity.this, Message.class);
                                        intent.putExtra("conversation", conversationkey);
                                        intent.putExtra("Participant", participant);
                                        intent.putExtra("ParticipantName", participantName);
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
