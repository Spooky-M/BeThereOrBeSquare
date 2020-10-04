package com.example.bethereorbesquare.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bethereorbesquare.entity.Rectangle;

import java.util.List;

@Dao
public interface RectangleDao {

    @Query("SELECT * FROM Rectangles ORDER BY `index` ASC")
    LiveData<List<Rectangle>> getAllRectangles();

    @Query("SELECT * FROM Rectangles WHERE id=:id")
    Rectangle getRectangle(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllRectangles(List<Rectangle> rectangles);

    @Update
    void updateRectangle(Rectangle r);

    @Query("DELETE FROM Rectangles")
    void deleteAllRectangles();
}
