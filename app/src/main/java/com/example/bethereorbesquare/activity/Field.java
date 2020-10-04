package com.example.bethereorbesquare.activity;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.adapter.FieldAdapter;
import com.example.bethereorbesquare.entity.Rectangle;
import com.example.bethereorbesquare.view.AppViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An activity which handles the creation and display of rectangles field. Uses {@link RecyclerView},
 * a custom adapter {@link FieldAdapter} and contains a "Switch" button which switches the positions
 * of two selected rectangles. On "volume up" button the field shifts by 1 to right, on "volume down"
 * the rectangles are shuffled. Handles rectangles selection (up to 2 can be selected at the same time)
 * and updates database on exit. {@link FieldListener} listeners can be attached to follow
 * the field's state.
 */
public class Field extends AppCompatActivity implements FieldAdapter.RectangleClickListener {

    /**
     * App's view model which contains methods for communication with {@code Room} database
     */
    private AppViewModel viewModel;

    /**
     * A {@link RecyclerView} element for field display
     */
    private RecyclerView recyclerView;

    /**
     * An adapter for {@link Field#recyclerView}
     */
    private FieldAdapter fieldAdapter;

    /**
     * Button for switching 2 selected rectangles
     */
    private Button switchButton;

    /**
     * Field dimensions
     */
    private int rows, columns;

    /**
     * Two selected rectangles, if both {@code null} no element is selected. {@code secondSelected}
     * cannot be non-null if {@code firstSelected} is null.
     */
    private Rectangle firstSelected, secondSelected;

    /**
     * Rectangle elements to be displayed
     */
    private LiveData<List<Rectangle>> field;

    /**
     * All currently attached {@link FieldListener} listeners
     */
    private List<FieldListener> listeners = new ArrayList<>();

    /**
     * Uses {@link SharedPreferences} to access the {@code boolean continue} value, which indicates
     * whether the field needs to be loaded from the database or a new field needs to be created.
     * Creates a new {@link FieldAdapter} adapter with {@link GridLayoutManager}. Sets a button listener
     * on {@link Field#switchButton}.
     * @param savedInstanceState Bundle of last saved state
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        recyclerView = findViewById(R.id.recycler_view);
        switchButton = findViewById(R.id.switch_button);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        SharedPreferences preferences = getSharedPreferences(
                String.valueOf(getText(R.string.continue_preferences)),
                Context.MODE_PRIVATE);
        boolean cont = preferences.getBoolean(String.valueOf(getText(R.string.continue_key)), false);

        if(!cont) {
            Bundle extras = getIntent().getExtras();
            assert extras != null;
            Bundle dimensions = extras.getBundle("dimensions");
            assert dimensions != null;
            rows = dimensions.getInt("rows");
            columns = dimensions.getInt("columns");
            if (rows <= 0 || columns <= 0) throw new IllegalArgumentException();
        } else {
            rows = preferences.getInt(String.valueOf(getText(R.string.rows)), -1);
            columns = preferences.getInt(String.valueOf(getText(R.string.columns)), -1);
            if(rows < 0 || columns < 0) throw new IllegalArgumentException("Couldn't find rows or columns values!");
        }
        field = viewModel.getAllRectangles();
        field.observe(this, this::processFieldChange);

        fieldAdapter = new FieldAdapter(this, Objects.requireNonNull(field.getValue()),
                rows, columns);
        fieldAdapter.setClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fieldAdapter);

        switchButton.setOnClickListener(v -> {
            if(secondSelected != null) {
                List<Rectangle> rectangles = field.getValue();
                int index1 = firstSelected.getIndex(), index2 = secondSelected.getIndex();
                rectangles.set(index1, secondSelected);
                rectangles.set(index2, firstSelected);
                firstSelected.setSelected(false);
                secondSelected.setSelected(false);
                firstSelected.setIndex(index2);
                secondSelected.setIndex(index1);
                firstSelected = secondSelected = null;
                processFieldChange(rectangles);
            }
        });
    }

    /**
     * Sets rectangle's selection to {@code false} if it was {@code true}, and vice versa.
     * If rectangle was selected, saves it in either {@link Field#firstSelected}
     * or {@link Field#secondSelected}.
     * @param v view in which click occurred
     * @param position view's position in {@link Field#recyclerView}
     */
    @Override
    public void onRectangleClick(View v, int position) {
        List<Rectangle> rectangleList = Objects.requireNonNull(field.getValue());
        Rectangle cur = rectangleList.get(position);
        cur.setSelected(!cur.isSelected());

        if(cur.equals(firstSelected)) {
            if(secondSelected == null) {
                firstSelected = null;
            } else {
                firstSelected = secondSelected;
                secondSelected = null;
            }
        } else if(cur.equals(secondSelected)) {
            secondSelected = null;
        } else {
            if(secondSelected == null) {
                if(firstSelected == null) firstSelected = cur;
                else secondSelected = cur;
            } else {
                firstSelected.setSelected(false);
                firstSelected = secondSelected;
                secondSelected = cur;
            }
        }
        processFieldChange(rectangleList);
    }

