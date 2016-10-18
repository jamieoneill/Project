package com.mismatched.nowyouretalking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.host;

/**
 * Created by jamie on 18/10/2016.
 */

public class MeetingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Meetings");

    //set unique meeting id
    DatabaseReference meeting = myRef.push();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        // get user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String host = "name";
                String title = "test";
                String location = "location";
                String timedate = "11";
                int minLevel = 1;
                int maxLevel = 5;
                int NumGuests = 3;
                String attending = "test";
                String note = "test note 222";

                writeNewPost(host,title, location, timedate, minLevel, maxLevel, NumGuests, attending, note);


            }
            });

    }



    private void writeNewPost(String host, String title, String location, String timedate, int minLevel, int maxLevel, int NumGuests, String attending, String note) {

        //unique ID
        String key =  meeting.getKey();

        //take values
        Meet meet = new Meet(host,location, title, timedate, minLevel, maxLevel, NumGuests, attending, note);
        Map<String, Object> postValues = meet.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);

        //update DB
        myRef.updateChildren(childUpdates);
        myRef.child(key).setValue(postValues);

        Toast.makeText(MeetingActivity.this,myRef +"/"+ key + postValues.toString(),
                Toast.LENGTH_SHORT).show();
    }

    @IgnoreExtraProperties
    public class Meet {

        public String host;
        public String title;
        public String location;
        public String timedate;
        public int minLevel;
        public int maxLevel;
        public int NumGuests;
        public String attending;
        public String note;
       // public Map<String, Boolean> stars = new HashMap<>();

        public Meet(String host, String title, Date timedate, int minLevel, int maxLevel, int numGuests, String attending, String note) {
            // Default constructor required for calls to DataSnapshot.getValue(Meet.class)
        }

        public Meet(String host, String title, String location, String timedate, int minLevel, int maxLevel, int NumGuests, String attending, String note) {
            this.host = host;
            this.title = title;
            this.location = location;
            this.timedate = timedate;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.NumGuests = NumGuests;
            this.attending = attending;
            this.note = note;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("host", host);
            result.put("title", title);
            result.put("timedate", timedate);
            result.put("minLevel", minLevel);
            result.put("maxLevel", maxLevel);
            result.put("NumGuests", NumGuests);
            result.put("attending", attending);
            result.put("note", note);

            return result;
        }

    }

}
