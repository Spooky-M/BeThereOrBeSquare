package com.example.bethereorbesquare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.adapter.FieldAdapter;
import com.example.bethereorbesquare.listeners.FieldListener;
import com.example.bethereorbesquare.service.DatabaseHelper;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field extends AppCompatActivity implements FieldAdapter.RectangleClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private FieldAdapter fieldAdapter;
    private Button switchButton;
    private int rows, columns;
    SharedPreferences preferences;

    private Rectangle firstSelected, secondSelected;

    private List<Rectangle> field;
    private List<FieldListener> listeners = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_field);
        recyclerView = findViewById(R.id.recycler_view);
        switchButton = findViewById(R.id.switch_button);

        dbHelper = new DatabaseHelper(this);

        preferences = getSharedPreferences(String.valueOf(getText(R.string.continue_preferences)), Context.MODE_PRIVATE);
        boolean cont = preferences.getBoolean(String.valueOf(getText(R.string.continue_key)), false);

        if(!cont) {
            Bundle extras = getIntent().getExtras();
            assert extras != null;
            Bundle dimensions = extras.getBundle("dimensions");
            assert dimensions != null;
            rows = dimensions.getInt("rows");
            columns = dimensions.getInt("columns");
            if (rows <= 0 || columns <= 0) throw new IllegalArgumentException();
            dbHelper.fillRectanglesTable(rows * columns, dbHelper.getAllColors());
        }
        field = dbHelper.getAllRectangles();

        // TODO 6) Napravi prikaz grida koristeći RecyclerView (mora imat GridLayoutManager da bude grid)
        // Primjer -> https://medium.com/@droidbyme/android-recyclerview-fca74609725e
        // Dokumentacija -> https://developer.android.com/guide/topics/ui/layout/recyclerview
        // Nemoj zaboravit dodat i redni broj u prikazu kvadratića


        fieldAdapter = new FieldAdapter(Field.this, field);
        fieldAdapter.setClickListener(Field.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fieldAdapter);

        switchButton.setOnClickListener(v -> {
            if(secondSelected != null) {
                int index1 = field.indexOf(firstSelected), index2 = field.indexOf(secondSelected);
                field.set(index1, secondSelected);
                field.set(index2, firstSelected);
                firstSelected.setSelected(false);
                secondSelected.setSelected(false);
                firstSelected = secondSelected = null;
                processFieldChange();
            }
        });
    }

    @Override
    public void onRectangleClick(View v, int position) {
        Rectangle cur = field.get(position);
        cur.setSelected(!cur.isSelected());
        if(secondSelected == null) {
            if(firstSelected == null) firstSelected = cur;
            else secondSelected = cur;
        } else {
            firstSelected.setSelected(false);
            firstSelected = secondSelected;
            secondSelected = cur;
        }
        processFieldChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(Rectangle r : field) {
            dbHelper.updateRectangle(r);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(String.valueOf(getText(R.string.continue_key)), true);
            editor.apply();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() != KeyEvent.ACTION_DOWN) return true;

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Rectangle last = field.remove(field.size()-1);
                field.add(0, last);
                processFieldChange();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Collections.shuffle(field);
                processFieldChange();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void processFieldChange() {
        fieldAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
        recyclerView.requestLayout();
        fieldChanged();
    }

    protected void fieldChanged() {
        for(FieldListener fl : listeners) {
            fl.onFieldChange();
        }
    }

    public void attachFieldListener(FieldListener fl) {
        if(listeners.contains(fl)) return;
        listeners.add(fl);
    }

    public void detachFieldListener(FieldListener fl) {
        listeners.remove(fl);
    }
}
