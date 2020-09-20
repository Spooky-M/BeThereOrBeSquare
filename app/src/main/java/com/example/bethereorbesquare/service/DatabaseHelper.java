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

    /**
     * Basic DatabaseHelper constructor which accepts only a {@link Context} argument.
     * @param context current context state of app
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /**
     * Initiates new rectangles and colors table if either or both of them don't exist.
     * @param db a writeable {@link SQLiteDatabase} database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECTANGLES_TABLE_NAME +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INDEX + " INTEGER, " + COLUMN_COLOR_NAME + " TEXT, " + COLUMN_SELECTED + " INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + COLORS_TABLE_NAME +
                "(" + COLUMN_HEX + " TEXT, " + COLUMN_COLOR_NAME + " TEXT PRIMARY KEY, " + COLUMN_RGB + " INTEGER)");
    }

    /**
     * On upgrade, {@link DatabaseHelper#onCreate(SQLiteDatabase)} is called and a new version number stored
     * @param db a writeable {@link SQLiteDatabase} database
     * @param oldVersion old version number
     * @param newVersion new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        VERSION = newVersion;
    }

    /**
     * Inserts a rectangle without an id into database's {@link DatabaseHelper#RECTANGLES_TABLE_NAME} table.
     * Should be called only when a new {@link Rectangle} object is initiated without an id.
     * @param index {@link Rectangle#getIndex()}
     * @param colorName {@link CustomColor#getName()}
     * @param selected {@link Rectangle#isSelected()}
     */
    public void insertRectangle(int index, String colorName, boolean selected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_COLOR_NAME, colorName);
        contentValues.put(COLUMN_SELECTED, selected);
        db.insert(RECTANGLES_TABLE_NAME, null, contentValues);
    }

    /**
     * Inserts a rectangle without an id into the database.
     * Delegates work to {@link DatabaseHelper#insertRectangle(int, String, boolean)} )}
     * @param r rectangle to be stored
     */
    public void insertRectangle(Rectangle r) {
        insertRectangle(r.getIndex(), r.getColor().getName(), r.isSelected());
    }

    /**
     * Updates rectangle with {@code id} in the database.
     * @param id {@link Rectangle#getId()}
     * @param index {@link Rectangle#getIndex()}
     * @param colorName {@link CustomColor#getName()}
     * @param selected {@link Rectangle#isSelected()}
     */
    public void updateRectangle(long id, int index, String colorName, boolean selected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_COLOR_NAME, colorName);
        contentValues.put(COLUMN_SELECTED, selected);
        db.update(RECTANGLES_TABLE_NAME, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Updates rectangle with {@code id} in the database.
     * Delegates work to {@link DatabaseHelper#updateRectangle(long, int, String, boolean)}
     * @param r {@link Rectangle} to be updated
     */
    public void updateRectangle(Rectangle r) {
        updateRectangle(r.getId(), r.getIndex(), r.getColor().getName(), r.isSelected());
    }

    /**
     * Fetches rectangle with {@code id} from the database.
     * @param id {@link Rectangle#getId()}
     * @return {@link Cursor} object with query result
     */
    public Cursor getRectangleById(int id) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + RECTANGLES_TABLE_NAME +
                " WHERE " + COLUMN_ID + "=" + id, null);
    }

    /**
     * Inserts all the rectangles from list into database
     * @param rectangles list of {@link Rectangle} objects to be stored
     */
    public void insertAllRectangles(@NonNull List<Rectangle> rectangles) {
        for(Rectangle r : rectangles) {
            insertRectangle(r.getIndex(), r.getColor().getName(), r.isSelected());
        }
    }

    /**
     * Fetches all rectangles stored in database's {@link DatabaseHelper#RECTANGLES_TABLE_NAME} table.
     * Uses a query "SELECT * FROM RECTANGLES_TABLE_NAME" and iterates through its response,
     * creating new {@link Rectangle} objects with their corresponding new {@link CustomColor} object.
     * @return list of all stored rectangles
     */
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

        list.sort((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
        return list;
    }

    /**
     * Fills database with new {@link Rectangle} objects, using {@link Util#makeRectangles(int, List)}
     * method to create rectangles without an id, and a pseudo-random {@link CustomColor} element from the list.
     * @param n number of rectangles to create and store into database.
     * @param colors list of colors from which some one will be randomly selected and used to construct
     *               a new rectangle
     */
    public void fillRectanglesTable(int n, List<CustomColor> colors) {
        List<Rectangle> rectangles = Util.makeRectangles(n, colors);
        for(Rectangle r : rectangles) {
            insertRectangle(r);
        }
    }

    /**
     * Drops {@link DatabaseHelper#RECTANGLES_TABLE_NAME} if exists, and creates a new table of the same name.
     */
    public void initNewRectanglesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RECTANGLES_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Drop {@link DatabaseHelper#COLORS_TABLE_NAME} if exists, and create a new table of the same name.
     */
    public void insertAllColors(List<CustomColor> colors) {
        for(CustomColor c : colors) insertColor(c);
    }

    /**
     * Inserts a color into database's {@link DatabaseHelper#COLORS_TABLE_NAME} table.
     * @param c {@link CustomColor} to be inserted
     */
    public void insertColor(CustomColor c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_HEX, c.getHex());
        contentValues.put(COLUMN_COLOR_NAME, c.getName());
        contentValues.put(COLUMN_RGB, c.getRgbInt());
        db.insert(COLORS_TABLE_NAME, null, contentValues);
    }

    /**
     * Fetches color with {@code id} from the database.
     * @param name {@link CustomColor#getName()}
     * @return {@link Cursor} object with query result
     */
    public Cursor getColorByName(String name) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + COLORS_TABLE_NAME +
                " WHERE " + COLUMN_COLOR_NAME + "='" + name + "'", null);
    }

    /**
     * Fetches all colors stored in database's {@link DatabaseHelper#COLORS_TABLE_NAME} table.
     * Uses a query "SELECT * FROM COLORS_TABLE_NAME" and iterates through its response,
     * creating new {@link CustomColor} objects.
     * @return list of all stored colors
     */
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

    /**
     * Drops {@link DatabaseHelper#COLORS_TABLE_NAME} if exists, and creates a new table of the same name.
     */
    public void initNewColorsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + COLORS_TABLE_NAME);
        onCreate(db);
    }
}
