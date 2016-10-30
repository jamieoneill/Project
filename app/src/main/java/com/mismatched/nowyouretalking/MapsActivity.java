package com.mismatched.nowyouretalking;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat =0 , lng = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button joinButton = (Button) findViewById(R.id.JoinButton);
        Button cancelButton = (Button) findViewById(R.id.CancelButton);

        joinButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

    }

    //Convert to get address
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    //TODO handle join meet up button

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //set map to dublin.... TODO change to user location
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(53.3441,-6.2675) , 6.0f) );

        //database ref
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Meetings");

        //set images(colours) depending on language TODO change to country flags maybe
        final BitmapDescriptor French =  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        final BitmapDescriptor Spanish =  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        final BitmapDescriptor German =  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieves all in meeting database
                Map<String,Object> dbRead=(Map<String,Object>)dataSnapshot.getValue();
                List<String> myList = new ArrayList<String>();

                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    //find out if spaces are available
                    int NumGuests = child.child("NumGuests").getValue(int.class);
                    int Attending = (int) child.child("Attending").getChildrenCount();
                    int Spaces = NumGuests - Attending;

                    if(Spaces <= 0){
                        //if no spaces do nothing, don't display, skip over this entry in the database

                    }else { //get info and display

                        //get info from DB
                        String Host = child.child("Host").getValue(String.class);
                        String Titles = child.child("Title").getValue(String.class);
                        String Locations = child.child("Location").getValue(String.class);
                        String MeetingTime = child.child("MeetingTime").getValue(String.class);
                        String MeetingDate = child.child("MeetingDate").getValue(String.class);
                        String Language = child.child("Language").getValue(String.class);
                        int MinLevel = child.child("MinLevel").getValue(int.class);
                        int MaxLevel = child.child("MaxLevel").getValue(int.class);
                        String Note = child.child("Note").getValue(String.class);

                        //convert address
                        LatLng newaddress = getLocationFromAddress(MapsActivity.this, Locations);

                        //add to maker here
                        if (Language.equals("French")) {
                            mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\n Created By: " + Host).snippet("Address: " + Locations + "\nTime: " +MeetingTime + "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(French));
                        } else if (Language.equals("Spanish")) {
                            mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\n Created By: " + Host).snippet("Address: " + Locations + "\nTime: " +MeetingTime +  "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(Spanish));
                        } else if (Language.equals("German")) {
                            mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\n Created By: " + Host).snippet("Address: " + Locations + "\nTime: " +MeetingTime +  "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(German));
                        }
                    }


                      }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("database ", "Failed to read value.", error.toException());
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                //set info window
                Context context = getApplicationContext();
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);


                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setBackgroundColor(Color.parseColor("#61D9FF"));
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);
                marker.hideInfoWindow();

                //hide everything on cancel button
                Button Cancel = (Button) findViewById(R.id.CancelButton);
                Cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button joinButton = (Button) findViewById(R.id.JoinButton);
                        Button cancelButton = (Button) findViewById(R.id.CancelButton);

                        joinButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);

                        marker.hideInfoWindow();
                    }
                });

                return info;
            }


        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng mapclick) {
                //hide everything
                Button joinButton = (Button) findViewById(R.id.JoinButton);
                Button cancelButton = (Button) findViewById(R.id.CancelButton);

                joinButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);

            }
        });


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //show buttons
                Button joinButton = (Button) findViewById(R.id.JoinButton);
                Button cancelButton = (Button) findViewById(R.id.CancelButton);

                joinButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                return false;
            }
        });


    }
}
