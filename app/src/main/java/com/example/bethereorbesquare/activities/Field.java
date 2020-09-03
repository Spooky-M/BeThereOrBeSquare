package com.example.bethereorbesquare.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.bethereorbesquare.DatabaseHelper;
import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.listeners.FieldListener;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Field extends Activity {

    private DatabaseHelper dbHelper;

    private View drawView;
    private Button switchButton;
    private int rows, columns;

    private Rectangle firstSelected, secondSelected;

    private List<Rectangle> field;
    private Random rand = new Random();

    private List<FieldListener> listeners = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        drawView = findViewById(R.id.draw_view);
        switchButton = findViewById(R.id.switch_button);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        Bundle dimensions = extras.getBundle("dimensions");
        assert dimensions != null;
        rows = dimensions.getInt("rows");
        columns = dimensions.getInt("columns");
        if (rows <= 0 || columns <= 0) throw new IllegalArgumentException();

        field = new ArrayList<>(rows * columns);
        for (int i = 0; i < rows * columns; i++)
            field.add(new Rectangle(0, 0, 0, 0,
                    Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))));

        drawView = new DrawView(this);
        drawView.invalidate();
        drawView.requestLayout();
        fieldChanged();
        drawView.setFocusableInTouchMode(true);
        drawView.setEnabled(true);
        drawView.setClickable(true);
//        setContentView(drawView);

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
                    v.invalidate();
                    v.requestLayout();
                    fieldChanged();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        pref = getPreferences(Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear();
//        editor.putInt("rows", rows);
//        editor.putInt("columns", columns);
//        int i = 0;
//        for(Rectangle r : field) {
//            editor.putInt("color" + i, r.getColor());
//            i++;
//        }
//        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        pref = getPreferences(Context.MODE_PRIVATE);
//
//        rows = pref.getInt("rows", 0);
//        columns = pref.getInt("columns", 0);
//        if(field == null) field = new ArrayList<>(rows*columns);
//
//        int color;
//        for(int i = 0; i < rows*columns; i++) {
//            color = pref.getInt("color" + i, 0);
//            field.add(new Rectangle(0, 0, 0, 0, color));
//        }
    }

    //    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("field", (ArrayList<? extends Parcelable>) field);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        field = savedInstanceState.getParcelableArrayList("field");
//        drawView.invalidate();
//        drawView.requestLayout();
//        fieldChanged();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() != KeyEvent.ACTION_DOWN) return true;

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Rectangle last = field.remove(field.size()-1);
                field.add(0, last);
                drawView.invalidate();
                drawView.requestLayout();
                fieldChanged();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Collections.shuffle(field);
                drawView.invalidate();
                drawView.requestLayout();
                fieldChanged();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class DrawView extends View {

        private Paint paint = new Paint();
        private int rectWidth = this.getWidth() / columns;
        private int rectHeight = this.getHeight() / rows;

        public DrawView(Context context) {
            super(context);
            init();
        }

        public DrawView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            paint.setStrokeWidth(1f);
            paint.setAntiAlias(false);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            rectWidth = getWidth() / columns;
            rectHeight = getHeight() / rows;

            int x, y;
            int i = 0, j = 0;
            for (Rectangle r : field) {
                x = i * rectWidth;
                y = j * rectHeight;
                r.setDimensions(x, y, x + rectWidth, y + rectHeight);

                paint.setColor(r.getColor());
                canvas.drawRect(x, y, x + rectWidth, y + rectHeight, paint);

                if(r.isSelected()) {
                    paint.setStrokeWidth(10f);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(0xFF005EFF);
                    canvas.drawRect(x, y, x + rectWidth, y + rectHeight, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(1f);
                }

                i++;
                if (i == columns) {
                    i = 0;
                    j++;
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            rectWidth = getWidth() / columns;
            rectHeight = getHeight() / rows;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                performClick();
                return true;
            }

            if(event.getAction() != MotionEvent.ACTION_DOWN) return false;

            float x, y;
            x = event.getX() * columns / getWidth();
            y = event.getY() * rows / getHeight();
            Rectangle cur = field.get((int)y * columns + (int)x);

            if(cur.isSelected()) {
                cur.setSelected(false);
                if(secondSelected != null && firstSelected != null) {
                    if(firstSelected.equals(cur)) {
                        firstSelected = secondSelected;
                    }
                    secondSelected = null;
                } else {
                    firstSelected = null;
                }
            } else {
                cur.setSelected(true);
                if(secondSelected != null && firstSelected != null) {
                    firstSelected.setSelected(false);
                    firstSelected = secondSelected;
                    secondSelected = cur;
                } else if(firstSelected != null) {
                    secondSelected = cur;
                } else {
                    firstSelected = cur;
                }
            }
            invalidate();
            requestLayout();
            fieldChanged();
            return true;
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }
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
