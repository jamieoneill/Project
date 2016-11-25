package com.mismatched.nowyouretalking;

import android.annotation.TargetApi;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import java.util.Locale;

/**
 * Created by jamie on 24/11/2016.
 */

public class TranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

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

        //set text to speech
        tts = new TextToSpeech(this, this);

        Button translateButton = (Button) findViewById(R.id.translateButton);
        translateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();


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
        if(language.equals("SPANISH")){
            speechLanguage = new Locale("spa", "MEX");
            tts.setLanguage(speechLanguage);
        }
        else if(language.equals("FRENCH")){
            speechLanguage =  Locale.FRANCE;
            tts.setLanguage(speechLanguage);
        }
        else if(language.equals("GERMAN")){
            speechLanguage = Locale.GERMAN;
            tts.setLanguage(speechLanguage);
        }
        else if(language.equals("ENGLISH")){
            speechLanguage = Locale.ENGLISH;
            tts.setLanguage(speechLanguage);
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


    @Override
    protected void onDestroy() {

        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
            Log.i("onDestroy", "TTS Destroyed");
        }
        super.onDestroy();
    }
}
