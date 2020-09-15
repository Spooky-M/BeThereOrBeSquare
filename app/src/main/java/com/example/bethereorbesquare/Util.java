package com.example.bethereorbesquare;

import com.example.bethereorbesquare.model.CustomColor;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<Rectangle> makeRectangles(List<CustomColor> colors) {
        List<Rectangle> rectangles = new ArrayList<>();

        for(int i = 0; i < colors.size(); i++) {
            rectangles.add(new Rectangle(i, colors.get(i)));
        }
        return rectangles;
    }
}
