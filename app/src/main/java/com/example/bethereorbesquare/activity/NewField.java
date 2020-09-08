package com.example.bethereorbesquare.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.bethereorbesquare.R;
import com.google.android.material.textfield.TextInputEditText;

public class NewField extends Activity {

    private TextView rowsView, columnsView;
    private TextInputEditText inputFieldRows, inputFieldColumns;
    private Button next;
    private View popupView;
    private PopupWindow popupWindow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfield);

        rowsView = findViewById(R.id.textViewRows);
        columnsView = findViewById(R.id.textViewColumns);
        inputFieldRows = findViewById(R.id.editTextRows);
        inputFieldColumns = findViewById(R.id.editTextColumns);
        next = findViewById(R.id.nextButton);

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                int rows, columns;
                try {
                    rows = Integer.parseInt(String.valueOf(inputFieldRows.getText()));
                    columns = Integer.parseInt(String.valueOf(inputFieldColumns.getText()));

                    if(rows <= 0 || columns <= 0) throw new IllegalArgumentException();

                    Bundle dimensions = new Bundle();
                    dimensions.putInt("rows", rows);
                    dimensions.putInt("columns", columns);

                    Intent intent = new Intent(NewField.this, Field.class);
                    intent.putExtra("dimensions", dimensions);
                    startActivity(intent);
                } catch (IllegalArgumentException e) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    popupView = inflater.inflate(R.layout.popup_window_help, null);
                    popupView.setBackgroundColor(Color.CYAN);

                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    popupWindow = new PopupWindow(popupView, width, height, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                }
            }
        });


    }
}
