package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jamie on 18/10/2016.
 */

public class MeetingActivity extends AppCompatActivity  {

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Meetings");

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    //set unique meeting id
    DatabaseReference meeting = myRef.push();
    DatabaseReference attend = myRef.child("Attending").push();

    //inputs
    private TextView TitleText;
    private String LocationText = "Search";
    private Spinner LanguageSpinner;
    private Spinner MinLevelSpinner;
    private Spinner MaxLevelSpinner;
    private Spinner GuestsSpinner;
    private TextView NoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        TitleText = (TextView) findViewById(R.id.TitleText);
        LanguageSpinner = (Spinner) findViewById(R.id.LanguageSpinner);
        MinLevelSpinner = (Spinner) findViewById(R.id.MinLevelSpinner);
        MaxLevelSpinner = (Spinner) findViewById(R.id.MaxLevelSpinner);
        GuestsSpinner = (Spinner) findViewById(R.id.GuestsSpinner);
        NoteText = (TextView) findViewById(R.id.NoteText);

        //location auto complete
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //set location from address picked
               LocationText = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Places error", "An error occurred: " + status);
            }
        });

        //get views for dialog
        final View dialogView = View.inflate(MeetingActivity.this, R.layout.datetimepicker_layout, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MeetingActivity.this).create();

        //set date view
        final Button dateButton = (Button) findViewById(R.id.DateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {

                // set dialog
                alertDialog.setView(dialogView);
                alertDialog.show();

                //set pickers
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                datePicker.setMinDate(System.currentTimeMillis() - 10000);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                //get time and date on confirm
                Button ok = (Button) dialogView.findViewById(R.id.datetimeButton);
                ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth()+1;
                        int year = datePicker.getYear();
                        int hour;
                        int minute;

                        // get method is different between android versions
                        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                             hour = timePicker.getHour();
                             minute = timePicker.getMinute();
                        } else {
                             hour = timePicker.getCurrentHour();
                             minute = timePicker.getCurrentMinute();
                        }

                        //format time and date
                        String time =  String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        String date = " " + day + "/" + month + "/" + year;
                        String timedate = time + date;

                        //add to button
                        dateButton.setText(timedate);

                        alertDialog.dismiss();
                    }

                });



            }
        });

        Button AddButton = (Button) findViewById(R.id.AddMeetButton);
        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //split time from date
                String timeanddate = dateButton.getText().toString();
                String[] parts = timeanddate.split(" ");
                String time = parts[0];
                String date = parts[1];

                //get values
                String Host = getUserProfile.name;
                String Title = TitleText.getText().toString();
                String Location = LocationText;
                String MeetingDate = date;
                String MeetingTime = time;
                String Language = String.valueOf(LanguageSpinner.getSelectedItem());
                int MinLevel = Integer.parseInt(String.valueOf(MinLevelSpinner.getSelectedItem()));
                int MaxLevel = Integer.parseInt(String.valueOf(MaxLevelSpinner.getSelectedItem()));
                int NumGuests = Integer.parseInt(String.valueOf(GuestsSpinner.getSelectedItem()));
                String Note = NoteText.getText().toString();

                //check for empty values
                if (Title.isEmpty() || Location.contains("Search") || MeetingDate.contains("Date")) {
                    Toast.makeText(MeetingActivity.this, R.string.fillFields, Toast.LENGTH_SHORT).show();
                } else {

                    //write to database
                    writeNewPost(Host, Title, Location, Language, MeetingDate, MeetingTime, MinLevel, MaxLevel, NumGuests, Note);
                }

            }
        });
    }


    private void writeNewPost(String Host, String Title, String Location, String Language, String MeetingDate, String MeetingTime, int MinLevel, int MaxLevel, int NumGuests, String Note) {

        //unique ID
        String key = meeting.getKey();

        //take values
        Meet meet = new Meet(Host, Title, Location, Language, MeetingDate, MeetingTime, MinLevel, MaxLevel, NumGuests, Note);
        Map<String, Object> postValues = meet.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);

        //update DB
        myRef.updateChildren(childUpdates);
        myRef.child(key).setValue(postValues);

        //add the creator to attending field
        myRef.child(key).child("Attending").child(attend.getKey()).setValue(getUserProfile.uid);

        //tell user it has been added
        Toast.makeText(MeetingActivity.this, Title + " "  + getResources().getString(R.string.MeetupCreated), Toast.LENGTH_SHORT).show();

        // return to main
        Intent intent = new Intent(MeetingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @IgnoreExtraProperties
    public class Meet {

        public String Host;
        public String Title;
        public String Location;
        public String Language;
        public String MeetingDate;
        public String MeetingTime;
        public int MinLevel;
        public int MaxLevel;
        public int NumGuests;
        public String Note;
        // public Map<String, Boolean> stars = new HashMap<>();

        public Meet(String Host, String Title, String Location, String Language, Date MeetingDate, String MeetingTime, int MinLevel, int MaxLevel, int numGuests, String Note) {
            // Default constructor required for calls to DataSnapshot.getValue(Meet.class)
        }

        public Meet(String Host, String Title, String Location, String Language, String MeetingDate, String MeetingTime, int MinLevel, int MaxLevel, int NumGuests, String Note) {
            this.Host = Host;
            this.Title = Title;
            this.Location = Location;
            this.Language = Language;
            this.MeetingDate = MeetingDate;
            this.MeetingTime = MeetingTime;
            this.MinLevel = MinLevel;
            this.MaxLevel = MaxLevel;
            this.NumGuests = NumGuests;
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
            result.put("MeetingTime", MeetingTime);
            result.put("MinLevel", MinLevel);
            result.put("MaxLevel", MaxLevel);
            result.put("NumGuests", NumGuests);
            result.put("Note", Note);

            return result;
        }
    }
}
