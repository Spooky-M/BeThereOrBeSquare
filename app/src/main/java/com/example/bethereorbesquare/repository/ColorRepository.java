package com.example.bethereorbesquare.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.bethereorbesquare.dao.ColorDao;
import com.example.bethereorbesquare.entity.CustomColor;
import com.example.bethereorbesquare.service.AppRoomDatabase;

import java.util.List;

public class ColorRepository {

    private ColorDao colorDao;
    private LiveData<List<CustomColor>> colors;

    public ColorRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getInstance(application);
        colorDao = db.colorDao();
        colors = colorDao.getAllColors();
    }

    public LiveData<List<CustomColor>> getAllColors() {
        return colors;
    }

    public @Nullable CustomColor getColor(String name) {
        List<CustomColor> colorList = colors.getValue();
        if(colors == null || colorList == null || colorList.isEmpty()) return null;
        for(CustomColor c : colorList) {
            if(c.getName().equals(name)) return c;
        }
        return null;
    }

    public void insertColor(CustomColor color) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> colorDao.insertColor(color));
    }

    public void insertAllColors(List<CustomColor> colorsList) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            colorDao.insertAllColors(colorsList);
            colors = getAllColors();
        });
    }

    public void deleteAllColors() {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> colorDao.deleteAllColors());

    }
}
