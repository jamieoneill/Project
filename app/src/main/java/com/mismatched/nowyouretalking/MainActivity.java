package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTesting";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get user
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            //go to sign in
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //get user id
                String uid = user.getUid();
                String host = user.getDisplayName();

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Meetings");

                //set unique meeting id
                DatabaseReference meeting = myRef.push();


                meeting.child("Host").setValue(host);
                meeting.child("Title").setValue("test");
                meeting.child("Location").setValue("google map location");
                meeting.child("Date").setValue("13:00 18/10/2016");
                meeting.child("Language").setValue("German");
                meeting.child("MinLevel").setValue("1");
                meeting.child("MaxLevel").setValue("5");
                meeting.child("NumGuests").setValue("3");
                meeting.child("Attending").setValue(uid);
                meeting.child("Note").setValue("This is a test");

                Toast.makeText(MainActivity.this, "unique test meeting created in DB",
                        Toast.LENGTH_SHORT).show();


                /*
                host
                Title
                location
                date
                Language
                min level
                max level
                Guests
                note

                 */


                //myRef.setValue("DB Entry test");
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("Meetings");


                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //retrieves all in meeting database
                        Map<String,Object> dbRead=(Map<String,Object>)dataSnapshot.getValue();
                        //String result = dbRead.values().toString();
                        //Set val = dbRead.keySet();

                        List<String> myList = new ArrayList<String>();

                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            Log.i("each entry", child.getValue().toString());
                            myList.add(child.child("Note").getValue(String.class));
                        }
                        Log.i("all notes", myList.toString());

                        /*
                        Log.d(TAG, "Value is: " + result);
                        Log.d(TAG, "children: " + dataSnapshot.getChildren().iterator().toString());
                        Log.d(TAG, "key is: " + val);
                        Log.d(TAG, "key is: " + dbRead.get("-KUOJLRjalFx29GoMZuU"));

                        Map<String,Object>  test = (Map<String,Object>)dbRead.get("-KUOJLRjalFx29GoMZuU");

                        Log.d(TAG, "key is: " + test.get("Note"));

                        */

                        Toast.makeText(MainActivity.this, "reading in. see log for details",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);

                startActivity(intent);
            }
        });

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeetingActivity.class);

                startActivity(intent);
            }
        });

        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });

    }

}