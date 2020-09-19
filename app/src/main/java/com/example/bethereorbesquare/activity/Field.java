package com.example.bethereorbesquare.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.adapter.ActionModeController;
import com.example.bethereorbesquare.adapter.FieldAdapter;
import com.example.bethereorbesquare.adapter.FieldItemDetailsLookup;
import com.example.bethereorbesquare.adapter.FieldItemKeyProvider;
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
    private ActionModeController controller;
    private Button switchButton;
    private int rows, columns;

    private Rectangle firstSelected, secondSelected;

    private List<Rectangle> field;
    private List<FieldListener> listeners = new ArrayList<>();

    public static final String SELECTION_ID = "selection_id";
    private static final int MAX_SELECTIONS = 2;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState != null) {
//            fieldAdapter.getSelectionTracker().onRestoreInstanceState(savedInstanceState);
//        }

        setContentView(R.layout.activity_field);
        recyclerView = findViewById(R.id.recycler_view);
        switchButton = findViewById(R.id.switch_button);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences(MainActivity.CONTINUE_PREFERENCES, Context.MODE_PRIVATE);
        boolean cont = preferences.getBoolean(MainActivity.CONTINUE_KEY, false);

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

        fieldAdapter = new FieldAdapter(this, field);
        fieldAdapter.setClickListener(this);
        recyclerView.setAdapter(fieldAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerView.setHasFixedSize(true);

//        SelectionTracker<Long> selectionTracker = new SelectionTracker.Builder<>(
//                SELECTION_ID, recyclerView,
//                new FieldItemKeyProvider(ItemKeyProvider.SCOPE_CACHED, field),
//                new FieldItemDetailsLookup(recyclerView), StorageStrategy.createLongStorage())
//                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
//                .build();
//        fieldAdapter.setSelectionTracker(selectionTracker);
//
//        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
//            @Override
//            public void onSelectionChanged() {
//                super.onSelectionChanged();
//
//
//                Selection<Long> selection = selectionTracker.getSelection();
//                if(selection.size() > MAX_SELECTIONS + 1) throw new IllegalArgumentException();
//
//                //todo
//
//                if(selection.size() == MAX_SELECTIONS + 1) {
//                    firstSelected.setSelected(false);
//                    firstSelected = secondSelected;
//                }
//            }
//        });

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
        processFieldChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(Rectangle r : field) {
            dbHelper.updateRectangle(r);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        field = dbHelper.getAllRectangles();
        processFieldChange();
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

    protected void fieldChanged() {
        for(FieldListener fl : listeners) {
            fl.onFieldChange();
        }
    }

    private void processFieldChange() {
        fieldAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
        recyclerView.requestLayout();
        fieldChanged();
    }

    public void attachFieldListener(FieldListener fl) {
        if(listeners.contains(fl)) return;
        listeners.add(fl);
    }

    public void detachFieldListener(FieldListener fl) {
        listeners.remove(fl);
    }
}
