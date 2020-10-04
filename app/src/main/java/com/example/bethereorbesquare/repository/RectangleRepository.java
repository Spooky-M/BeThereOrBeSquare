package com.example.bethereorbesquare.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.bethereorbesquare.dao.RectangleDao;
import com.example.bethereorbesquare.entity.Rectangle;
import com.example.bethereorbesquare.service.AppRoomDatabase;

import java.util.List;

public class RectangleRepository {

    private RectangleDao rectangleDao;
    private LiveData<List<Rectangle>> rectangles;

    public RectangleRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getInstance(application);
        rectangleDao = db.rectangleDao();
        rectangles = rectangleDao.getAllRectangles();
    }

    public LiveData<List<Rectangle>> getAllRectangles() {
        return rectangles;
    }

    public Rectangle getRectangle(long id) {
        return rectangleDao.getRectangle(id);
    }

    public void insertAllRectangles(List<Rectangle> rectangles) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> rectangleDao.insertAllRectangles(rectangles));
    }

    public void updateRectangle(Rectangle rectangle) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> rectangleDao.updateRectangle(rectangle));
    }

    public void deleteAllRectangles() {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> rectangleDao.deleteAllRectangles());
    }
}
