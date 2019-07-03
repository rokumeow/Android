package com.example.lixiao.androidfinal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

@Entity()
public class ImageElement {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String fileName;
    private long fileSize;
    private String takeTime;
    private String description;
    private double lat;
    private double lng;

    public ImageElement(){

    }

    public ImageElement(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        this.title = name.substring(0, dot);
        this.fileName = file.getAbsolutePath();
        this.fileSize = file.length();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(double lat) {this.lat = lat; }

    public void setLng(double lng) {this.lng = lng; }

    public double getLat() {return lat;}

    public double getLng() {return lng;}

}
