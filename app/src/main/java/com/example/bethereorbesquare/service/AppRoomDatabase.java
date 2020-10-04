package com.example.bethereorbesquare.service;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bethereorbesquare.dao.ColorDao;
import com.example.bethereorbesquare.dao.RectangleDao;
import com.example.bethereorbesquare.entity.CustomColor;
import com.example.bethereorbesquare.entity.Rectangle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CustomColor.class, Rectangle.class}, version = 2, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {

    private static final String DB_NAME = "BeThereOrBeSquare_database";
    private static volatile AppRoomDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppRoomDatabase getInstance(final Context context) {
        if(instance == null) {
            synchronized(AppRoomDatabase.class) {
                if(instance == null)
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
            }
        }
        return instance;
    }

    public abstract ColorDao colorDao();

    public abstract RectangleDao rectangleDao();
}
