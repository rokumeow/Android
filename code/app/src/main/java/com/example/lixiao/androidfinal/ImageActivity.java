/**
 * this activity is link to show_photo.xml
 * aim to show the detail of image you choose on FirstActivity
 */
package com.example.lixiao.androidfinal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lixiao.androidfinal.database.ImageElementViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

public class ImageActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private TextView mTitle;
    private TextView mDescription;
    private TextView mTime;
    private ImageView mImageView;
    private ImageElement imageElement;
    private ImageElementViewModel mViewModel;
    private static long id;
    private String imageSize;
    private GoogleMap mMap;
    private MapView mMapView;
    private Location mLocation;
    private LatLng mSite;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Button see_photo;
    private Bitmap myBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);
        mTitle=findViewById(R.id.title_detail);
        mDescription=findViewById(R.id.description_detail);
        mTime=findViewById(R.id.time_detail);
        mMapView=findViewById(R.id.location_map);
        mImageView = findViewById(R.id.image);
        Intent intent=getIntent();
        id=intent.getLongExtra("id",0);
        if(id==0){
            finish();
            return;
        }
        mViewModel = ViewModelProviders.of(this).get(ImageElementViewModel.class);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);



        // asynchronously get the image by id and display
        mViewModel.getById(id).observe(this, new Observer<ImageElement>() {
            @Override
            public void onChanged(@Nullable ImageElement imageElement) {
                if(imageElement!=null) {
                    ImageActivity.this.imageElement = imageElement;
                    myBitmap = BitmapFactory.decodeFile(imageElement.getFileName());
                    // Log.d("location_",imageElement.getLat()+" "+imageElement.getLng());
                    double lat = imageElement.getLat();
                    double lng = imageElement.getLng();
                    // when there is location data
                    if (lat != 0 || lng != 0) {
                        mSite = new LatLng(imageElement.getLat(), imageElement.getLng());
                        if (mSite != null) {
                            mMap.addMarker(new MarkerOptions().position(mSite));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mSite));
                        }
                    }
                    else {
                        // hide map when there is no location data
                        TextView loactionText = findViewById(R.id.text_location);
                        mMapView.setVisibility(View.GONE);
                        loactionText.setVisibility(View.GONE);
                    }
//                    mLocation.setLatitude(imageElement.getLat());
//                    mLocation.setLongitude(imageElement.getLng());

//                    Log.d("mLocation is",mLocation.getLatitude()+""+mLocation.getLongitude());
                    imageSize=myBitmap.getWidth()+"x"+myBitmap.getHeight();
                    // calculate the size
                    // size is not calculated when adding to the database as loading the image may
                    // be time consuming
                    int size=myBitmap.getByteCount();
                    if(size<1024){
                        imageSize+=String.format(" %dB",size);
                    }else if(size<1024*1024){
                        imageSize+=String.format(" %1.2fKB",size/1024.0);
                    }else{
                        imageSize+=String.format(" %1.2fMB",size/1024.0/1024.0);
                    }
                    mImageView.setImageBitmap(myBitmap);
                    // display image info
                    String title = imageElement.getTitle();
                    String description = imageElement.getDescription();
                    if (title != null) {
                        if (title.length() > 15)
                            mTitle.setText(title.substring(0, 15) + "...");
                        else
                            mTitle.setText(title);
                    }
                    if (description != null) {
                        if (description.length() > 15)
                            mDescription.setText(description.substring(0, 15) + "...");
                        else
                            mDescription.setText(description);
                    }
                    else
                        mDescription.setText("No description.");
                    if(imageElement.getTakeTime()!=null) {
                        mTime.setText(imageElement.getTakeTime());
                    }
                    else
                        mTime.setText("Time not available.");
//                    mSite = new LatLng(imageElement.getLat(),imageElement.getLng());
                }
            }
        });

        /**@param see_photo
         * FloatingActionButton, showing large image by click
         */
        FloatingActionButton see_photo = (FloatingActionButton) findViewById(R.id.see_photo);
        see_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramView) {
                LayoutInflater inflater = LayoutInflater.from(ImageActivity.this);
                View imgEntryView = inflater.inflate(R.layout.only_photo, null);
                final AlertDialog dialog = new AlertDialog.Builder(ImageActivity.this).create();
                ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                img.setImageBitmap(myBitmap);
                dialog.setView(imgEntryView); // set the view of dialog
                dialog.show();
                // close the dialog after clicking the image
                imgEntryView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        dialog.cancel();
                    }
                });
            }
        });

        /**@param edit_delete FloatingActionButton
         * delete image
         *
         */
        FloatingActionButton edit_delete = (FloatingActionButton) findViewById(R.id.edit_delete);
        edit_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImageElement();
            }
        });

        /**@param edit_information FloatingActionButton
         * edit information for image
         *
         */
        FloatingActionButton editInfo = (FloatingActionButton) findViewById(R.id.editor_information);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editImageInfo();
            }
        });

    }

    /**
     * Deletes the image in database, image list and cache, asynchronously
     */
    private void deleteImageElement() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.delete);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to delete?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file=new File(imageElement.getFileName());
                // asynchronous delete image in the database
                mViewModel.delete(imageElement);
                // delete in the UI
                MyAdapter.removeById(id);
                // delete in the cache
                if(file.exists()){
                    file.delete();
                }
                ImageActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.setCancelable(true);
        builder.create().show();
    }

    /**
     * Creates the intent to open InformationActivity, image id is passed.
     */
    private void editImageInfo() {
        Intent intent=new Intent(this, InformationActivity.class);
        intent.putExtra("id", id);
        // intent.putExtra("imageSize", imageSize);
        startActivity(intent);
    }

    public void toMap(){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * override onMapReady to set the details of MapView
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                toMap();
                return false;
            }

        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);

    }

    /**
     * override onMarkerClick to set action when click marker on MapView
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer clickCount = (Integer) marker.getTag();
        if(clickCount != null){
            clickCount = clickCount+1;
            marker.setTag(clickCount);
            Toast.makeText(this,marker.getTitle()+"has been clicked" + "times.",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }



    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
