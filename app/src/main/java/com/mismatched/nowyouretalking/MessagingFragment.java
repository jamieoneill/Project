package com.mismatched.nowyouretalking;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class MessagingFragment extends android.support.v4.app.Fragment {

    private ListView mListview;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflating the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_message, null);

        // read message database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Messages");

        // get user info from profile class
        final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

        //set arrays for message info
        final ArrayList messageList = new ArrayList();
        final ArrayList conversationList = new ArrayList();
        final ArrayList participantList = new ArrayList();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    //if user is in this coversation
                    if (child.getKey().contains(getUserProfile.uid)) {

                        //get conversation
                        final String conversationkey = child.getKey();

                        //get the other user's name
                        final String participant = conversationkey.replace(getUserProfile.uid, "");

                        final DatabaseReference getusername = database.getReference("Users/" + participant);
                        getusername.addListenerForSingleValueEvent(new ValueEventListener() {


                            //// TODO: 10/03/2017 sort views by date or latest recived
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //add data to lists
                                messageList.add(dataSnapshot.child("Name").getValue().toString());
                                participantList.add(participant);
                                conversationList.add(conversationkey);

                                //use messageModel as default for hash map
                                final FirebaseListAdapter<MessageModel> firebaseListAdapter = new FirebaseListAdapter<MessageModel>(
                                        getActivity(),
                                        MessageModel.class,
                                        R.layout.items,
                                        myRef
                                ) {
                                    @Override
                                    protected void populateView(View v, MessageModel model, int position) {

                                        //fill message info
                                        final TextView tvfrom = (TextView) v.findViewById(R.id.nameLable);
                                        final TextView tvdate = (TextView) v.findViewById(R.id.dateLabel);
                                        final TextView tvtext = (TextView) v.findViewById(R.id.textLable);
                                        final ImageView impicture = (ImageView) v.findViewById(R.id.profileImage);

                                        //check if out of bounds
                                        boolean inBounds = (position >= 0) && (position < messageList.size());
                                        if (!inBounds) {
                                            Log.d("listsss ", String.valueOf(messageList.size()));
                                        }

                                        //add to view when in bounds
                                        else if (messageList.size() > 1) {
                                            //set name
                                            tvfrom.setText(messageList.get(position).toString());
                                            //set image
                                            getProfileImage(participantList.get(position).toString(), impicture);

                                            myRef.child(String.valueOf(conversationList.get(position))).orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                                                    //set date and text
                                                    tvdate.setText(dataSnapshot.child("date").getValue().toString());
                                                    tvtext.setText(dataSnapshot.child("text").getValue().toString());

                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }

                                            });

                                        } else if (messageList.size() <= 1) {
                                            //do nothing while initializing a single message
                                        }
                                    }
                                };

                                mListview = (ListView) v.findViewById(R.id.listview);
                                mListview.setAdapter(firebaseListAdapter);
                                mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                        //send intent with participant name and conversation key to message class
                                        Intent intent = new Intent(getActivity(), Message.class);
                                        intent.putExtra("conversation", conversationList.get(i).toString());
                                        intent.putExtra("Participant", participantList.get(i).toString());
                                        intent.putExtra("ParticipantName", messageList.get(i).toString());
                                        startActivity(intent);
                                    }

                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } //end if user message
                } // end database loop
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }

    public void getProfileImage(String participant, ImageView profileImage) {

        // path to /data/data/nowyourtalking/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File otheruser = new File(directory, participant); //file name of other user using the "from" id
        Activity activity = getActivity();

        ProfileFragment profileFragment = new ProfileFragment();
        //get image
        if (otheruser.length() != 0) {
            //if there is a image on device load it
            profileFragment.loadImageFromStorage(participant, profileImage, activity);
        } else {
            //else pull image from online storage
            profileFragment.loadProfileImage(participant, profileImage, activity);
        }
    }

}
