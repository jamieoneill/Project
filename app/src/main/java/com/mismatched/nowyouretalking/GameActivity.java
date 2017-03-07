package com.mismatched.nowyouretalking;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jamie on 28/02/2017.
 */

public class GameActivity extends AppCompatActivity {

    int score = 0;

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //get lesson selected
        Bundle extras = getIntent().getExtras();
        final String lesson = extras.getString("Lesson");

        // get user details
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users/" +  getUserProfile.uid);

        //get language and level
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String userLanguage = dataSnapshot.child("Language").getValue(String.class);
                final String Level = dataSnapshot.child("Level").getValue(String.class);

                //get question list
                getQuestionList(lesson, userLanguage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set progress bar
        final ProgressBar pb = (ProgressBar) findViewById(R.id.CompletionProgressBar);
        pb.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.Green), PorterDuff.Mode.SRC_IN);

        //question layouts
        RelativeLayout imageQuestionView = (RelativeLayout) findViewById(R.id.imageQuestionLayout);
        RelativeLayout buttonQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
        RelativeLayout translateQuestionView = (RelativeLayout) findViewById(R.id.translateQuestionLayout);

        imageQuestionView.setVisibility(View.GONE);
        buttonQuestionView.setVisibility(View.GONE);
        translateQuestionView.setVisibility(View.GONE);

    }

    private void checkAnswer(int questionNumber, String correctAnswer, String answerGiven, String[] questionInfo, String[] falseInfo) {

        if(answerGiven.equals(correctAnswer)){
            Toast.makeText(GameActivity.this, "Correct = " + answerGiven, Toast.LENGTH_SHORT).show();

            score ++;
            TextView scoreLbl = (TextView) findViewById(R.id.scoreLable);
            scoreLbl.setText(String.valueOf(score));
        }
        else{
            Toast.makeText(GameActivity.this, "Wrong = " + answerGiven, Toast.LENGTH_SHORT).show();

        }
        //update progress and question
        ProgressBar pb = (ProgressBar) findViewById(R.id.CompletionProgressBar);
        pb.setProgress(pb.getProgress() + 10);
        questionNumber = questionNumber + 1;

        // load next question here
        loadNextQuestion(questionNumber, questionInfo, falseInfo);

    }

    private void loadNextQuestion(final int questionNumber, final String[] questionInfo, final String[] falseInfo) {

            //get question info
            String[] parts = questionInfo[questionNumber].split(",");

            String questionType = parts[0];
            String question = parts[1];
            final String answer = parts[2];

            //get false answers
            String[] parts2 = falseInfo[questionNumber].split(",");
            String false1 = parts2[0];
            String false2 = parts2[1];
            String false3 = parts2[2];

        ArrayList answersShuffled = new ArrayList();
        answersShuffled.add(answer);
        answersShuffled.add(false1);
        answersShuffled.add(false2);
        answersShuffled.add(false3);
        Collections.shuffle(answersShuffled);

        //set confirm button
        Button confirm = (Button) findViewById(R.id.checkAnswerBtn);

        //set goal
        TextView goalLbl = (TextView) findViewById(R.id.goalLable);
        goalLbl.setText(question);

        //question layouts
        RelativeLayout imageQuestionView = (RelativeLayout) findViewById(R.id.imageQuestionLayout);
        RelativeLayout buttonQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
        RelativeLayout translateQuestionView = (RelativeLayout) findViewById(R.id.translateQuestionLayout);

        imageQuestionView.setVisibility(View.GONE);
        buttonQuestionView.setVisibility(View.GONE);
        translateQuestionView.setVisibility(View.GONE);

        switch (questionType) {
            case "ImageQuestion":
                //handle question here
                imageQuestionView.setVisibility(View.VISIBLE);

                //get radio buttons
                final RadioButton b_one = (RadioButton) findViewById(R.id.radioButton1);
                final RadioButton b_two = (RadioButton) findViewById(R.id.radioButton2);
                final RadioButton b_three = (RadioButton) findViewById(R.id.radioButton3);
                final RadioButton b_four = (RadioButton) findViewById(R.id.radioButton4);

                b_one.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(true);
                        b_two.setChecked(false);
                        b_three.setChecked(false);
                        b_four.setChecked(false);
                    }
                });

                b_two.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(true);
                        b_three.setChecked(false);
                        b_four.setChecked(false);
                    }
                });

                b_three.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(false);
                        b_three.setChecked(true);
                        b_four.setChecked(false);
                    }
                });

                b_four.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(false);
                        b_three.setChecked(false);
                        b_four.setChecked(true);
                    }
                });


                b_one.setText(answersShuffled.get(0).toString());
                b_two.setText(answersShuffled.get(1).toString());
                b_three.setText(answersShuffled.get(2).toString());
                b_four.setText(answersShuffled.get(3).toString());

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String givenAnswer = "none";
                        if (b_one.isChecked()){
                            givenAnswer = String.valueOf(b_one.getText());
                        }else if(b_two.isChecked()){
                            givenAnswer = String.valueOf(b_two.getText());
                        }else if(b_three.isChecked()){
                            givenAnswer = String.valueOf(b_three.getText());
                        }else if(b_four.isChecked()){
                            givenAnswer = String.valueOf(b_four.getText());
                        }
                        //reset buttons
                        b_one.setChecked(false);
                        b_two.setChecked(false);
                        b_three.setChecked(false);
                        b_four.setChecked(false);

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo);
                    }
                });
                break;
            case "ButtonQuestion":
                //handle question here
                break;
            case "TranslateQuestion":
                //handle question here
                break;
        }
    }

    private void getQuestionList(String lesson, String language) {

        //set array used for game
        String languageLessionArray = language + "_" + lesson+ "_questions_array";
        //get id of array
        int resId=GameActivity.this.getResources().getIdentifier(languageLessionArray, "array", GameActivity.this.getPackageName());
        //pull back questions for this game
        String[] questionInfo = getBaseContext().getResources().getStringArray(resId);

        //set wrong questions for game
        String languageFalseArray = language + "_" + lesson+ "_false_array";
        //get id of array
        int resId2 =GameActivity.this.getResources().getIdentifier(languageFalseArray, "array", GameActivity.this.getPackageName());
        //pull back false answers for this game
        String[] wrongAnswers = getBaseContext().getResources().getStringArray(resId2);

        //load the first question which is 0 in array
        loadNextQuestion(0, questionInfo, wrongAnswers);

    }
}
