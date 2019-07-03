/**
 * this activity is the main surface of this app
 * show the images of app with grid layout
 * there are two function to add images into app
 * when use camera, after taking photo,save the photo you took and save the location of photo where it took.
 */
package com.example.lixiao.androidfinal;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lixiao.androidfinal.database.ImageElementViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class FirstaAtivity extends AppCompatActivity {

    private static final int PERMISSIONS_CODE = 233;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<ImageElement> myPictureList = new ArrayList<>();
    private RecyclerView.Adapter  mAdapter;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ImageElementViewModel mViewModel;
    private TextView mEmptyInfoView;
    private Toolbar mToolbar;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activity= this;

        mEmptyInfoView = findViewById(R.id.empty_info);
        mSearchView = findViewById(R.id.searchView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        // set up the RecyclerView
        int numberOfColumns = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mAdapter= new MyAdapter(myPictureList);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyInfoView.setText("Loading image\n\nPlease wait...\n\n");

        // required by Android 6.0 +
        checkPermissions(FirstaAtivity.this);

        /** @author @jkwiecien
        jkwiecien in Github
        URL:https://github.com/jkwiecien/EasyImage
        */
        initEasyImage();

        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.use_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openCamera(getActivity(), 0);
            }
        });

        FloatingActionButton fabPhoto = (FloatingActionButton) findViewById(R.id.add_photo);
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ImageElementViewModel.class);
        // Use LiveData to search in another thread
        // The UI is updated after observing the search is finished
        mViewModel.getAllData().observe(this, new Observer<List<ImageElement>>() {
            @Override
            public void onChanged(@Nullable List<ImageElement> imageElements) {
                myPictureList.clear();
                if (imageElements != null & imageElements.size() > 0) {
                    myPictureList.addAll(imageElements);
                    mAdapter.notifyDataSetChanged();
                    setViewStatus(true);
                } else {
                    setViewStatus(false);
                }
            }
        });

        mSearchView.setQueryHint("search by title or description");
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setIconified(false);
        mSearchView.onActionViewCollapsed();
        mSearchView.setMaxWidth(android.R.attr.width);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // As the title or description may contain "s", "%" should be added
                // Update the UI after observing the result of the search
                mViewModel.search("%" + s + "%").observe(FirstaAtivity.this, new Observer<List<ImageElement>>() {
                    @Override
                    public void onChanged(@Nullable List<ImageElement> imageElements) {
                        myPictureList.clear();
                        if (imageElements != null) {
                            myPictureList.addAll(imageElements);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * Updates the images being displayed, as an image may be deleted in ImageActivity.
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Update UI as one image might be deleted in ImageActivity
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Shows a message on main activity when there is no images in the database. Hides the message
     * when there are images.
     * @param hasImage the status whether there are images in the database
     *                   <code>true</code> when there are images.
     */
    private void setViewStatus(boolean hasImage) {
        if (hasImage) {
            // hide message, show images and search bar
            mEmptyInfoView.setVisibility(View.GONE);
            mSearchView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            // show the message
            mEmptyInfoView.setText("No image\n\nClick button\n\nto add\n\n");
            mEmptyInfoView.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }


    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("Photo Assistant")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    /**
     * Checks if the app has been granted the permissions needed
     * @param context FirstaAtivity.this
     * @param p        String of permissions needed
     * @return         <code>true</code> if all the permissions are granted
     *                  <code>false</code> otherwise
     */
    private boolean hasPermissions(final Context context, String[] p){
        boolean permissionsGranted = true;
        for (int i = 0; i < p.length; i++) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionsGranted = false;
            }
        }
        return permissionsGranted;
    }

    /**
     * Checks and ask for permissions when the current Android version is over 6.0
     * @param context FirstaAtivity.this
     */
    private void checkPermissions(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        // if Android version >=6.0
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            int permissions_code = 42;
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
            if(!hasPermissions(this, permissions)){
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE);
            }
            //
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {


                @SuppressWarnings("unused")
                String context ;
                Location location;

                for (File file : imageFiles) {
                    double latitude = 0;
                    double longitude = 0;
                    ImageElement imageElement = new ImageElement(file);
                    if (source == EasyImage.ImageSource.GALLERY || source == EasyImage.ImageSource.DOCUMENTS ) {
                        // if image from gallery, try to read location in exif
                        try {
                            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                            String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                            String lngRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                            Log.d("latlng", latValue + ',' + lngValue);
                            if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
                                try {
                                    latitude = convertRationalLatLonToFloat(latValue, latRef);
                                    longitude = convertRationalLatLonToFloat(lngValue, lngRef);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        imageElement.setLat(latitude);
                        imageElement.setLng(longitude);
                    }
                    else if (source == EasyImage.ImageSource.CAMERA) { //if taking photo
                        // Record time
                        imageElement.setTakeTime(sdf.format(new Date()));
                        // Record location
                        LocationActivity gps = new LocationActivity(FirstaAtivity.this);
                        if(gps.canGetLocation()){
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            imageElement.setLat(latitude);
                            imageElement.setLng(longitude);
                            // Log.d("gps_location",latitude+""+longitude);
                        }else {
                            imageElement.setLat(latitude);
                            imageElement.setLng(longitude);
                            gps.showSettingsAlert();
                        }
                    }
                    myPictureList.add(imageElement); // add to picture list
                    mViewModel.insert(imageElement); // save to database
                }
                // update UI
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(myPictureList.size() - 1);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private static float convertRationalLatLonToFloat(
            String rationalString, String ref) {
        try {
            String [] parts = rationalString.split(",");

            String [] pair;
            pair = parts[0].split("/");
            double degrees = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            pair = parts[1].split("/");
            double minutes = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            pair = parts[2].split("/");
            double seconds = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());

            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ((ref.equals("S") || ref.equals("W"))) {
                return (float) -result;
            }
            return (float) result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }

//    private double[] getGPS(){
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        List<String> providers = lm.getProviders(true);
//
//        Location l = null;
//
//        for(int i=providers.size()-1;i>=0;i--){
//            l=lm.getLastKnownLocation(providers.get(i));
//            if(l!=null) break;;
//        }
//    }


    public Activity getActivity() {
        return activity;
    }



}
