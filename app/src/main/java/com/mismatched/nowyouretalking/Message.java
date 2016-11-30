package com.mismatched.nowyouretalking;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jamie on 29/11/2016.
 */

public class Message extends AppCompatActivity {

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);

        //get conversation and participant selected
        Bundle extras = getIntent().getExtras();
        final String conversation = extras.getString("conversation");
        final String participant = extras.getString("Participant");

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Messages/"+conversation);

        //layout
        final LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.messagesLayout);


        myRef.addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clear view first
                myLinearLayout.removeAllViews();

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //make sure text field is set
                    if(child.child("text").getValue() != null){

                    //get conversation messages
                    final String to = child.child("to").getValue().toString();
                    final String from = child.child("from").getValue().toString();
                    final String text = child.child("text").getValue().toString();

                    //create textview and style
                    final TextView rowTextView = new TextView(Message.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    rowTextView.setLayoutParams(params);
                    rowTextView.setText(text);
                    rowTextView.setPadding(40,0,40,0);
                    rowTextView.setTextColor(ContextCompat.getColor(Message.this, R.color.White));
                    rowTextView.setGravity(Gravity.BOTTOM);

                    //set layout if to or from user
                    if(from.contains(getUserProfile.uid)){
                        params.setMargins(0, 5, 150, 5);
                        params.gravity = Gravity.END;
                        rowTextView.setBackgroundColor(ContextCompat.getColor(Message.this, R.color.colorPrimary));
                    }
                    else if(to.contains(getUserProfile.uid)){
                        params.setMargins(150, 5, 0, 5);
                        rowTextView.setGravity(Gravity.START);
                        rowTextView.setBackgroundColor(ContextCompat.getColor(Message.this, R.color.colorAccent));
                    }

                    // add to view
                    myLinearLayout.addView(rowTextView);

                    }// end null check

                } // end database loop
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //get entered text
                EditText messageText = (EditText) findViewById(R.id.MessageText);
                String sendingText = messageText.getText().toString();

                //check if not empty
                if (!sendingText.isEmpty()){
                //create push value
                DatabaseReference pushval = myRef.push();

                //upload message
                pushval.child("to").setValue(participant);
                pushval.child("from").setValue(getUserProfile.uid);
                pushval.child("text").setValue(sendingText);
                }
                else{
                    Toast.makeText(Message.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
            }

        });

}

}
