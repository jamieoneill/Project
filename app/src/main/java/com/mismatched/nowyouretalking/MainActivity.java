package com.mismatched.nowyouretalking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

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
            Log.d("testing", "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            //go to sign in
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }


        Button ProfileActivityButton = (Button) findViewById(R.id.ProfileActivityButton);
        ProfileActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);

                startActivity(intent);
            }
        });

        Button AddMeetActivityButton = (Button) findViewById(R.id.AddMeetActivityButton);
        AddMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeetingActivity.class);

                startActivity(intent);
            }
        });

        Button FindMeetActivityButton = (Button) findViewById(R.id.FindMeetActivityButton);
        FindMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                startActivity(intent);
            }
        });

        Button ManageMeetActivityButton = (Button) findViewById(R.id.ManageMeetActivityButton);
        ManageMeetActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageActivity.class);

                startActivity(intent);
            }
        });

    }

}