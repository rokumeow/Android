package com.example.lixiao.androidfinal.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.lixiao.androidfinal.ImageElement;

import java.util.List;


public class ImageElementRepository extends ViewModel {
    private ImageElementDAO imageElementDAO;
    public ImageElementRepository(@NonNull Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        imageElementDAO = db.imageElementDao();
    }
    /**
     * Calls DAO to search for the image by id
     * @param id the id of the image
     */
    public LiveData<ImageElement> getById(long id){
        return imageElementDAO.getById(id);
    }

    /**
     * Calls DAO to fetch all images in the database
     */
    public LiveData<List<ImageElement>> getAllData() {
        return imageElementDAO.getAllData();
    }

    /**
     * Calls DAO to search for the image by keyword
     * @param keyword the input keyword
     */
    public LiveData<List<ImageElement>> search(String keyword) {
        return imageElementDAO.search(keyword);
    }

    /**
     * Async inserts images to the database
     * @param imageElements the imageElement object to be inserted
     * @see InsertAsyncTask
     */
    public void insert(ImageElement... imageElements){
        new InsertAsyncTask(imageElementDAO).execute(imageElements);
    }

    private static class InsertAsyncTask extends AsyncTask<ImageElement, Void, Void> {
        private ImageElementDAO dao;
        private LiveData<ImageElement> imageElement;

        InsertAsyncTask(ImageElementDAO dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(final ImageElement... params) {
            for(ImageElement imageElement:params) {
                dao.insert(imageElement);
            }
            return null;
        }
    }

    /**
     * Async calls DAO to update
     * @param imageElements the imageElement objects to be updated
     * @see UpdateAsyncTask
     */
    public void update(ImageElement... imageElements){
        new UpdateAsyncTask(imageElementDAO).execute(imageElements);
    }

    private static class UpdateAsyncTask extends AsyncTask<ImageElement, Void, Void> {
        private ImageElementDAO dao;
        private LiveData<ImageElement> imageElement;

        UpdateAsyncTask(ImageElementDAO dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(final ImageElement... params) {
            for(ImageElement imageElement:params) {
                dao.update(imageElement);
            }
            return null;
        }
    }

    /**
     * Async calls DAO to delete an image
     * @param imageElement the imageElement object to be deleted
     * @see DeleteAsyncTask
     */
    public void delete(ImageElement imageElement) {
        new DeleteAsyncTask(imageElementDAO).execute(imageElement);
    }

    private static class DeleteAsyncTask extends AsyncTask<ImageElement, Void, Void> {
        private ImageElementDAO dao;
        private LiveData<ImageElement> imageElement;

        DeleteAsyncTask(ImageElementDAO dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(final ImageElement... params) {
            for(ImageElement imageElement:params) {
                dao.delete(imageElement);
            }
            return null;
        }
    }
}
