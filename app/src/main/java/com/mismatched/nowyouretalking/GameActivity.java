package com.mismatched.nowyouretalking;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        //hide layouts
        hideAllViews();

    }

    private void checkAnswer(int questionNumber, String correctAnswer, final String answerGiven, final String[] questionInfo, final String[] falseInfo, final String userlanguage, final String lesson) {

        // hide keyboard if showing
        View keyboardView = this.getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }

        //set result view
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.resultLayout);
        resultView.setVisibility(View.VISIBLE);
        TextView resultHolder = (TextView) findViewById(R.id.resultHolder);
        TextView answerHolder = (TextView) findViewById(R.id.correctAnswerHolder);

        //set text in result
        answerHolder.setText(correctAnswer);

        if (answerGiven.equals(correctAnswer)) {

            //set result
            resultView.setBackgroundResource(R.drawable.rounded_textview_green);
            resultHolder.setTextColor(getResources().getColor(R.color.DarkGreen));
            answerHolder.setTextColor(getResources().getColor(R.color.DarkGreen));
            resultHolder.setText(getResources().getString(R.string.CorrectAnswer));


            //add a score
            score++;
            TextView scoreLbl = (TextView) findViewById(R.id.scoreLable);
            scoreLbl.setText(String.valueOf(score));
        } else {
            //set result
            resultView.setBackgroundResource(R.drawable.rounded_textview_red);
            resultHolder.setTextColor(getResources().getColor(R.color.Red));
            answerHolder.setTextColor(getResources().getColor(R.color.Red));
            resultHolder.setText(getResources().getString(R.string.WrongAnswer));
        }

        //update progress and question
        ProgressBar pb = (ProgressBar) findViewById(R.id.CompletionProgressBar);
        pb.setProgress(pb.getProgress() + 10);
        questionNumber = questionNumber + 1;

        final Button confirm = (Button) findViewById(R.id.checkAnswerBtn);
        confirm.setText(getResources().getString(R.string.Continue));
        final int finalQuestionNumber = questionNumber;
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalQuestionNumber == 10) {

                    //hide views
                    Button confirm = (Button) findViewById(R.id.checkAnswerBtn);
                    confirm.setVisibility(View.GONE);
                    TextView goalLbl = (TextView) findViewById(R.id.goalLable);
                    goalLbl.setVisibility(View.GONE);
                    ProgressBar pb = (ProgressBar) findViewById(R.id.CompletionProgressBar);
                    pb.setProgress(0);
                    TextView scoreLbl = (TextView) findViewById(R.id.scoreLable);
                    scoreLbl.setText(null);

                    hideAllViews();

                    //show end game view
                    RelativeLayout endGameView = (RelativeLayout) findViewById(R.id.endGameLayout);
                    endGameView.setVisibility(View.VISIBLE);

                    //set messages
                    TextView endScore = (TextView) findViewById(R.id.endScore);
                    endScore.setText(score + "/10");

                    TextView endMessage = (TextView) findViewById(R.id.endMessage);
                    if(score == 10){
                        endMessage.setText(getResources().getString(R.string.EndMessageGood));
                    }else if(score < 10 && score > 6){
                        endMessage.setText(getResources().getString(R.string.EndMessageMiddle));
                    }else if(score < 7){
                        endMessage.setText(getResources().getString(R.string.EndMessageBad));
                    }

                    Button leaveGameBtn = (Button) findViewById(R.id.leaveGameBtn);
                    leaveGameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    Button tryAgainBtn = (Button) findViewById(R.id.tryAgainBtn);
                    tryAgainBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View vg = findViewById (R.id.gameLayout);
                            vg.invalidate();
                            recreate();
                        }
                    });

                    //update score
                    // get user details
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference("Users/" + getUserProfile.uid);

                    //userRef.child("Level").setValue(score);

                    //check if score is higher and update
                    SharedPreferences Prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                    int thisLessonScore = Prefs.getInt(userlanguage + lesson, 0);

                    //int thisLessonScore = Prefs.getInt("German" + "Basics", 0);
                    if (thisLessonScore < score) {
                        SharedPreferences.Editor editor = Prefs.edit();
                        editor.putInt(userlanguage + lesson, score);
                        editor.apply();
                    }

                } else {
                    // load next question here
                    confirm.setText(getResources().getString(R.string.Check));
                    loadNextQuestion(finalQuestionNumber, questionInfo, falseInfo, userlanguage, lesson);
                }
            }
        });



    }

    private void loadNextQuestion(final int questionNumber, final String[] questionInfo, final String[] falseInfo, final String userlanguage, final String lesson) {

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

        //hidelayouts
        hideAllViews();

        switch (questionType) {
            case "ImageQuestion":
                //handle question here
                RelativeLayout imageQuestionView = (RelativeLayout) findViewById(R.id.imageQuestionLayout);
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

                ArrayList<View> radiobuttons = new ArrayList<View>();
                radiobuttons.add(b_one);
                radiobuttons.add(b_two);
                radiobuttons.add(b_three);
                radiobuttons.add(b_four);


                for(View current : radiobuttons){
                    //get the imageview for this button
                    RelativeLayout parent = (RelativeLayout) current.getParent();
                    ImageView myimage = (ImageView) parent.getChildAt(0);
                    TextView textFromButton = (TextView)current;

                    //pass text into image helper
                    ImageHelper imageHelperClass = new ImageHelper();
                    int selected = imageHelperClass.getImageForView(textFromButton.getText().toString());

                    //set image from imageHelper
                    myimage.setBackgroundResource(selected);

                }


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
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo, userlanguage, lesson);
                    }
                });
                break;
            case "ButtonQuestion":
                //handle question here
                RelativeLayout buttonQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
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
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo, userlanguage, lesson);
                    }
                });

                break;
            case "TranslateQuestion":
                //handle question here
                RelativeLayout translateQuestionView = (RelativeLayout) findViewById(R.id.translateQuestionLayout);
                translateQuestionView.setVisibility(View.VISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.TranslateGoal));

                //disable button
                confirm.setEnabled(false);

                //set question
                TextView transQuestionText = (TextView) findViewById(R.id.translatQuestionTextview);
                transQuestionText.setText(question);

                final EditText input = (EditText) findViewById(R.id.translateInputText);

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        confirm.setEnabled(true);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String givenAnswer = input.getText().toString();

                        //reset input
                        input.getText().clear();

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo, userlanguage, lesson);
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
        loadNextQuestion(0, questionInfo, wrongAnswers, language, lesson);
    }

    private void hideAllViews(){
        //layouts
        RelativeLayout imageQuestionView = (RelativeLayout) findViewById(R.id.imageQuestionLayout);
        RelativeLayout buttonQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
        RelativeLayout translateQuestionView = (RelativeLayout) findViewById(R.id.translateQuestionLayout);
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.resultLayout);
        RelativeLayout endGameView = (RelativeLayout) findViewById(R.id.endGameLayout);

        //hide views
        imageQuestionView.setVisibility(View.GONE);
        buttonQuestionView.setVisibility(View.GONE);
        translateQuestionView.setVisibility(View.GONE);
        resultView.setVisibility(View.GONE);
        endGameView.setVisibility(View.GONE);
    }
}
