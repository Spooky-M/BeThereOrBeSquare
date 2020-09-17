package com.example.bethereorbesquare.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.bethereorbesquare.Util;
import com.example.bethereorbesquare.model.CustomColor;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BeThereOrBeSquare.db";
    public static final String RECTANGLES_TABLE_NAME = "FieldTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDEX = "list_index";
    public static final String COLUMN_SELECTED = "selected";

    public static final String COLORS_TABLE_NAME = "ColorsTable";
    public static final String COLUMN_HEX = "hex";
    public static final String COLUMN_COLOR_NAME = "color_name";
    public static final String COLUMN_RGB = "rgb";

    public static int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECTANGLES_TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INDEX + " INTEGER UNIQUE, " + COLUMN_COLOR_NAME + " TEXT UNIQUE, " + COLUMN_SELECTED + " INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + COLORS_TABLE_NAME +
                "(" + COLUMN_HEX + " TEXT, " + COLUMN_COLOR_NAME + " TEXT PRIMARY KEY, " + COLUMN_RGB + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        VERSION = newVersion;
    }

    public void insertRectangle(Rectangle r) {
        insertRectangle(r.getId(), r.getColor().getName(), r.isSelected());
    }

    public void insertRectangle(int index, String name, boolean selected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_COLOR_NAME, name);
        contentValues.put(COLUMN_SELECTED, selected);
        db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
    }

    public void updateRectangle(int id, int index, String colorName, boolean isSelected) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("UPDATE " + RECTANGLES_TABLE_NAME + " SET "
                + COLUMN_INDEX + "=" + index + ", " + COLUMN_COLOR_NAME + "='" + colorName + "', "
                + COLUMN_SELECTED + "=" + (isSelected ? 1 : 0) + ", " + " WHERE " + COLUMN_ID + "=" + id,
                null);
        res.close();
    }

    public void updateRectangle(Rectangle r) {
        updateRectangle(r.getId(), r.getIndex(), r.getColor().getName(), r.isSelected());
    }

    public Cursor getRectangleById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + RECTANGLES_TABLE_NAME +
                " WHERE " + COLUMN_ID + "=" + id, null);
    }

    public void insertAllRectangles(@NonNull List<Rectangle> rectangles) {
        for(Rectangle r : rectangles) {
            insertRectangle(r.getIndex(), r.getColor().getName(), r.isSelected());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Rectangle> getAllRectangles() {
        List<Rectangle> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + RECTANGLES_TABLE_NAME, null);
        res.moveToFirst();

        int idIndex = res.getColumnIndex(COLUMN_ID),
                indexIndex = res.getColumnIndex(COLUMN_INDEX),
                colorNameIndex = res.getColumnIndex(COLUMN_COLOR_NAME),
                selectedIndex = res.getColumnIndex(COLUMN_SELECTED);

        Rectangle r;
        String colorName;
        Cursor res2;
        CustomColor cur;
        boolean selected;
        while(!res.isAfterLast()){
            colorName = res.getString(colorNameIndex);
            res2 = db.rawQuery("SELECT * FROM " + COLORS_TABLE_NAME +
                    " WHERE " + COLUMN_COLOR_NAME + "='" + colorName + "'", null);
            res2.moveToFirst();
            cur = new CustomColor(res2.getString(res2.getColumnIndex(COLUMN_HEX)),
                    res2.getString(res2.getColumnIndex(COLUMN_COLOR_NAME)),
                    res2.getString(res2.getColumnIndex(COLUMN_RGB)));
            res2.close();

            selected = res.getInt(selectedIndex) != 0;
            r = new Rectangle(res.getInt(idIndex), res.getInt(indexIndex), cur, selected);
            list.add(r);
            res.moveToNext();
        }
        res.close();

        list.sort(new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return Integer.compare(o1.getIndex(), o2.getIndex());
            }
        });
        return list;
    }

    public void fillRectanglesDatabase(List<CustomColor> colors) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Rectangle> rectangles = Util.makeRectangles(colors);
        Cursor cursor = null;
        for(Rectangle r : rectangles) {
            cursor = db.rawQuery("INSERT INTO " + RECTANGLES_TABLE_NAME +
                    " (" + COLUMN_INDEX + ", " + COLUMN_COLOR_NAME + ", "  + COLUMN_SELECTED + ")"
                    + " VALUES (" + r.getIndex() + ", '"  + r.getColor().getName() + "', "
                    + (r.isSelected() ? 1 : 0) + ")" ,
                    null);
        }
        if(cursor != null) cursor.close();
    }

    public void initNewRectanglesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RECTANGLES_TABLE_NAME);
        onCreate(db);
    }

    public void insertAllColors(List<CustomColor> colors) {
        for(CustomColor c : colors) insertColor(c);
    }

    public void insertColor(CustomColor c) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("INSERT INTO " + COLORS_TABLE_NAME +
                        " (" + COLUMN_HEX + ", " + COLUMN_COLOR_NAME + ", "  + COLUMN_RGB + ")"
                        + " VALUES ('" + c.getHex() + "', '"  + c.getName() + "', "
                        + c.getRgbInt() + ")" ,
                null);
        cursor.close();
    }

    public Cursor getColorByName(String name) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + COLORS_TABLE_NAME +
                " WHERE " + COLUMN_COLOR_NAME + "='" + name + "'", null);
    }

    public List<CustomColor> getAllColors() {
        List<CustomColor> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + COLORS_TABLE_NAME, null);
        res.moveToFirst();

        int hex = res.getColumnIndex(COLUMN_HEX), name = res.getColumnIndex(COLUMN_COLOR_NAME),
                rgb = res.getColumnIndex(COLUMN_RGB);

        CustomColor c;
        while(!res.isAfterLast()){
            c = new CustomColor(res.getString(hex), res.getString(name), res.getString(rgb));
            list.add(c);
            res.moveToNext();
        }
        res.close();

        return list;
    }

    public void initNewColorsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + COLORS_TABLE_NAME);
        onCreate(db);
    }
}
