package com.example.bethereorbesquare.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.bethereorbesquare.DatabaseHelper;
import com.example.bethereorbesquare.listeners.FieldListener;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field extends Activity {

    private DatabaseHelper dbHelper;

    private View drawView;
    private int rows, columns;

    private Rectangle firstSelected, secondSelected;

    private List<Rectangle> field;
    private List<FieldListener> listeners = new ArrayList<>();

    private boolean continued = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            dbHelper.initRectanglesDatabase(rows*columns);
        }

        drawView = new DrawView(this);
        drawView.setFocusableInTouchMode(true);
        drawView.setEnabled(true);
        drawView.setClickable(true);
        drawView.setVisibility(View.VISIBLE);
        setContentView(drawView);
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
//        drawView.invalidate();
//        drawView.requestLayout();
//        fieldChanged();
    }

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

//        private Button switchButton;

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

//            switchButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(secondSelected != null) {
//                        int index1 = field.indexOf(firstSelected), index2 = field.indexOf(secondSelected);
//                        field.set(index1, secondSelected);
//                        field.set(index2, firstSelected);
//                        firstSelected.setSelected(false);
//                        secondSelected.setSelected(false);
//                        firstSelected = secondSelected = null;
//                        v.invalidate();
//                        v.requestLayout();
//                        fieldChanged();
//                    }
//                }
//            });
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(continued) {
                for(Rectangle r : field) {
                    r.draw(canvas, paint);
                }
                continued = false;
                return;
            }

            int rectWidth = getWidth() / columns;
            int rectHeight = getHeight() / rows;

            int x, y;
            int i = 0, j = 0;
            for (Rectangle r : field) {
                x = i * rectWidth;
                y = j * rectHeight;
                r.setDimensions(x, y, x + rectWidth, y + rectHeight);
                r.setIndex(j*columns + i);

                paint.setColor(r.getColor());
                r.draw(canvas, paint);

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
