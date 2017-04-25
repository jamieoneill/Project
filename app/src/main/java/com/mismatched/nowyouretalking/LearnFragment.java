package com.mismatched.nowyouretalking;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        //set title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.Learn));

        //get language
        SharedPreferences Prefs = getActivity().getSharedPreferences("Prefs", MODE_PRIVATE);
        userLanguage = Prefs.getString("currentLanguage", null);

        //check for level in this language
        LevelPrefs = getActivity().getSharedPreferences("levels", Context.MODE_PRIVATE);
        userLevel = LevelPrefs.getInt(userLanguage + "Level", 0);

        //set buttons
        ImageButton basicsBtn = (ImageButton) v.findViewById(R.id.basicsBtn);
        basicsBtn.setOnClickListener(LearnFragment.this);
        ImageButton phrasesBtn = (ImageButton) v.findViewById(R.id.phrasesBtn);
        phrasesBtn.setOnClickListener(LearnFragment.this);
        ImageButton basics2Btn = (ImageButton) v.findViewById(R.id.basics2Btn);
        basics2Btn.setOnClickListener(LearnFragment.this);
        ImageButton foodBtn = (ImageButton) v.findViewById(R.id.foodBtn);
        foodBtn.setOnClickListener(LearnFragment.this);
        ImageButton locationsBtn = (ImageButton) v.findViewById(R.id.locationsBtn);
        locationsBtn.setOnClickListener(LearnFragment.this);
        ImageButton adjectivesBtn = (ImageButton) v.findViewById(R.id.adjectivesBtn);
        adjectivesBtn.setOnClickListener(LearnFragment.this);

        //open buttons based on level
        if (userLevel > 0) {
            TextView phrases = (TextView) v.findViewById(R.id.phrasesText);
            phrases.setText(getResources().getString(R.string.Phrases));
        }
        if (userLevel > 1) {
            basics2Btn.setOnClickListener(LearnFragment.this);
            TextView basics2 = (TextView) v.findViewById(R.id.basics2Text);
            basics2.setText(getResources().getString(R.string.Basics2));
        }
        if (userLevel > 2) {
            TextView basics2 = (TextView) v.findViewById(R.id.animalsText);
            basics2.setText(getResources().getString(R.string.Animals));
        }
        if (userLevel > 3) {
            TextView food = (TextView) v.findViewById(R.id.foodText);
            food.setText(getResources().getString(R.string.Food));
        }
        if (userLevel > 4) {
            TextView locations = (TextView) v.findViewById(R.id.locationsText);
            locations.setText(getResources().getString(R.string.Locations));
        }
        if (userLevel > 5) {
            TextView adjectives = (TextView) v.findViewById(R.id.adjectivesText);
            adjectives.setText(getResources().getString(R.string.Adjectives));
        }

        //set progress bars
        setProgressBars(v, userLanguage);

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

        // items for cardview
        CardPagerAdapter mCardAdapter;
        ViewPager mViewPager;
        ShadowTransformer mCardShadowTransformer;
        mViewPager = (ViewPager) dialogView.findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        int lessonCount = 0;
        String lessonName = myLesson.getText().toString();

        //dont open if locked
        if (lessonName.equals(getResources().getString(R.string.locked))) {
            Toast.makeText(getActivity(), getResources().getString(R.string.needToComplete), Toast.LENGTH_SHORT).show();
        } else {
            //set lesson number
            switch (v.getId()) {
                case R.id.basicsBtn:
                    lessonCount = 3;
                    break;
                case R.id.phrasesBtn:
                    lessonCount = 2;
                    break;
            }

            //add a card for the same number of the lesson count
            for (int i = 0; i < lessonCount; i++) {
                mCardAdapter.addCardItem(addMyCard(mCardAdapter, lessonName, lessonCount, userLanguage));
            }

            //set card view
            mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
            mCardShadowTransformer.enableScaling(true);
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setOffscreenPageLimit(3);
            alertDialog.show();
        }

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

            //// TODO: 21/04/2017 needs to change the dividing number based on lesson number 
            //sum of lessons displays
            int result = (LessonScore1 + LessonScore2 + LessonScore3) / 3;
            myProgressBar.setProgress(result * 10);

            //change progress bar
            if (myProgressBar.getProgress() == 100) {
                //set progress bar
                myProgressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.Gold), PorterDuff.Mode.SRC_IN);

                //get lesson
                boolean LessonComplete = LevelPrefs.getBoolean(myLesson.getText().toString() + "Complete", false);
                Log.d(myLesson.getText().toString() + "Complete ", String.valueOf(LessonComplete));

                if (!LessonComplete) {
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