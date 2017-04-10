package com.mismatched.nowyouretalking;

import android.app.Activity;

public class CardItem {

    private int mTextResource;
    private String mTitleResource;
    private Class mClassResource;
    private Activity mActivityResource;
    private String mLessonResource;

    public CardItem(String title, int text, Activity myActivity, Class myClass, String myLesson) {
        mTitleResource = title;
        mTextResource = text;
        mClassResource = myClass;
        mActivityResource = myActivity;
        mLessonResource = myLesson;
    }

    public int getText() {
        return mTextResource;
    }

    public String getTitle() {
        return mTitleResource;
    }

    public Class getMyClass() {
        return mClassResource;
    }

    public Activity getMyActivity() {return mActivityResource; }

    public String getLesson() {
        return mLessonResource;
    }

}
