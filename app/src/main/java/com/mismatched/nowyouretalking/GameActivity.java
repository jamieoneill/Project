package com.mismatched.nowyouretalking;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        final DatabaseReference userRef = database.getReference("Users/" + getUserProfile.uid);

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

        if (answerGiven.equals(correctAnswer)) {
            Toast.makeText(GameActivity.this, "Correct = " + answerGiven, Toast.LENGTH_SHORT).show();

            score++;
            TextView scoreLbl = (TextView) findViewById(R.id.scoreLable);
            scoreLbl.setText(String.valueOf(score));
        } else {
            Toast.makeText(GameActivity.this, "Wrong = " + answerGiven, Toast.LENGTH_SHORT).show();
        }

        //update progress and question
        ProgressBar pb = (ProgressBar) findViewById(R.id.CompletionProgressBar);
        pb.setProgress(pb.getProgress() + 10);
        questionNumber = questionNumber + 1;

        if (questionNumber == 10) {
            Toast.makeText(GameActivity.this, "finished " + answerGiven, Toast.LENGTH_SHORT).show();
        } else {
            // load next question here
            loadNextQuestion(questionNumber, questionInfo, falseInfo);
        }

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

        //shuffle false answers array
        ArrayList allButtons = new ArrayList();
        Collections.addAll(allButtons, parts2);
        Collections.shuffle(allButtons);

        //set confirm button
        final Button confirm = (Button) findViewById(R.id.checkAnswerBtn);
        //set goal label
        TextView goalLbl = (TextView) findViewById(R.id.goalLable);

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

                //set goal
                goalLbl.setText(question);

                //disable button
                confirm.setEnabled(false);

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
                        confirm.setEnabled(true);

                    }
                });

                b_two.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(true);
                        b_three.setChecked(false);
                        b_four.setChecked(false);
                        confirm.setEnabled(true);
                    }
                });

                b_three.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(false);
                        b_three.setChecked(true);
                        b_four.setChecked(false);
                        confirm.setEnabled(true);
                    }
                });

                b_four.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        b_one.setChecked(false);
                        b_two.setChecked(false);
                        b_three.setChecked(false);
                        b_four.setChecked(true);
                        confirm.setEnabled(true);
                    }
                });

                ArrayList answersShuffled = new ArrayList();
                answersShuffled.add(answer);
                answersShuffled.add(false1);
                answersShuffled.add(false2);
                answersShuffled.add(false3);
                Collections.shuffle(answersShuffled);

                b_one.setText(answersShuffled.get(0).toString());
                b_two.setText(answersShuffled.get(1).toString());
                b_three.setText(answersShuffled.get(2).toString());
                b_four.setText(answersShuffled.get(3).toString());

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String givenAnswer = "none";
                        if (b_one.isChecked()) {
                            givenAnswer = String.valueOf(b_one.getText());
                        } else if (b_two.isChecked()) {
                            givenAnswer = String.valueOf(b_two.getText());
                        } else if (b_three.isChecked()) {
                            givenAnswer = String.valueOf(b_three.getText());
                        } else if (b_four.isChecked()) {
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
                buttonQuestionView.setVisibility(View.VISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.TranslateGoal));

                //set question
                TextView questionText = (TextView) findViewById(R.id.buttonQuestionTextview);
                questionText.setText(question);
                final TextView textLine = (TextView) findViewById(R.id.questionInput);

                //disable button
                confirm.setEnabled(false);

                //button layout
                final LinearLayout buttonLayout1 = (LinearLayout) findViewById(R.id.buttonLine1);
                final LinearLayout buttonLayout2 = (LinearLayout) findViewById(R.id.buttonLine2);
                final LinearLayout buttonLayout3 = (LinearLayout) findViewById(R.id.buttonLine3);


                for (int i = 0; i < allButtons.size(); i++) {

                    String buttonText = String.valueOf(allButtons.get(i));
                    final Button addingButton = new Button(GameActivity.this);
                    addingButton.setText(buttonText);
                    addingButton.setTransformationMethod(null);
                    addingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //enable button
                            confirm.setEnabled(true);

                            //button selected and exsisting text
                            String addedText = String.valueOf(addingButton.getText());
                            String exsistingText = textLine.getText().toString();

                            //seperate exsiting text into words
                            String[] exsistingArray = exsistingText.split(" ");

                            //// TODO: 08/03/2017 fix first word not being rewritten
                            //check how to handle word
                            for (int i = 0; i < exsistingArray.length; i++) {

                                String currentword = exsistingArray[i];

                                if (currentword.equals(addedText)) {
                                    //remove the word
                                    String myString = exsistingText.replaceAll("\\b\\s" + addedText + "\\b", "");
                                    textLine.setText(myString);
                                    break;
                                } else if (i < exsistingArray.length - 1 && !currentword.equals(addedText)) {
                                    //do nothing in word that is not equal
                                    Log.d("nothing ", " ");
                                } else {
                                    //add word to line
                                    textLine.append(" " + addedText);
                                }
                            }//end for

                        }
                    });

                    //add the buttons to the layout
                    if (buttonLayout1.getChildCount() <= 2) {
                        buttonLayout1.addView(addingButton);
                    } else if (buttonLayout1.getChildCount() > 2 && buttonLayout2.getChildCount() <= 2) {
                        buttonLayout2.addView(addingButton);
                    } else {
                        buttonLayout3.addView(addingButton);
                    }
                }//end for loop

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String givenAnswer = String.valueOf(textLine.getText());

                        //reset buttons
                        buttonLayout1.removeAllViews();
                        buttonLayout2.removeAllViews();
                        buttonLayout3.removeAllViews();
                        textLine.setText(null);

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo);
                    }
                });

                break;
            case "TranslateQuestion":
                //handle question here
                translateQuestionView.setVisibility(View.VISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.TranslateGoal));

                //disable button
                confirm.setEnabled(false);

                //set question
                TextView transQuestionText = (TextView) findViewById(R.id.translatQuestionTextview);
                transQuestionText.setText(question);

                final EditText input = (EditText) findViewById(R.id.translateInputText);

                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //enable button
                        confirm.setEnabled(true);
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String givenAnswer = input.getText().toString();

                        //reset input
                        input.getText().clear();

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo);
                    }
                });
                break;
        }
    }

    private void getQuestionList(String lesson, String language) {

        //set array used for game
        String languageLessionArray = language + "_" + lesson + "_questions_array";
        //get id of array
        int resId = GameActivity.this.getResources().getIdentifier(languageLessionArray, "array", GameActivity.this.getPackageName());
        //pull back questions for this game
        String[] questionInfo = getBaseContext().getResources().getStringArray(resId);

        //set wrong questions for game
        String languageFalseArray = language + "_" + lesson + "_false_array";
        //get id of array
        int resId2 = GameActivity.this.getResources().getIdentifier(languageFalseArray, "array", GameActivity.this.getPackageName());
        //pull back false answers for this game
        String[] wrongAnswers = getBaseContext().getResources().getStringArray(resId2);

        //load the first question which is 0 in array
        loadNextQuestion(0, questionInfo, wrongAnswers);
    }
}
