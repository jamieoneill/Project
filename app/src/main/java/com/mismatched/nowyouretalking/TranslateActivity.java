package com.mismatched.nowyouretalking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Created by jamie on 24/11/2016.
 */

public class TranslateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        //set views
        final TextView InputText = (TextView) findViewById(R.id.InputText);
        final TextView ResultLabel = (TextView) findViewById(R.id.ResultLabel);
        final Spinner FromLanguageSpinner = (Spinner) findViewById(R.id.FromLanguageSpinner);
        final Spinner ToLanguageSpinner = (Spinner) findViewById(R.id.ToLanguageSpinner);


        Button translateButton = (Button) findViewById(R.id.translateButton);
        translateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //must run on separate thread
                new Thread(){
                    public void run() {

                        //set authentication
                        Translate.setClientId("NowYoureTalking");
                        Translate.setClientSecret("nowyourtalkingsecret");

                        //get selected languages
                        String fromLanguage = String.valueOf(FromLanguageSpinner.getSelectedItem()).toUpperCase();
                        String toLanguage = String.valueOf(ToLanguageSpinner.getSelectedItem()).toUpperCase();

                        //get inputted text
                        String originalText  =  InputText.getText().toString();

                        try {
                            String translatedText = Translate.execute(originalText, Language.valueOf(fromLanguage), Language.valueOf(toLanguage));
                            ResultLabel.setText(translatedText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();


            }
        });


    }

    }
