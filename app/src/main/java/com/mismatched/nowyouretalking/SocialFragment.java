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

public class SocialFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View v = inflater.inflate(R.layout.social_fragment, null);

        //set buttons
        Button AddMeetActivityButton = (Button) v.findViewById(R.id.AddMeetActivityButton);
        AddMeetActivityButton.setOnClickListener(this);

        Button FindMeetActivityButton = (Button) v.findViewById(R.id.FindMeetActivityButton);
        FindMeetActivityButton.setOnClickListener(this);

        Button ManageMeetActivityButton = (Button) v.findViewById(R.id.ManageMeetActivityButton);
        ManageMeetActivityButton.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        switch (v.getId()) {
            case R.id.AddMeetActivityButton:
                intent = new Intent(getActivity(), MeetingActivity.class);
                startActivity(intent);
                break;
            case R.id.FindMeetActivityButton:
                intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.ManageMeetActivityButton:
                intent = new Intent(getActivity(), ManageActivity.class);
                startActivity(intent);
                break;
        }
    }
}