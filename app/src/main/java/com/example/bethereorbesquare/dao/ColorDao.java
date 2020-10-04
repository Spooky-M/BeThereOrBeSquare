package com.example.bethereorbesquare.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bethereorbesquare.entity.CustomColor;

import java.util.List;

@Dao
public interface ColorDao {

    @Query("SELECT * FROM Colors")
    LiveData<List<CustomColor>> getAllColors();

    @Query("SELECT * FROM Colors WHERE name=:name")
    CustomColor getColor(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertColor(CustomColor c);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllColors(List<CustomColor> colors);

    @Query("DELETE FROM Colors")
    void deleteAllColors();

}
