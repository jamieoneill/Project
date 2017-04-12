package com.mismatched.nowyouretalking;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

        //hide layouts
        hideAllViews();

    }

    private void checkAnswer(int questionNumber, String correctAnswer, final String answerGiven, final String[] questionInfo, final String[] falseInfo, final String userlanguage, final String lesson) {

        // hide keyboard if showing
        View keyboardView = this.getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    if (score == 10) {
                        endMessage.setText(getResources().getString(R.string.EndMessageGood));
                    } else if (score < 10 && score > 6) {
                        endMessage.setText(getResources().getString(R.string.EndMessageMiddle));
                    } else if (score < 7) {
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
                            View vg = findViewById(R.id.gameLayout);
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
        final ArrayList allButtons = new ArrayList();
        Collections.addAll(allButtons, parts2);
        Collections.shuffle(allButtons);

        //set confirm button
        final Button confirm = (Button) findViewById(R.id.checkAnswerBtn);
        //set goal label
        TextView goalLbl = (TextView) findViewById(R.id.goalLable);

        //get button background
        Button newButton = new Button(GameActivity.this);
        final Drawable buttonBackground = newButton.getBackground();

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


                for (View current : radiobuttons) {
                    //get the imageview for this button
                    RelativeLayout parent = (RelativeLayout) current.getParent();
                    ImageView myimage = (ImageView) parent.getChildAt(0);
                    TextView textFromButton = (TextView) current;

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
                questionText.setVisibility(View.VISIBLE);
                final TextView textLine = (TextView) findViewById(R.id.questionInput);
                TextView questionLine = (TextView) findViewById(R.id.questionLine1);
                questionLine.setVisibility(View.VISIBLE);

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
                                    //change button color
                                    addingButton.setBackground(buttonBackground);
                                    addingButton.setTextColor(Color.BLACK);


                                    break;
                                } else if (i < exsistingArray.length - 1 && !currentword.equals(addedText)) {
                                    //do nothing in word that is not equal
                                    Log.d("nothing ", " ");
                                } else {
                                    //add word to line
                                    textLine.append(" " + addedText);
                                    //change button color
                                    addingButton.setTextColor(Color.WHITE);
                                    addingButton.setBackgroundResource(R.drawable.circle_button);


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
            case "MatchingQuestion":
                //handle question here
                RelativeLayout MatchingQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
                MatchingQuestionView.setVisibility(View.VISIBLE);
                TextView questionLine2 = (TextView) findViewById(R.id.questionLine1);
                questionLine2.setVisibility(View.INVISIBLE);
                TextView questionText2 = (TextView) findViewById(R.id.buttonQuestionTextview);
                questionText2.setVisibility(View.INVISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.MatchTheWords));

                //disable button
                confirm.setEnabled(false);

                //button layout
                final LinearLayout buttonLayout11 = (LinearLayout) findViewById(R.id.buttonLine1);
                final LinearLayout buttonLayout22 = (LinearLayout) findViewById(R.id.buttonLine2);
                final LinearLayout buttonLayout33 = (LinearLayout) findViewById(R.id.buttonLine3);

                //set vars for game
                final int[] count = {0};
                final int[] selected = new int[1];
                final int[] previous = new int[1];
                final Button[] previousButton = new Button[1];
                final int[] correctCount = {0};

                for (int i = 0; i < allButtons.size(); i++) {

                    String buttonText = String.valueOf(allButtons.get(i));
                    final Button addingButton = new Button(GameActivity.this);

                    //remove number from text
                    String word = buttonText.substring(1);
                    final int number = Integer.parseInt(buttonText.substring(0, 1));

                    //set button
                    addingButton.setText(word);
                    addingButton.setTransformationMethod(null);
                    addingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //set up for toast when correct
                            Toast toast = new Toast(getApplicationContext());
                            LayoutInflater inflater = getLayoutInflater();
                            View customToastroot;
                            TextView messageText;
                            customToastroot = inflater.inflate(R.layout.toast_green, null);
                            messageText = (TextView) customToastroot.findViewById(R.id.toastMessage);
                            messageText.setText(getResources().getString(R.string.correct));

                            if (count[0] == 1) {
                                //this is the second selected button
                                selected[0] = number;

                                //set correct matches
                                if ((previous[0] == 1 || previous[0] == 2) && (selected[0] == 1 || selected[0] == 2)) {
                                    addingButton.setEnabled(false);
                                    correctCount[0] = correctCount[0] + 1;
                                } else if ((previous[0] == 3 || previous[0] == 4) && (selected[0] == 3 || selected[0] == 4)) {
                                    addingButton.setEnabled(false);
                                    correctCount[0] = correctCount[0] + 1;
                                } else if ((previous[0] == 5 || previous[0] == 6) && (selected[0] == 5 || selected[0] == 6)) {
                                    addingButton.setEnabled(false);
                                    correctCount[0] = correctCount[0] + 1;
                                } else if ((previous[0] == 7 || previous[0] == 8) && (selected[0] == 7 || selected[0] == 8)) {
                                    addingButton.setEnabled(false);
                                    correctCount[0] = correctCount[0] + 1;
                                } else {
                                    //false match. re-enable buttons
                                    previousButton[0].setEnabled(true);
                                    previousButton[0].setBackground(buttonBackground);

                                    //change toast
                                    customToastroot = inflater.inflate(R.layout.toast_red, null);
                                    messageText = (TextView) customToastroot.findViewById(R.id.toastMessage);
                                    messageText.setText(getResources().getString(R.string.incorrect));

                                }

                                //change button back
                                previousButton[0].setBackground(buttonBackground);

                                //show result
                                toast.setView(customToastroot);
                                toast.setGravity(0, 0, 800);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();

                                //reset values
                                count[0] = 0;
                                previous[0] = 0;
                                selected[0] = 0;

                                //check for finish of game
                                if (correctCount[0] == allButtons.size() / 2) {
                                    //enable continue button
                                    confirm.setEnabled(true);
                                }

                            } else {
                                //get the first button selected
                                previous[0] = number;
                                count[0] = 1;

                                previousButton[0] = addingButton;
                                previousButton[0].setEnabled(false);
                                previousButton[0].setBackgroundResource(R.drawable.circle_button);
                            }

                        }
                    });

                    //add the buttons to the layout
                    if (buttonLayout11.getChildCount() <= 2) {
                        buttonLayout11.addView(addingButton);
                    } else if (buttonLayout11.getChildCount() > 2 && buttonLayout22.getChildCount() <= 2) {
                        buttonLayout22.addView(addingButton);
                    } else {
                        buttonLayout33.addView(addingButton);
                    }
                }//end for loop

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String givenAnswer = "Well done";

                        //reset buttons
                        buttonLayout11.removeAllViews();
                        buttonLayout22.removeAllViews();
                        buttonLayout33.removeAllViews();

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer, questionInfo, falseInfo, userlanguage, lesson);
                    }
                });
                break;
            case "MissingQuestion":
                //handle question here
                LinearLayout missingQuestionView = (LinearLayout) findViewById(R.id.missingQuestionLayout);
                missingQuestionView.setVisibility(View.VISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.MissingGoal));

                //disable button
                confirm.setEnabled(false);

                //set question
                TextView missingQuestionText = (TextView) findViewById(R.id.missingQuestionTextview);
                missingQuestionText.setText(question);

                //put back all views
                final RadioGroup missingGroup = (RadioGroup) findViewById(R.id.missingButtonGroup);
                missingGroup.getChildAt(0).setVisibility(View.VISIBLE);
                missingGroup.getChildAt(1).setVisibility(View.VISIBLE);
                missingGroup.getChildAt(2).setVisibility(View.VISIBLE);
                missingGroup.getChildAt(3).setVisibility(View.VISIBLE);
                missingGroup.clearCheck();

                final String[] givenAnswer = new String[1];

                //set text on buttons or hide it
                for (int i = 0; i < allButtons.size(); i++) {
                    RadioButton currentButton = (RadioButton) missingGroup.getChildAt(i);
                    if (String.valueOf(allButtons.get(i)).equals("none")) {
                        currentButton.setVisibility(View.GONE);
                    } else {
                        currentButton.setText(String.valueOf(allButtons.get(i)));
                    }
                }

                //get selected button
                missingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        confirm.setEnabled(true);

                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                        if (radioButton != null){
                            givenAnswer[0] = String.valueOf(radioButton.getText());
                        }
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //check answer
                        checkAnswer(questionNumber, answer, givenAnswer[0], questionInfo, falseInfo, userlanguage, lesson);
                    }
                });

                break;
            case "TranslateImageQuestion":
                //handle question here
                LinearLayout translateImageQuestionView = (LinearLayout) findViewById(R.id.translateImageQuestionLayout);
                translateImageQuestionView.setVisibility(View.VISIBLE);

                //set goal
                goalLbl.setText(getResources().getString(R.string.TranslateImageGoal));

                //disable button
                confirm.setEnabled(false);

                //set question
                TextView translateImageQuestionText = (TextView) findViewById(R.id.translateImageQuestionTextview);
                translateImageQuestionText.setText(question);

                //pass text into image helper
                ImageHelper imageHelperClass = new ImageHelper();
                int selectedImage = imageHelperClass.getImageForView(question);

                //set image from imageHelper
                ImageView myimageView = (ImageView) findViewById(R.id.translateImageImageview);
                myimageView.setBackgroundResource(selectedImage);

                //get input
                final EditText inputText = (EditText) findViewById(R.id.translateImageInput);
                inputText.addTextChangedListener(new TextWatcher() {
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

                        String givenAnswer = inputText.getText().toString();

                        //reset input
                        inputText.getText().clear();

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

    private void hideAllViews() {
        //layouts
        RelativeLayout imageQuestionView = (RelativeLayout) findViewById(R.id.imageQuestionLayout);
        RelativeLayout buttonQuestionView = (RelativeLayout) findViewById(R.id.buttonQuestionLayout);
        RelativeLayout translateQuestionView = (RelativeLayout) findViewById(R.id.translateQuestionLayout);
        LinearLayout missingQuestionView = (LinearLayout) findViewById(R.id.missingQuestionLayout);
        RelativeLayout resultView = (RelativeLayout) findViewById(R.id.resultLayout);
        RelativeLayout endGameView = (RelativeLayout) findViewById(R.id.endGameLayout);
        LinearLayout translateImageQuestionLayout = (LinearLayout) findViewById(R.id.translateImageQuestionLayout);

        //hide views
        imageQuestionView.setVisibility(View.GONE);
        buttonQuestionView.setVisibility(View.GONE);
        translateQuestionView.setVisibility(View.GONE);
        missingQuestionView.setVisibility(View.GONE);
        translateImageQuestionLayout.setVisibility(View.GONE);
        resultView.setVisibility(View.GONE);
        endGameView.setVisibility(View.GONE);
    }
}
