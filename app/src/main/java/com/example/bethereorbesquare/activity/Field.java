package com.example.bethereorbesquare.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

public class Field extends Activity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private FieldAdapter fieldAdapter;
    private Button switchButton;
    private int rows, columns;

    private Rectangle firstSelected, secondSelected;

    private List<Rectangle> field;
    private List<FieldListener> listeners = new ArrayList<>();

    private boolean continued = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfield);
        recyclerView = findViewById(R.id.recycler_view);
        switchButton = findViewById(R.id.switch_button);

        dbHelper = new DatabaseHelper(this);
        field = dbHelper.getAllRectangles();
        if(field != null && !field.isEmpty()) {
            continued = true;
        } else {
            Bundle extras = getIntent().getExtras();
            assert extras != null;
            Bundle dimensions = extras.getBundle("dimensions");
            assert dimensions != null;
            rows = dimensions.getInt("rows");
            columns = dimensions.getInt("columns");
            if (rows <= 0 || columns <= 0) throw new IllegalArgumentException();
            dbHelper.initRectanglesDatabase(dbHelper.getAllColors());
        }

        //TODO 6) Napravi prikaz grida koristeći RecyclerView (mora imat GridLayoutManager da bude grid)
        // Primjer -> https://medium.com/@droidbyme/android-recyclerview-fca74609725e
        // Dokumentacija -> https://developer.android.com/guide/topics/ui/layout/recyclerview
        // Nemoj zaboravit dodat i redni broj u prikazu kvadratića

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        fieldAdapter = new FieldAdapter(this, field);
        recyclerView.setAdapter(fieldAdapter);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secondSelected != null) {
                    int index1 = field.indexOf(firstSelected), index2 = field.indexOf(secondSelected);
                    field.set(index1, secondSelected);
                    field.set(index2, firstSelected);
                    firstSelected.setSelected(false);
                    secondSelected.setSelected(false);
                    firstSelected = secondSelected = null;
                    recyclerView.invalidate();
                    recyclerView.requestLayout();
                    fieldChanged();
                }
            }
        });
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() != KeyEvent.ACTION_DOWN) return true;

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Rectangle last = field.remove(field.size()-1);
                field.add(0, last);
                recyclerView.invalidate();
                recyclerView.requestLayout();
                fieldChanged();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Collections.shuffle(field);
                recyclerView.invalidate();
                recyclerView.requestLayout();
                fieldChanged();
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public class FieldViewHolder extends RecyclerView.ViewHolder {

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
        }


    }
}
