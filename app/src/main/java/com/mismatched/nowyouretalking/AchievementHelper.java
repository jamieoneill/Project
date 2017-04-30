package com.mismatched.nowyouretalking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jamie on 17/04/2017.
 */

public class AchievementHelper {

    public void CheckUnlockedAchievements(View v, final Activity activity) {

        //get Achievements
        SharedPreferences Achievements = activity.getSharedPreferences("Achievements", MODE_PRIVATE);
        Boolean CompleteYourFirstLessonAchievement = Achievements.getBoolean("CompleteYourFirstLesson", false);
        Boolean ReachLevel5Achievement = Achievements.getBoolean("ReachLevel5", false);
        Boolean MasterALanguageAchievement = Achievements.getBoolean("MasterALanguage", false);
        Boolean Attend3MeetUpsAchievement = Achievements.getBoolean("Attend3MeetUps", false);
        Boolean Attend10MeetUpsAchievement = Achievements.getBoolean("Attend10MeetUps", false);
        Boolean CompleteALessonAfter11Achievement = Achievements.getBoolean("CompleteALessonAfter11", false);
        Boolean TryMoreThanOneLanguageAchievement = Achievements.getBoolean("TryMoreThanOneLanguage", false);
        Boolean AddAFriendAchievement = Achievements.getBoolean("AddAFriend", false);
        Boolean UseTheTranslatorAchievement = Achievements.getBoolean("UseTheTranslator", false);
        Boolean CompleteALessonBefore8Achievement = Achievements.getBoolean("CompleteALessonBefore8", false);

        ImageView CompleteYourFirstLesson = (ImageView) v.findViewById(R.id.CompleteYourFirstLesson);
        ImageView ReachLevel5 = (ImageView) v.findViewById(R.id.ReachLevel5);
        ImageView MasterALanguage = (ImageView) v.findViewById(R.id.MasterALanguage);
        ImageView Attend3MeetUps = (ImageView) v.findViewById(R.id.Attend3MeetUps);
        ImageView Attend10MeetUps = (ImageView) v.findViewById(R.id.Attend10MeetUps);
        ImageView CompleteALessonAfter11 = (ImageView) v.findViewById(R.id.CompleteALessonAfter11);
        ImageView TryMoreThanOneLanguage = (ImageView) v.findViewById(R.id.TryMoreThanOneLanguage);
        ImageView AddAFriend = (ImageView) v.findViewById(R.id.AddAFriend);
        ImageView UseTheTranslator = (ImageView) v.findViewById(R.id.UseTheTranslator);
        ImageView CompleteALessonBefore8 = (ImageView) v.findViewById(R.id.CompleteALessonBefore8);

        if (CompleteYourFirstLessonAchievement) {
            CompleteYourFirstLesson.setBackgroundResource(R.drawable.achievements_completeyourfirstlesson);
        }
        if (ReachLevel5Achievement) {
            ReachLevel5.setBackgroundResource(R.drawable.achievements_reachlevel5);
        }
        if (MasterALanguageAchievement) {
            MasterALanguage.setBackgroundResource(R.drawable.achievements_masteralanguage);
        }
        if (Attend3MeetUpsAchievement) {
            Attend3MeetUps.setBackgroundResource(R.drawable.achievements_attend3meetups);
        }
        if (Attend10MeetUpsAchievement) {
            Attend10MeetUps.setBackgroundResource(R.drawable.achievements_attend10meetups);
        }
        if (CompleteALessonAfter11Achievement) {
            CompleteALessonAfter11.setBackgroundResource(R.drawable.achievements_completealessonafter11);
        }
        if (TryMoreThanOneLanguageAchievement) {
            TryMoreThanOneLanguage.setBackgroundResource(R.drawable.achievements_trymorethanonelanguage);
        }
        if (AddAFriendAchievement) {
            AddAFriend.setBackgroundResource(R.drawable.achievements_addafriend);
        }
        if (UseTheTranslatorAchievement) {
            UseTheTranslator.setBackgroundResource(R.drawable.achievements_usethetranslator);
        }
        if (CompleteALessonBefore8Achievement) {
            CompleteALessonBefore8.setBackgroundResource(R.drawable.achievements_completealessonbefore8);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get views for achievement dialog
                View dialogView = View.inflate(activity, R.layout.achievement_layout, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

                // set dialog
                alertDialog.setView(dialogView);
                alertDialog.show();

                //set views
                TextView title = (TextView) dialogView.findViewById(R.id.achievementTitle);
                TextView message = (TextView) dialogView.findViewById(R.id.achievementText);
                ImageView image = (ImageView) dialogView.findViewById(R.id.achievementImage);

                //set texts
                switch (v.getId()) {
                    case R.id.CompleteYourFirstLesson:
                        title.setText(activity.getString(R.string.beginnerTitle));
                        message.setText(activity.getString(R.string.beginnerAchievement));
                        break;
                    case R.id.ReachLevel5:
                        title.setText(activity.getString(R.string.onMyWayTitle));
                        message.setText(activity.getString(R.string.onMyWayAchievement));
                        break;
                    case R.id.MasterALanguage:
                        title.setText(activity.getString(R.string.theMasterTitle));
                        message.setText(activity.getString(R.string.theMasterAchievement));
                        break;
                    case R.id.Attend3MeetUps:
                        title.setText(activity.getString(R.string.meetingNewPeopleTitle));
                        message.setText(activity.getString(R.string.meetingNewPeopleAchievement));
                        break;
                    case R.id.Attend10MeetUps:
                        title.setText(activity.getString(R.string.enjoyingTheCompanyTitle));
                        message.setText(activity.getString(R.string.enjoyingTheCompanyAchievement));
                        break;
                    case R.id.CompleteALessonAfter11:
                        title.setText(activity.getString(R.string.nightOwlTitle));
                        message.setText(activity.getString(R.string.nightOwlAchievement));
                        break;
                    case R.id.TryMoreThanOneLanguage:
                        title.setText(activity.getString(R.string.mixItUpTitle));
                        message.setText(activity.getString(R.string.mixItUpAchievement));
                        break;
                    case R.id.AddAFriend:
                        title.setText(activity.getString(R.string.gettingSocialTitle));
                        message.setText(activity.getString(R.string.gettingSocialAchievement));
                        break;
                    case R.id.UseTheTranslator:
                        title.setText(activity.getString(R.string.speakToMeTitle));
                        message.setText(activity.getString(R.string.speakToMeAchievement));
                        break;
                    case R.id.CompleteALessonBefore8:
                        title.setText(activity.getString(R.string.earlyRiserTitle));
                        message.setText(activity.getString(R.string.earlyRiserAchievement));
                        break;
                }
                //set image
                image.setImageDrawable(v.getBackground());

                //ok button
                Button closeBtn = (Button) dialogView.findViewById(R.id.closeButton);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        };

        //add click listeners
        CompleteYourFirstLesson.setOnClickListener(listener);
        ReachLevel5.setOnClickListener(listener);
        MasterALanguage.setOnClickListener(listener);
        Attend3MeetUps.setOnClickListener(listener);
        Attend10MeetUps.setOnClickListener(listener);
        CompleteALessonAfter11.setOnClickListener(listener);
        TryMoreThanOneLanguage.setOnClickListener(listener);
        AddAFriend.setOnClickListener(listener);
        UseTheTranslator.setOnClickListener(listener);
        CompleteALessonBefore8.setOnClickListener(listener);
    }

    public void UnlockAchievement(String unlock, Activity activity){

        //get Achievements
        SharedPreferences Achievements = activity.getSharedPreferences("Achievements", MODE_PRIVATE);
        Boolean thisAchievement = Achievements.getBoolean(unlock, false);

        //if it hasn't been unlocked yet unlock it
        if(!thisAchievement){
        SharedPreferences.Editor editor = Achievements.edit();
        editor.putBoolean(unlock, true);
        editor.apply();

        //toast
        Toast.makeText(activity, activity.getString(R.string.achivementUnlocked), Toast.LENGTH_SHORT).show();
        }
    }

}
