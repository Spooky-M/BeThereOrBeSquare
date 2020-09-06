package com.example.bethereorbesquare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BeThereOrBeSquare.db";
    public static final String RECTANGLES_TABLE_NAME = "FieldTable";
    public static final String COLUMN_ID = "id";
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
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LEFT + " INTEGER, " + COLUMN_TOP + " INTEGER, " + COLUMN_RIGHT + " INTEGER, "
                + COLUMN_BOTTOM + " INTEGER, " + COLUMN_COLOR + " LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RECTANGLES_TABLE_NAME);
        onCreate(db);
        VERSION = newVersion;
    }

    public void insertRectangle(int left, int top, int right, int bottom, long color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
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
        contentValues.put(COLUMN_LEFT, r.getLeft());
        contentValues.put(COLUMN_TOP, r.getTop());
        contentValues.put(COLUMN_RIGHT, r.getRight());
        contentValues.put(COLUMN_BOTTOM, r.getBottom());
        contentValues.put(COLUMN_COLOR, r.getColor());
        db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
    }

    public void updateRectangle(int id, int left, int top, int right, int bottom, long color) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery( "UPDATE " + RECTANGLES_TABLE_NAME +
                " SET " + COLUMN_LEFT + "=" + left + ", " + COLUMN_TOP + "=" + top + ", " +
                COLUMN_RIGHT + "=" + right + ", " + COLUMN_BOTTOM + "=" + bottom + ", " +
                COLUMN_COLOR + "=" + color, null);
        res.close();
    }

    public void updateRectangle(Rectangle r) {
    }

    public Cursor getRectangle(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + RECTANGLES_TABLE_NAME +
                " WHERE " + COLUMN_ID + "=" + id, null);
    }

    public void insertAllRectangles(@NonNull List<Rectangle> rectangles) {
        for(Rectangle r : rectangles) {
            insertRectangle(r.getLeft(), r.getTop(), r.getRight(), r.getBottom(), r.getColor());
        }
    }

    public List<Rectangle> getAllRectangles() {
        List<Rectangle> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + RECTANGLES_TABLE_NAME, null);
        res.moveToFirst();

        Rectangle r;
        while(!res.isAfterLast()){
            r = new Rectangle(res.getColumnIndex(COLUMN_LEFT), res.getColumnIndex(COLUMN_TOP),
                    res.getColumnIndex(COLUMN_RIGHT), res.getColumnIndex(COLUMN_BOTTOM),
                    res.getColumnIndex(COLUMN_COLOR));
            r.setId(res.getColumnIndex(COLUMN_ID));
            list.add(r);
            res.moveToNext();
        }
        res.close();
        return list;
    }

    public void initRectanglesDatabase(int n) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Rectangle> rectangles = Util.makeRectangles(n);
        for(Rectangle r : rectangles) {
            ContentValues contentValues = new ContentValues();
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
