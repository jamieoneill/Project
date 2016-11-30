package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 26/11/2016.
 */
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LearnFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View v = inflater.inflate(R.layout.learn_fragment, null);

        //set buttons
        Button TranslateActivityButton = (Button) v.findViewById(R.id.TranslateActivityButton);
        TranslateActivityButton.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        switch (v.getId()) {
            case R.id.TranslateActivityButton:
                intent = new Intent(getActivity(), TranslateActivity.class);
                startActivity(intent);
                break;
        }
    }
}