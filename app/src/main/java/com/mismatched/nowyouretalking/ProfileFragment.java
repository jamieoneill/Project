package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 26/11/2016.
 */
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    // image var's
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    //set storage
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // get user info from profile class
    final UserProfileActivity.getUserProfile getUserProfile = new UserProfileActivity().new getUserProfile();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, null);

        //set buttons
        Button changePhotoButton = (Button) view.findViewById(R.id.ChangePhotoButton);
        changePhotoButton.setOnClickListener(this);

        Button TranslateActivityButton = (Button) view.findViewById(R.id.TranslateActivityButton);
        TranslateActivityButton.setOnClickListener(this);

        Button changeLanguageButton = (Button) view.findViewById(R.id.ChangeLanguageButton);
        changeLanguageButton.setOnClickListener(this);

        Button SignOutButton = (Button) view.findViewById(R.id.SignOutButton);
        SignOutButton.setOnClickListener(this);

        Button statsButton = (Button) view.findViewById(R.id.StatsButton);
        statsButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // path to /data/data/nowyourtalking/app_data/imageDir
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,getUserProfile.uid); //file name
        Activity activity = getActivity();
        ImageView imageview = (ImageView) getView().findViewById(R.id.ProfilePic);


        //get image
        if(mypath.length() != 0){
            //if there is a image on device load it
            loadImageFromStorage(getUserProfile.uid, imageview, activity);
        }
        else{
            //else pull image from online storage
            loadProfileImage(getUserProfile.uid, imageview, activity);
        }

    }


    @Override
    public void onClick(View v) {
        //start intents on click
        Intent intent;
        switch (v.getId()) {
            case R.id.ChangePhotoButton:
                // Create intent to Gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                break;
            case R.id.TranslateActivityButton:
                intent = new Intent(getActivity(), TranslateActivity.class);
                startActivity(intent);
                break;
            case R.id.ChangeLanguageButton:

                // get reference
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("Users");

                final CharSequence languages[] = new CharSequence[] {"French", "Spanish", "German"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick a Language to Learn");
                builder.setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myRef.child(getUserProfile.uid).child("Language").setValue(languages[which]);

                    }
                });
                builder.show();

                break;
            case R.id.SignOutButton:

                //sign out & return to sign in
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.StatsButton:

                //open stats
                intent = new Intent(getActivity(), StatsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check permissions
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // waiting for the user's response!
            } else {
                // request the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE);
                }

            }
        }
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                //get selected
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Create storage reference
                StorageReference storageRef = storage.getReferenceFromUrl("gs://now-yourre-talking.appspot.com/ProfilePictures/");

                // Create a reference to user picture
                StorageReference imageRef = storageRef.child(getUserProfile.uid);

                //upload image to storage
                Uri file = Uri.fromFile(new File(imgDecodableString));
                UploadTask uploadTask = imageRef.putFile(file);
                Toast.makeText(getActivity(), "Profile picture updated.", Toast.LENGTH_LONG).show();

                //save user's image to local
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                     saveToInternalStorage(bitmap, getUserProfile.uid, getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }


    }

    //fill users image to selected view
    public String loadProfileImage (final String userToLoad, final ImageView viewToFill, final Activity activity){

        // Create storage reference
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://now-yourre-talking.appspot.com/ProfilePictures/");

        //set image based on user id
        StorageReference  myProfilePic = storageRef.child(userToLoad);

        //set max image download size
        final long ONE_MEGABYTE = 10000 * 10000;
        myProfilePic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                //decode image
                Bitmap userImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                //save this user image to local device
                saveToInternalStorage(userImage, userToLoad, activity);

                //set circle style
                RoundedBitmapDrawable roundedImage = RoundedBitmapDrawableFactory.create(activity.getResources(), userImage);
                roundedImage.setCornerRadius(Math.max(userImage.getWidth(), userImage.getHeight()) / 2.0f);

                //set image to view
                ImageView imgView = viewToFill;
                imgView.setImageDrawable(roundedImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                    //reset to default image if no image is selected
                    StorageReference myProfilePic = storageRef.child("defaultImage");
                    myProfilePic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>(){
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //decode image
                            Bitmap userImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            //set circle style
                            RoundedBitmapDrawable roundedImage = RoundedBitmapDrawableFactory.create(activity.getResources(), userImage);
                            roundedImage.setCornerRadius(Math.max(userImage.getWidth(), userImage.getHeight()) / 2.0f);

                            //set image to view
                            ImageView imgView = viewToFill;
                            imgView.setImageDrawable(roundedImage);
                        }
                    });


            }
        });

                 return null;
    }

    //save user images when downloaded
    public String saveToInternalStorage(Bitmap bitmapImage, String userToSave, Activity activity){
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,userToSave); //file name

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void loadImageFromStorage(String userToLoad, ImageView viewToFill, Activity activity)
    {

        try {
            // path to /data/data/yourapp/app_data/imageDir
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // path for this user
            File mypath=new File(directory,userToLoad); //file name

            Bitmap userimage = BitmapFactory.decodeStream(new FileInputStream(mypath));

            //set circle style
            RoundedBitmapDrawable roundedImage = RoundedBitmapDrawableFactory.create(activity.getResources(), userimage);
            roundedImage.setCornerRadius(Math.max(userimage.getWidth(), userimage.getHeight()) / 2.0f);

            //set to imageview
            viewToFill.setImageDrawable(roundedImage);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}

