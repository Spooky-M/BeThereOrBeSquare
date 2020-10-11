package com.example.bethereorbesquare.view;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bethereorbesquare.entity.CustomColor;
import com.example.bethereorbesquare.entity.Rectangle;
import com.example.bethereorbesquare.repository.ColorRepository;
import com.example.bethereorbesquare.repository.RectangleRepository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private ColorRepository colorRepository;
    private RectangleRepository rectangleRepository;
    private LiveData<List<Rectangle>> rectangles;
    private LiveData<List<CustomColor>> colors;

    public AppViewModel(@NonNull Application application) {
        super(application);
        colorRepository = new ColorRepository(application);
        rectangleRepository = new RectangleRepository(application);
        rectangles = rectangleRepository.getAllRectangles();
        colors = colorRepository.getAllColors();
    }

    public LiveData<List<Rectangle>> getAllRectangles() {
        return rectangles;
    }

    public Rectangle getRectangle(long id) {
        return rectangleRepository.getRectangle(id);
    }

    public void insertAllRectangles(List<Rectangle> rectangles) {
        rectangleRepository.insertAllRectangles(rectangles);
    }

    public void updateRectangle(Rectangle r) {
        rectangleRepository.updateRectangle(r);
    }

    public void deleteAllRectangles() {
        rectangleRepository.deleteAllRectangles();
    }

    public CustomColor getColor(String colorName) {
        return colorRepository.getColor(colorName);
    }

    public LiveData<List<CustomColor>> getAllColors() {
        return colors;
    }

    public void insertColor(CustomColor c) {
        colorRepository.insertColor(c);
    }

    public void insertAllColors(List<CustomColor> colors) {
        colorRepository.insertAllColors(colors);
    }

    public void deleteAllColors() {
        colorRepository.deleteAllColors();
    }
}
