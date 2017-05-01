package com.mismatched.nowyouretalking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/**
 * Code for card Adapter taken from Rúben Sousa
 * https://github.com/rubensousa/ViewPagerCards/
 *
 * this code has been modified to suit my needs such as setting views and adding intents to open activity's
 */

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.lesson_switcher, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        //get values for starting game
        Button lessonButton = (Button) view.findViewById(R.id.lessonButton);
        final Class myintent = mData.get(position).getMyClass();
        final Activity myActivity = mData.get(position).getMyActivity();
        final String myLesson = mData.get(position).getLesson();
        String userLanguage = mData.get(position).getUserLanguage();

        lessonButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //load the game with the lesson
                Intent intent = new Intent(myActivity, myintent);
                intent.putExtra("Lesson",myLesson);

                myActivity.startActivity(intent);
            }
        });

        //set progress bar
        ProgressBar myProgressBar = (ProgressBar) view.findViewById(R.id.cardProgressBar);
        //get score from prefs
        SharedPreferences Prefs = container.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        int thisLessonScore = Prefs.getInt(userLanguage + myLesson, 0);
        myProgressBar.setProgress(thisLessonScore * 10);

        if(thisLessonScore == 10){
            //display completed lesson
            myProgressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(container.getContext(), R.color.Gold), PorterDuff.Mode.SRC_IN);
            ImageView icon = (ImageView) view.findViewById(R.id.bookmarkIcon);
            icon.setImageResource(R.drawable.bookmark_check);
            lessonButton.setText(container.getResources().getString(R.string.redo));

        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

}
