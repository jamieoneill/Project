package com.mismatched.nowyouretalking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**
 * Created by jamie on 10/03/2017.
 */


public class FriendFragment  extends android.support.v4.app.Fragment  {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get a string resource from Resources
        final String searchString = getResources().getString(R.string.search);
        final String enterFriendString = getResources().getString(R.string.friendEmail);
        final String enterString = getResources().getString(R.string.enter);
        final String cancelString = getResources().getString(R.string.cancel);
        final String messageString = getResources().getString(R.string.Messages);
        final String addContactString = getResources().getString(R.string.addFriend);
        final String addString = getResources().getString(R.string.add);

        //set layout
        final View v = inflater.inflate(R.layout.fragment_friend, null);

        // get user info from profile class
        final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(searchString);
                builder.setMessage(enterFriendString);
                builder.setView(input);

                builder.setPositiveButton(enterString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String emailAddress = input.getText().toString();

                        //get friends
                        final DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference("Users/");

                        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                                    if(emailAddress.equals(child.child("Email").getValue())){

                                        //open dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle(child.child("Name").getValue().toString());
                                        builder.setMessage(addContactString);

                                        builder.setPositiveButton(addString, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //add to contact list
                                                phoneRef.child(getUserProfile.uid).child("Friends").child(child.getKey()).setValue(child.child("Name").getValue());

                                            }
                                        });
                                        builder.setNeutralButton(cancelString, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });

                                        builder.show();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //cancelled
                            }
                        });


                    }
                });

                builder.show();
            }
        });

        //set listview
        ListView mListview = (ListView) v.findViewById(R.id.listview);

        //get friends
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users/" + getUserProfile.uid + "/Friends");

        final FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                getActivity(),
                String.class,
                android.R.layout.simple_list_item_1,
                myRef
        ) {
            @Override
            protected void populateView(View v, String model, int position) {

                TextView textView = (TextView)v.findViewById(android.R.id.text1);
                textView.setText(model);

            }
        };

        mListview.setAdapter(firebaseListAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get username
                final String username = (String) adapterView.getItemAtPosition(i);

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (final DataSnapshot child : dataSnapshot.getChildren()) {
                            if(child.getValue().equals(username)){
                                //get keys
                                final String userKey = child.getKey();
                                DatabaseReference phoneKey = FirebaseDatabase.getInstance().getReference("Users/" + userKey);

                                phoneKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        //get phone number
                                        final String email = snapshot.child("/Email").getValue().toString();

                                        //open dialog box
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle(username);
                                        builder.setMessage(email);

                                        builder.setPositiveButton(messageString, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //send intent with participant name to message class
                                                Intent intent = new Intent(getActivity(), Message.class);
                                                intent.putExtra("conversation", userKey + getUserProfile.uid);
                                                intent.putExtra("Participant", userKey);
                                                intent.putExtra("ParticipantName",username);

                                                startActivity(intent);
                                            }
                                        });
                                        builder.setNeutralButton(cancelString, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });

                                        builder.show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //cancelled
                    }
                });

            }
        });

        return v;
    }

}
