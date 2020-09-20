package com.example.bethereorbesquare;

import com.example.bethereorbesquare.model.CustomColor;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class
 */
public class Util {

    /**
     * Creates a new list of {@link Rectangle} objects, with default {@code id},
     * {@code index} corresponding to rectangle's position in a newly created list
     * and a randomly chosen {@link CustomColor} from the {@code colors} list.
     * {@link Random#nextInt(int)} is used to randomly choose a position in {@code colors} list,
     * and a color on that position is used to construct a new rectangle.
     * @param n number of rectangles to create
     * @param colors list of available colors
     * @return
     */
    public static List<Rectangle> makeRectangles(int n, List<CustomColor> colors) {
        Random rand = new Random();
        List<Rectangle> rectangles = new ArrayList<>();

        int size = colors.size();
        for(int i = 0; i < n; i++) {
            rectangles.add(new Rectangle(i, colors.get(rand.nextInt(size))));
        }
        return rectangles;
    }
}
