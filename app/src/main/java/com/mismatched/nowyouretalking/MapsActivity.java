package com.mismatched.nowyouretalking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // map variables
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    // get user
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Convert to get address
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // add api for location and connect
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        //database ref
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Meetings");

        //set images(country flags) depending on language
        final BitmapDescriptor German = BitmapDescriptorFactory.fromResource(R.drawable.germanyicon);
        final BitmapDescriptor French = BitmapDescriptorFactory.fromResource(R.drawable.franceicon);
        final BitmapDescriptor Spanish = BitmapDescriptorFactory.fromResource(R.drawable.spainicon);
        final BitmapDescriptor GermanDark = BitmapDescriptorFactory.fromResource(R.drawable.germanyicon_dark);
        final BitmapDescriptor FrenchDark = BitmapDescriptorFactory.fromResource(R.drawable.franceicon_dark);
        final BitmapDescriptor SpanishDark = BitmapDescriptorFactory.fromResource(R.drawable.spainicon_dark);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    //show if user is not already attending
                    String Guests = child.child("Attending").getValue().toString();

                    //find out if spaces are available
                    int NumGuests = child.child("NumGuests").getValue(int.class);
                    int Attending = (int) child.child("Attending").getChildrenCount();
                    int Spaces = NumGuests - Attending;

                    //if there is a space get info and display
                    if (Spaces >= 1) {
                         //get info and display

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

                        // marker icon holder
                        BitmapDescriptor markerIcon;

                        //add maker here and add the database key
                        if (Language.equals("French")) {
                            if(Guests.contains(user.getUid())){
                                markerIcon = FrenchDark;
                            }else{
                                markerIcon = French;
                            }
                            Marker marker = mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\nCreated By: " + Host).snippet("Address: " + Locations + "\nTime: " + MeetingTime + "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(markerIcon));
                            marker.setTag(child.getKey());
                        } else if (Language.equals("Spanish")) {
                            if(Guests.contains(user.getUid())){
                                markerIcon = SpanishDark;
                            }else{
                                markerIcon = Spanish;
                            }
                            Marker marker = mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\nCreated By: " + Host).snippet("Address: " + Locations + "\nTime: " + MeetingTime + "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(markerIcon));
                            marker.setTag(child.getKey());
                        } else if (Language.equals("German")) {

                            if(Guests.contains(user.getUid())){
                                markerIcon = GermanDark;
                            }else{
                                markerIcon = German;
                            }
                            Marker marker = mMap.addMarker(new MarkerOptions().position(newaddress).title(Titles + "\nCreated By: " + Host).snippet("Address: " + Locations + "\nTime: " + MeetingTime + "\nDate: " + MeetingDate + "\nLanguage: " + Language + "\nRecommended Level: " + MinLevel + "-" + MaxLevel + "\nAvailable Spaces: " + Spaces + "\nNote: " + Note).icon(markerIcon));
                            marker.setTag(child.getKey());
                        }

                    }//end no spaces check
                    } // end loop
                }

                @Override
                public void onCancelled (DatabaseError error){
                    // Failed to read value
                    Log.i("database ", "Failed to read value.", error.toException());
                }


        });


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                //get views for dialog
                View dialogView = View.inflate(MapsActivity.this, R.layout.markinfo_layout, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();

                // set dialog
                alertDialog.setView(dialogView);
                alertDialog.show();

                //set texts
                TextView title = (TextView) dialogView.findViewById(R.id.TitleLable);
                title.setText(marker.getTitle());

                TextView address = (TextView) dialogView.findViewById(R.id.MeetingText);
                address.setText(marker.getSnippet());

                //hide on cancel button
                Button Cancel = (Button) dialogView.findViewById(R.id.CancelButton);
                Cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        //don't show info window
                        marker.hideInfoWindow();
                    }
                });


                //join to add user to meet up
                Button Join = (Button) dialogView.findViewById(R.id.JoinButton);
                Join.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //look in attending for marker selected
                        myRef.child(marker.getTag().toString()).child("Attending").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //list to hold attendees
                                List<String> myList = new ArrayList<>();

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    //get all users currently attending meeting
                                    String guest = child.getValue(String.class);
                                    myList.add(guest);
                                }
                                //if user is already attending
                                if (myList.contains(user.getUid())) {
                                    Toast.makeText(MapsActivity.this, "You are already attending this meet up",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // add the user to attending for the marker selected
                                    myRef.child(marker.getTag().toString()).child("Attending").push().setValue(user.getUid());

                                    //tell user it has been added
                                    Toast.makeText(MapsActivity.this, "Meet up added", Toast.LENGTH_LONG).show();

                                    //change activity to break database updating loop
                                    Intent intent = new Intent(MapsActivity.this, ManageActivity.class);
                                    startActivity(intent);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.i("database ", "Failed to read value.", error.toException());
                            }
                        });
                    }
                });

                return false;
            }
        });

    }



    //return permissions
    private boolean hasPermission(MapsActivity mapsActivity, String accessFineLocation) {
        return ContextCompat.checkSelfPermission(mapsActivity, accessFineLocation) == PackageManager.PERMISSION_GRANTED;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {


        //Set GPS as the provider
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if GPS is enabled, if not open dialog to ask
        if (!enabled) {

            //enable location through dialog box
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();

                    try {
                        // Show the dialog by calling startResolutionForResult()
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);

                    } catch (IntentSender.SendIntentException e) {
                        Log.i("Location ", "Failed to get location.", e.getCause());
                    }

                }
            });
        }

        // set location if turned on
        setUserLocation();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setUserLocation() {
        //check for permissions to location
        if (hasPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //return user location is permission is set
            mMap.setMyLocationEnabled(true);
        } else {
            //request permission if not set
            this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        //set view to user last known location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double mLat = mLastLocation.getLatitude();
            double mLng = mLastLocation.getLongitude();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLng), 10.0f));

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:

                switch (resultCode) {
                    case Activity.RESULT_OK:

                            //tell user to to zoom in on map
                            Toast toast=Toast.makeText(MapsActivity.this, "Click here to zoom to your location", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.END| Gravity.TOP, 20, 210);
                            toast.show();
                break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MapsActivity.this, "Location not turned on", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