    /**
     * Database is updated with new rectangle values and value {@code boolean continue}
     * is stored in {@link SharedPreferences}, as well as {@code rows} and {@code columns} values.
     */
    @Override
    protected void onPause() {
        super.onPause();
        for(Rectangle r : Objects.requireNonNull(field.getValue())) {
            viewModel.updateRectangle(r);
        }
        SharedPreferences preferences = getSharedPreferences(
                String.valueOf(getText(R.string.continue_preferences)),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(String.valueOf(getText(R.string.continue_key)), true);
        editor.putInt(String.valueOf(getText(R.string.rows)), rows);
        editor.putInt(String.valueOf(getText(R.string.columns)), columns);
        editor.apply();
    }

    /**
     * On {@link KeyEvent#KEYCODE_VOLUME_UP} the field is shifted to the right by 1, and the last
     * rectangle becomes the first element of the list. On {@link KeyEvent#KEYCODE_VOLUME_DOWN}
     * the field is shuffled. In both cases, the rectangles'
     * {@link Rectangle#getIndex()} values are updated to appropriate values.
     * @param keyCode {@link KeyEvent}
     * @param event {@link KeyEvent}
     * @return boolean true if the event was handled, false otherwise
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() != KeyEvent.ACTION_DOWN) return false;
        List<Rectangle> rectangles = Objects.requireNonNull(field.getValue());

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Rectangle last = rectangles.remove(rectangles.size()-1);
                rectangles.add(0, last);
                for(int i = 0; i < rectangles.size(); i++) {
                    rectangles.get(i).setIndex(i);
                }
                processFieldChange(rectangles);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Collections.shuffle(rectangles);
                for(int i = 0; i < rectangles.size(); i++) {
                    rectangles.get(i).setIndex(i);
                }
                processFieldChange(rectangles);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Util method for notifying the {@link Field#fieldAdapter} of data set change and calling
     * {@link Field#fieldChanged()}.
     */
    private void processFieldChange(List<Rectangle> rectangles) {
        fieldAdapter.dataChanged(rectangles);
        recyclerView.invalidate();
        recyclerView.requestLayout();
        fieldChanged();
    }

    /**
     * Method for notifying listeners of a change in rectangle field
     */
    protected void fieldChanged() {
        for(FieldListener fl : listeners) {
            fl.onFieldChange();
        }
    }

    /**
     * Adds {@code fl} to {@link Field#listeners} listeners list.
     * @param fl listener to be attached
     */
    public void attachFieldListener(FieldListener fl) {
        if(listeners.contains(fl)) return;
        listeners.add(fl);
    }

    /**
     * Removes {@code fl} from {@link Field#listeners} listeners list.
     * @param fl listener to be detached
     */
    public void detachFieldListener(FieldListener fl) {
        listeners.remove(fl);
    }
}
