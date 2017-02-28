package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 26/11/2016.
 */
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.plus.model.people.Person;

public class LearnFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View v = inflater.inflate(R.layout.levelselect_fragment, null);
       // Button foodimage = (Button) v.findViewById(R.id.foodBtn);

        return v;
    }


    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        switch (v.getId()) {
            case R.id.basicsBtn:
                //start game

                break;
            case R.id.phrasesBtn:
                //do something
                break;

        }
    }
}