package com.mismatched.nowyouretalking;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by jamie on 11/10/2016.
 */

public class UserProfileActivity extends AppCompatActivity {

    private TextView NameText;
    private TextView EmailText;

    //get user profile
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity.getUserProfile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        NameText = (TextView) findViewById(R.id.nameText);
        EmailText = (TextView) findViewById(R.id.emailText);

        String name  = getUserProfile.name;
        String email = getUserProfile.email;

        NameText.append(name);
        EmailText.append(email);


    }

    // public class to get user from any activity
    public class getUserProfile{
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String name  = user.getDisplayName();
        String email = user.getEmail();
        String uid = user.getUid();
    }


}
