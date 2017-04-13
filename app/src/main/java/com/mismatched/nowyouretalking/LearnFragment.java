package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LearnFragment extends Fragment implements View.OnClickListener {

    private AlertDialog alertDialog;
    private String userLanguage;
    private int userLevel;
    SharedPreferences LevelPrefs;

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

                userLanguage = dataSnapshot.child("Language").getValue(String.class);

                //check for level in this language
                LevelPrefs = getActivity().getSharedPreferences("levels", Context.MODE_PRIVATE);
                userLevel = LevelPrefs.getInt(userLanguage + "Level", 0);

                //set buttons
                ImageButton basicsBtn = (ImageButton) v.findViewById(R.id.basicsBtn);
                basicsBtn.setOnClickListener(LearnFragment.this);
                ImageButton phrasesBtn = (ImageButton) v.findViewById(R.id.phrasesBtn);
                ImageButton basics2Btn = (ImageButton) v.findViewById(R.id.basics2Btn);
                ImageButton foodBtn = (ImageButton) v.findViewById(R.id.foodBtn);
                ImageButton locationsBtn = (ImageButton) v.findViewById(R.id.locationsBtn);
                ImageButton adjectivesBtn = (ImageButton) v.findViewById(R.id.adjectivesBtn);

                //open buttons based on level
                if(userLevel > 0){
                    phrasesBtn.setOnClickListener(LearnFragment.this);
                    TextView phrases = (TextView) v.findViewById(R.id.phrasesText);
                    phrases.setText(getResources().getString(R.string.Phrases));
                }
                if(userLevel > 1){
                    basics2Btn.setOnClickListener(LearnFragment.this);
                    TextView basics2 = (TextView) v.findViewById(R.id.basics2Text);
                    basics2.setText(getResources().getString(R.string.Basics2));
                }
                if(userLevel > 2){
                    basics2Btn.setOnClickListener(LearnFragment.this);
                    TextView basics2 = (TextView) v.findViewById(R.id.animalsText);
                    basics2.setText(getResources().getString(R.string.Animals));
                }
                if(userLevel > 3){
                    foodBtn.setOnClickListener(LearnFragment.this);
                    TextView food = (TextView) v.findViewById(R.id.foodText);
                    food.setText(getResources().getString(R.string.Food));
                }
                if(userLevel > 4){
                    locationsBtn.setOnClickListener(LearnFragment.this);
                    TextView locations = (TextView) v.findViewById(R.id.locationsText);
                    locations.setText(getResources().getString(R.string.Locations));
                }
                if(userLevel > 5){
                    adjectivesBtn.setOnClickListener(LearnFragment.this);
                    TextView adjectives = (TextView) v.findViewById(R.id.adjectivesText);
                    adjectives.setText(getResources().getString(R.string.Adjectives));
                }

                //set progress bars
                setProgressBars(v, userLanguage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        //get lesson
        RelativeLayout parent = (RelativeLayout) v.getParent();
        TextView myLesson = (TextView) parent.getChildAt(1);

        //get views for dialog
        View dialogView = View.inflate(getActivity(), R.layout.lesson_selector, null);
        alertDialog = new AlertDialog.Builder(getActivity()).create();

        // set dialog
        alertDialog.setView(dialogView);
        CardPagerAdapter mCardAdapter;
        ViewPager mViewPager;

        mViewPager = (ViewPager) dialogView.findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        int lessonCount;
        String lessonName = myLesson.getText().toString();

        switch (v.getId()) {
            case R.id.basicsBtn:

                //add a card for the same number of the lesson count
                lessonCount = 3;
                mCardAdapter.addCardItem(addMyCard(mCardAdapter, lessonName, lessonCount, userLanguage));
                mCardAdapter.addCardItem(addMyCard(mCardAdapter, lessonName, lessonCount, userLanguage));
                mCardAdapter.addCardItem(addMyCard(mCardAdapter, lessonName, lessonCount, userLanguage));
                break;
            case R.id.phrasesBtn:
                //do something
                break;
        }

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setOffscreenPageLimit(3);
        alertDialog.show();

    }

    public void setProgressBars(View v, String userLanguage) {

        //set buttons
        ImageButton basicsBtn = (ImageButton) v.findViewById(R.id.basicsBtn);
        ImageButton phrasesBtn = (ImageButton) v.findViewById(R.id.phrasesBtn);
        ImageButton basics2Btn = (ImageButton) v.findViewById(R.id.basics2Btn);
        ImageButton foodBtn = (ImageButton) v.findViewById(R.id.foodBtn);

        ArrayList<View> imagebuttons = new ArrayList<>();
        imagebuttons.add(basicsBtn);
        imagebuttons.add(phrasesBtn);
        imagebuttons.add(basics2Btn);
        imagebuttons.add(foodBtn);

        for (View current : imagebuttons) {
            //get the progressbar for this layout
            RelativeLayout parent = (RelativeLayout) current.getParent();
            ProgressBar myProgressBar = (ProgressBar) parent.getChildAt(2);
            TextView myLesson = (TextView) parent.getChildAt(1);

            //get score from prefs
            SharedPreferences Prefs = getActivity().getSharedPreferences("Prefs", MODE_PRIVATE);
            int LessonScore1 = Prefs.getInt(userLanguage + myLesson.getText().toString() + "1", 0);
            int LessonScore2 = Prefs.getInt(userLanguage + myLesson.getText().toString() + "2", 0);
            int LessonScore3 = Prefs.getInt(userLanguage + myLesson.getText().toString() + "3", 0);

            //sum of lessons displays
            int result = (LessonScore1 + LessonScore2 + LessonScore3) / 3;
            myProgressBar.setProgress(result * 10);

            //add a star to 100% lessons
            if (myProgressBar.getProgress() == 100) {
                //add layout
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, myLesson.getId());
                lp.addRule(RelativeLayout.RIGHT_OF, myProgressBar.getId());
                lp.setMargins(5,0,0,0);

                //add image and progress bar
                ImageView myimage = new ImageView(getActivity());
                myimage.setBackgroundResource(R.drawable.star);
                myProgressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.Gold), PorterDuff.Mode.SRC_IN);
                parent.addView(myimage, lp);

                //get lesson
                boolean LessonComplete = LevelPrefs.getBoolean(myLesson.getText().toString() + "Complete", false);
                Log.d(myLesson.getText().toString() + "Complete ", String.valueOf(LessonComplete));

                if(!LessonComplete){
                    //set lesson complete
                    SharedPreferences.Editor editor = LevelPrefs.edit();
                    editor.putBoolean(myLesson.getText().toString() + "Complete", true);

                    //update level
                    int newLevel = userLevel + 1;
                    editor.putInt(userLanguage + "Level", newLevel);
                    editor.apply();

                    //display to user
                    Toast toast = new Toast(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View customToastroot;
                    TextView messageText;
                    customToastroot = inflater.inflate(R.layout.toast_levelup, null);
                    messageText = (TextView) customToastroot.findViewById(R.id.toastMessage);
                    messageText.setText(String.valueOf(newLevel));

                    //show result
                    toast.setView(customToastroot);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        }
    }

    public static int getStringIdentifier(Context context, String language, String lesson, int number) {
        return context.getResources().getIdentifier(language + "_" + lesson + number, "string", context.getPackageName());
    }

    public CardItem addMyCard(CardPagerAdapter mCardAdapter, String lessonName, int lessonCount, String userLanguage) {
        //this will add a card with the correct information and link to game
        return new CardItem(getResources().getString(R.string.lesson) + " " + String.valueOf(mCardAdapter.getCount() + 1 + " of " + lessonCount), getStringIdentifier(getActivity(), userLanguage, lessonName, mCardAdapter.getCount() + 1), getActivity(), GameActivity.class, lessonName + String.valueOf(mCardAdapter.getCount() + 1), userLanguage);
    }

    @Override
    public void onResume() {
        super.onResume();
        // hide dialog
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

    }
}