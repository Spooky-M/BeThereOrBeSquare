package com.example.bethereorbesquare;

import android.graphics.Color;

import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    public static List<Rectangle> makeRectangles(int n) {
        Random rand = new Random();
        List<Rectangle> rectangles = new ArrayList<>(n);

        for(int i = 0; i < n; i++) {
            rectangles.add(new Rectangle(i, 0, 0, 0, 0,
                    Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))));
        }
        return rectangles;
    }
}
