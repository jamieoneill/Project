package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 26/11/2016.
 */
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LearnFragment extends Fragment implements View.OnClickListener {

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        final View v = inflater.inflate(R.layout.levelselect_fragment, null);

        // get user details
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users/" + getUserProfile.uid);

        //get language and level
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userLanguage = dataSnapshot.child("Language").getValue(String.class);
                final String Level = dataSnapshot.child("Level").getValue(String.class);

                setProgressBars(v, userLanguage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set buttons
        ImageButton basicsBtn = (ImageButton) v.findViewById(R.id.basicsBtn);
        basicsBtn.setOnClickListener(this);
        ImageButton phrasesBtn = (ImageButton) v.findViewById(R.id.phrasesBtn);
        basicsBtn.setOnClickListener(this);
        ImageButton basics2Btn = (ImageButton) v.findViewById(R.id.basics2Btn);
        basicsBtn.setOnClickListener(this);
        ImageButton foodBtn = (ImageButton) v.findViewById(R.id.foodBtn);
        basicsBtn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        RelativeLayout parent = (RelativeLayout) v.getParent();
        TextView myLesson = (TextView) parent.getChildAt(1);

        switch (v.getId()) {
            case R.id.basicsBtn:
                //start game
                intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("Lesson",myLesson.getText().toString());
                startActivity(intent);

                break;
            case R.id.phrasesBtn:
                //do something
                break;

        }
    }

    public void setProgressBars( View v, String userLanguage){

        //set buttons
        ImageButton basicsBtn = (ImageButton) v.findViewById(R.id.basicsBtn);
        ImageButton phrasesBtn = (ImageButton) v.findViewById(R.id.phrasesBtn);
        ImageButton basics2Btn = (ImageButton) v.findViewById(R.id.basics2Btn);
        ImageButton foodBtn = (ImageButton) v.findViewById(R.id.foodBtn);

        ArrayList<View> imagebuttons = new ArrayList<View>();
        imagebuttons.add(basicsBtn);
        imagebuttons.add(phrasesBtn);
        imagebuttons.add(basics2Btn);
        imagebuttons.add(foodBtn);

        for(View current : imagebuttons){
            //get the progressbar for this layout
            RelativeLayout parent = (RelativeLayout) current.getParent();
            ProgressBar myProgressBar = (ProgressBar) parent.getChildAt(2);
            TextView myLesson = (TextView) parent.getChildAt(1);

            //get score from prefs
            SharedPreferences Prefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            int thisLessonScore = Prefs.getInt(userLanguage + myLesson.getText().toString(), 0);
            myProgressBar.setProgress(thisLessonScore * 10);

            //add a start to 100% lessons
            if (myProgressBar.getProgress() == 100){
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, myLesson.getId());
                lp.addRule(RelativeLayout.RIGHT_OF, myProgressBar.getId());

                ImageView myimage = new ImageView(getActivity());
                myimage.setBackgroundResource(R.drawable.star);

                parent.addView(myimage, lp);
            }

        }

    }
}