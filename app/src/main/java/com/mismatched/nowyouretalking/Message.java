package com.mismatched.nowyouretalking;

import android.annotation.TargetApi;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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

        //run background task
        AsyncTaskRunner  runner = new AsyncTaskRunner();
        runner.execute();

        //on input click
        EditText messageText = (EditText) findViewById(R.id.MessageText);
        messageText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //set view to bottom
                ScrollView ScrollWindow = (ScrollView) findViewById(R.id.ScrollWindow);
                ScrollWindow.fullScroll(View.FOCUS_DOWN);
            }

        });

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            public void onClick(View v) {

                ScrollView ScrollWindow = (ScrollView) findViewById(R.id.ScrollWindow);

                //get entered text
                EditText messageText = (EditText) findViewById(R.id.MessageText);
                String sendingText = messageText.getText().toString();

                //check if not empty
                if (!sendingText.isEmpty()){

                    //set date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = sdf.format(new Date());

                    //put details in a map
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("date", currentDate);
                    childUpdates.put("to", participant);
                    childUpdates.put("text", sendingText);
                    childUpdates.put("from", getUserProfile.uid);

                    //upload message
                    myRef.push().updateChildren(childUpdates);

                    //close keyboard and clear input
                    InputMethodManager imm = (InputMethodManager) getSystemService(Message.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(messageText.getWindowToken(), 0);
                    messageText.getText().clear();

                    //set view to bottom
                    ScrollWindow.fullScroll(View.FOCUS_DOWN);

                }
                else{
                    Toast.makeText(Message.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            //get conversation and participant selected
            Bundle extras = getIntent().getExtras();
            final String conversation = extras.getString("conversation");
            final String participantName = extras.getString("ParticipantName");

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
                            rowTextView.setPadding(40,0,40,0);
                            rowTextView.setTextColor(ContextCompat.getColor(Message.this, R.color.White));

                            //set layout if to or from user
                            if(from.contains(getUserProfile.uid)){
                                params.gravity = Gravity.END;
                                params.setMargins(350, 5, 150, 5);
                                rowTextView.setBackgroundColor(ContextCompat.getColor(Message.this, R.color.colorPrimary));
                                String sourceString = "<b>" + getUserProfile.name + "</b> "+ "<br/>" + text;
                                rowTextView.setText(Html.fromHtml(sourceString));
                                rowTextView.setGravity(Gravity.END);
                            }
                            else if(to.contains(getUserProfile.uid)){
                                params.setMargins(150, 5, 350, 5);
                                params.gravity = Gravity.START;
                                rowTextView.setBackgroundColor(ContextCompat.getColor(Message.this, R.color.colorAccent));
                                String sourceString = "<b>" + participantName + "</b> "+ "<br/>" + text;
                                rowTextView.setText(Html.fromHtml(sourceString));
                            }

                            // add to view
                            myLinearLayout.addView(rowTextView);

                            //set view to bottom
                            final ScrollView ScrollWindow = (ScrollView) findViewById(R.id.ScrollWindow);
                            ScrollWindow.post(new Runnable()
                            {
                                public void run()
                                {
                                    ScrollWindow.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }// end null check
                    } // end database loop
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }//end do in background

    }

    public void onBackPressed(){
        //go back to messages
        Intent intent = new Intent(Message.this, MessagingActivity.class);
        startActivity(intent);
    }

}
