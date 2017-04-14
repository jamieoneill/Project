package com.mismatched.nowyouretalking;

import android.app.Activity;

public class CardItem {

    private int mTextResource;
    private String mTitleResource;
    private Class mClassResource;
    private Activity mActivityResource;
    private String mLessonResource;
    private String mUserLanguage;

    public CardItem(String title, int text, Activity myActivity, Class myClass, String myLesson, String userLanguage) {
        mTitleResource = title;
        mTextResource = text;
        mClassResource = myClass;
        mActivityResource = myActivity;
        mLessonResource = myLesson;
        mUserLanguage = userLanguage;
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

    public String getUserLanguage() {
        return mUserLanguage;
    }
}
