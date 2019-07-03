/**
 * this activity is show the surface on changing information of image
 */
package com.example.lixiao.androidfinal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lixiao.androidfinal.database.ImageElementViewModel;

public class InformationActivity extends AppCompatActivity {

    private EditText mDescription;
    private TextView mTitle;
    private ImageElement imageElement;
    private ImageElementViewModel mViewModel;
    private long id;
    private String imageSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDescription=findViewById(R.id.description);
        mTitle=findViewById(R.id.title_detail);
        Intent intent=getIntent();
        id=intent.getLongExtra("id", 0);
        imageSize=intent.getStringExtra("imageSize");

        if(id==0){
            finish();
        }

        mViewModel = ViewModelProviders.of(this).get(ImageElementViewModel.class);
        // get detail of image from image ID
        mViewModel.getById(id).observe(this, new Observer<ImageElement>() {
            @Override
            public void onChanged(@Nullable ImageElement imageElement) {
                if(imageElement!=null) {
                    InformationActivity.this.imageElement = imageElement;
                    StringBuilder sb=new StringBuilder();
                    sb.append("Title: "+imageElement.getTitle()+"\n");
                    if(imageElement.getTakeTime()!=null) {
                        sb.append("Date: " + imageElement.getTakeTime() + "\n");
                    }
                    sb.append("Size: "+imageSize+"\n");
                    sb.append("Path: "+imageElement.getFileName());
                    mTitle.setText(imageElement.getTitle());
                    mDescription.setText(imageElement.getDescription());
                }
            }
        });


    }

    public void backClick(View view) {
        finish();
    }

    public void saveClick(View view) {
        // save the changes of image
        imageElement.setTitle(mTitle.getText().toString());
        imageElement.setDescription(mDescription.getText().toString());
        mViewModel.update(imageElement);
        finish();
    }


}