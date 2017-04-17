package com.mismatched.nowyouretalking;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;
import java.util.Locale;

public class TranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private final int SPEECH_RECOGNITION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        //set views
        final TextView InputText = (TextView) findViewById(R.id.InputText);
        final TextView ResultLabel = (TextView) findViewById(R.id.ResultLabel);
        final Spinner FromLanguageSpinner = (Spinner) findViewById(R.id.FromLanguageSpinner);
        final Spinner ToLanguageSpinner = (Spinner) findViewById(R.id.ToLanguageSpinner);

        //set authentication
        Translate.setClientId("NowYoureTalking");
        Translate.setClientSecret("nowyourtalkingsecret");

        //speech to text
        Button stt = (Button) findViewById(R.id.speechToText);
        stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromLanguage = String.valueOf(FromLanguageSpinner.getSelectedItem()).toUpperCase();

                startSpeechToText(fromLanguage);
            }
        });

        //set text to speech
        tts = new TextToSpeech(this, this);

        Button translateButton = (Button) findViewById(R.id.translateButton);
        translateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //check for internet
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                if(activeNetworkInfo  != null){
                    //must run on separate thread
                    new Thread(){
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        public void run() {

                            //get selected languages
                            String fromLanguage = String.valueOf(FromLanguageSpinner.getSelectedItem()).toUpperCase();
                            String toLanguage = String.valueOf(ToLanguageSpinner.getSelectedItem()).toUpperCase();

                            //get inputted text
                            String originalText  =  InputText.getText().toString();

                            try {
                                //return translation
                                String translatedText = Translate.execute(originalText, Language.valueOf(fromLanguage), Language.valueOf(toLanguage));
                                ResultLabel.setText(translatedText);

                                //check Achievements
                                AchievementHelper AchievementHelperClass = new AchievementHelper();
                                AchievementHelperClass.UnlockAchievement("UseTheTranslator", TranslateActivity.this);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                }
                else{

                    //open dialog box
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranslateActivity.this);
                    builder.setTitle(getResources().getString(R.string.NoConnection));
                    builder.setMessage(getResources().getString(R.string.ConnectionNeeded));

                    builder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });

                    builder.show();
                }

            }
        });

        ImageButton SpeakButton = (ImageButton) findViewById(R.id.SpeakButton);
        SpeakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //call speak method
                String toLanguage = String.valueOf(ToLanguageSpinner.getSelectedItem()).toUpperCase();
                speakOut(ResultLabel.getText().toString(), toLanguage);

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(String input, String language) {

        Locale speechLanguage;

        // set voice to destination language
        switch (language) {
            case "SPANISH":
                speechLanguage = new Locale("spa", "MEX");
                tts.setLanguage(speechLanguage);
                break;
            case "FRENCH":
                speechLanguage = Locale.FRANCE;
                tts.setLanguage(speechLanguage);
                break;
            case "GERMAN":
                speechLanguage = Locale.GERMAN;
                tts.setLanguage(speechLanguage);
                break;
            case "ENGLISH":
                speechLanguage = Locale.ENGLISH;
                tts.setLanguage(speechLanguage);
                break;
        }


        String utteranceId=this.hashCode() + "";
            //speak text.. different for version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(input, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
        {
            tts.speak(input, TextToSpeech.QUEUE_FLUSH, null);
       }

    }

    @Override
    public void onInit(int status) {
        // catch  tts error
        if (status != TextToSpeech.SUCCESS) {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText(String language) {

        String speechLanguage = null;

        // set voice language
        switch (language) {
            case "SPANISH":
                speechLanguage = "es-ES";
                break;
            case "FRENCH":
                speechLanguage = "fr-FR";
                break;
            case "GERMAN":
                speechLanguage = "de-DE";
                break;
            case "ENGLISH":
                speechLanguage = "en-GB";
                break;
        }
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.sayToTranslate));
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.speechNotSupported), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    final TextView InputText = (TextView) findViewById(R.id.InputText);
                    InputText.setText(text);
                }
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {

        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
