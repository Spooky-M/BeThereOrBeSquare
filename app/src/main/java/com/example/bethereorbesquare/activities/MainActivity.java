package com.example.bethereorbesquare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.example.bethereorbesquare.DatabaseHelper;
import com.example.bethereorbesquare.R;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private TextView title;
    private Button startButton, continueButton;

    private View popupView;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        startButton = findViewById(R.id.start_button);
        continueButton = findViewById(R.id.continue_button);

        dbHelper = new DatabaseHelper(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.initNewTable();
                startActivity(new Intent(MainActivity.this, NewField.class));
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                if(dbHelper.getAllRectangles().isEmpty()) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    popupView = inflater.inflate(R.layout.popup_window_error, null);
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