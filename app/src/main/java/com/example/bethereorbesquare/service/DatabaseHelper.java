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
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BeThereOrBeSquare.db";
    public static final String RECTANGLES_TABLE_NAME = "FieldTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDEX = "list_index";
    public static final String COLUMN_LEFT = "left_coord";
    public static final String COLUMN_TOP = "top_coord";
    public static final String COLUMN_RIGHT = "right_coord";
    public static final String COLUMN_BOTTOM = "bottom_coord";
    public static final String COLUMN_COLOR = "color";

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
                + COLUMN_INDEX + " INTEGER UNIQUE, " + COLUMN_LEFT + " INTEGER, "
                + COLUMN_TOP + " INTEGER, " + COLUMN_RIGHT + " INTEGER, "
                + COLUMN_BOTTOM + " INTEGER, " + COLUMN_COLOR + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RECTANGLES_TABLE_NAME);
        onCreate(db);
        VERSION = newVersion;
    }

    public void insertRectangle(int id, int index, int left, int top, int right, int bottom, long color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_LEFT, left);
        contentValues.put(COLUMN_TOP, top);
        contentValues.put(COLUMN_RIGHT, right);
        contentValues.put(COLUMN_BOTTOM, bottom);
        contentValues.put(COLUMN_COLOR, color);
        db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
    }

    public void insertRectangle(Rectangle r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, r.getIndex());
        contentValues.put(COLUMN_LEFT, r.getLeft());
        contentValues.put(COLUMN_TOP, r.getTop());
        contentValues.put(COLUMN_RIGHT, r.getRight());
        contentValues.put(COLUMN_BOTTOM, r.getBottom());
        contentValues.put(COLUMN_COLOR, r.getColor());
        db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
    }

    public void updateRectangle(int id, int index, int left, int top, int right, int bottom, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery( "UPDATE " + RECTANGLES_TABLE_NAME +
                " SET " + COLUMN_INDEX + "=" + index + ", " + COLUMN_LEFT + "=" + left + ", " + COLUMN_TOP + "=" + top + ", " +
                COLUMN_RIGHT + "=" + right + ", " + COLUMN_BOTTOM + "=" + bottom + ", " +
                COLUMN_COLOR + "=" + color + " WHERE " + COLUMN_ID + "=" + id, null);
        res.close();
    }

    public void updateRectangle(Rectangle r) {
        updateRectangle(r.getId(), r.getIndex(), r.getLeft(), r.getTop(), r.getRight(), r.getBottom(), r.getColor());
    }

    public Cursor getRectangle(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + RECTANGLES_TABLE_NAME +
                " WHERE " + COLUMN_ID + "=" + id, null);
    }

    public void insertAllRectangles(@NonNull List<Rectangle> rectangles) {
        for(Rectangle r : rectangles) {
            insertRectangle(r.getId(), r.getIndex(), r.getLeft(), r.getTop(), r.getRight(), r.getBottom(), r.getColor());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Rectangle> getAllRectangles() {
        List<Rectangle> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + RECTANGLES_TABLE_NAME, null);
        res.moveToFirst();

        int left_index = res.getColumnIndex(COLUMN_LEFT),
                top_index = res.getColumnIndex(COLUMN_TOP),
                right_index = res.getColumnIndex(COLUMN_RIGHT),
                bottom_index = res.getColumnIndex(COLUMN_BOTTOM),
                color_index = res.getColumnIndex(COLUMN_COLOR),
                index_index = res.getColumnIndex(COLUMN_INDEX),
                id_index = res.getColumnIndex(COLUMN_ID);

        Rectangle r;
        while(!res.isAfterLast()){
            r = new Rectangle(res.getInt(id_index), res.getInt(index_index),
                    res.getInt(left_index), res.getInt(top_index), res.getInt(right_index),
                    res.getInt(bottom_index), res.getInt(color_index));
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

    public void initRectanglesDatabase(int n) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Rectangle> rectangles = Util.makeRectangles(n);
        for(Rectangle r : rectangles) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_INDEX, r.getIndex());
            contentValues.put(COLUMN_LEFT, r.getLeft());
            contentValues.put(COLUMN_TOP, r.getTop());
            contentValues.put(COLUMN_RIGHT, r.getRight());
            contentValues.put(COLUMN_BOTTOM, r.getBottom());
            contentValues.put(COLUMN_COLOR, r.getColor());
            db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
        }
    }

    public void initNewTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RECTANGLES_TABLE_NAME);
        onCreate(db);
    }

}
