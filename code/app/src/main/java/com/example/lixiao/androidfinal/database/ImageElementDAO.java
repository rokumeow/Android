package com.example.lixiao.androidfinal.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.lixiao.androidfinal.ImageElement;

import java.util.List;

@Dao
public interface ImageElementDAO {

    @Insert
    void insertAll(ImageElement... images);

    @Insert
    void insert(ImageElement imageElement);

    @Delete
    void delete(ImageElement imageElement);

    @Update
    void update(ImageElement imageElement);

    @Query("SELECT * FROM ImageElement where id=:id")
    LiveData<ImageElement> getById(long id);

    @Query("SELECT * FROM ImageElement")
    LiveData<List<ImageElement>> getAllData();

    @Query("SELECT * FROM ImageElement where title like :keyword or description like :keyword")
    LiveData<List<ImageElement>> search(String keyword);

}
