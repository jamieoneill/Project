package com.mismatched.nowyouretalking;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by jamie on 11/10/2016.
 */

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private FirebaseAuth mAuth;

    private TextView NameText;
    private TextView EmailText;
    private ImageView MyImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        NameText = (TextView) findViewById(R.id.nameText);
        EmailText = (TextView) findViewById(R.id.emailText);
        MyImage = (ImageView) findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "signed_in:" + user);

        String name  = user.getDisplayName();
        String email = user.getEmail();
        Uri image = user.getPhotoUrl();

        Log.d(TAG, "Image:" + image);

        NameText.append(name);
        EmailText.append(email);
        MyImage.setImageURI(image);




    }
}
