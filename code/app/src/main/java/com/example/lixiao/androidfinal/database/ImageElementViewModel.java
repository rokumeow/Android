package com.example.lixiao.androidfinal.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.lixiao.androidfinal.ImageElement;

import java.util.List;


public class ImageElementViewModel extends AndroidViewModel {
    private ImageElementRepository repository;
    public ImageElementViewModel(@NonNull Application application) {
        super(application);
        repository = new ImageElementRepository(application);
    }
    /**
     * Calls repository to search for the image by id
     * @param id the id of the image
     */
    public LiveData<ImageElement> getById(long id){
        return repository.getById(id);
    }

    /**
     * Calls repository to fetch all images in the database
     */
    public LiveData<List<ImageElement>> getAllData() {
        return repository.getAllData();
    }

    /**
     * Calls repository to search for the image by keyword
     * @param keyword the input keyword
     */
    public LiveData<List<ImageElement>> search(String keyword) {
        return repository.search(keyword);
    }

    /**
     * Inserts images to the database
     * @param imageElements the imageElement object to be inserted
     */
    public void insert(ImageElement... imageElements){
        repository.insert(imageElements);
    }

    /**
     * Calls repository to update
     * @param imageElements the imageElement objects to be updated
     */
    public void update(ImageElement... imageElements){
        repository.update(imageElements);
    }

    /**
     * Calls repository to delete an image
     * @param imageElement the imageElement object to be deleted
     */
    public void delete(ImageElement imageElement) {
        repository.delete(imageElement);
    }

}
