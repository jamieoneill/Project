package com.mismatched.nowyouretalking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
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

    //inputs
    private TextView TitleText;
    private TextView LocationText;
    private TextView DateText;
    private Spinner LanguageSpinner;
    private Spinner MinLevelSpinner;
    private Spinner MaxLevelSpinner;
    private NumberPicker GuestsNP;
    private TextView NoteText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        // get user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        TitleText = (TextView) findViewById(R.id.TitleText);
        LocationText = (TextView) findViewById(R.id.LocationText);
        DateText = (TextView) findViewById(R.id.DateText);
        LanguageSpinner = (Spinner) findViewById(R.id.LanguageSpinner);
        MinLevelSpinner = (Spinner) findViewById(R.id.MinLevelSpinner);
        MaxLevelSpinner = (Spinner) findViewById(R.id.MaxLevelSpinner);
        GuestsNP = (NumberPicker) findViewById(R.id.GuestsNP);
        GuestsNP.setMinValue(0);
        GuestsNP.setMaxValue(10);
        GuestsNP.setWrapSelectorWheel(false);
        NoteText = (TextView) findViewById(R.id.NoteText);


        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String Host = user.getDisplayName();
                String Title = TitleText.getText().toString();
                String Location = LocationText.getText().toString();
                String MeetingDate = DateText.getText().toString();
                String Language = String.valueOf(LanguageSpinner.getSelectedItem());
                int MinLevel =  Integer.parseInt(String.valueOf(MinLevelSpinner.getSelectedItem()));
                int MaxLevel = Integer.parseInt(String.valueOf(MaxLevelSpinner.getSelectedItem()));
                int NumGuests = GuestsNP.getValue();
                String Attending = "test";
                String Note = NoteText.getText().toString();

                writeNewPost(Host, Title, Location, Language, MeetingDate, MinLevel, MaxLevel, NumGuests, Attending, Note);


            }
            });

    }



    private void writeNewPost(String Host, String Title, String Location, String Language, String MeetingDate, int MinLevel, int MaxLevel, int NumGuests, String Attending, String Note) {

        //unique ID
        String key =  meeting.getKey();

        //take values
        Meet meet = new Meet(Host, Title, Location, Language, MeetingDate, MinLevel, MaxLevel, NumGuests, Attending, Note);
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

        public String Host;
        public String Title;
        public String Location;
        public String Language;
        public String MeetingDate;
        public int MinLevel;
        public int MaxLevel;
        public int NumGuests;
        public String Attending;
        public String Note;
       // public Map<String, Boolean> stars = new HashMap<>();

        public Meet(String Host, String Title, String Location, String Language, Date MeetingDate, int MinLevel, int MaxLevel, int numGuests, String Attending, String Note) {
            // Default constructor required for calls to DataSnapshot.getValue(Meet.class)
        }

        public Meet(String Host, String Title, String Location,String Language, String MeetingDate, int MinLevel, int MaxLevel, int NumGuests, String Attending, String Note) {
            this.Host = Host;
            this.Title = Title;
            this.Location = Location;
            this.Language = Language;
            this.MeetingDate = MeetingDate;
            this.MinLevel = MinLevel;
            this.MaxLevel = MaxLevel;
            this.NumGuests = NumGuests;
            this.Attending = Attending;
            this.Note = Note;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("Host", Host);
            result.put("Title", Title);
            result.put("Location", Location);
            result.put("Language", Language);
            result.put("MeetingDate", MeetingDate);
            result.put("MinLevel", MinLevel);
            result.put("MaxLevel", MaxLevel);
            result.put("NumGuests", NumGuests);
            result.put("Attending", Attending);
            result.put("Note", Note);

            return result;
        }

    }

}
