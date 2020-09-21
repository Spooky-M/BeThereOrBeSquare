package com.example.bethereorbesquare.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.List;

/**
 * Adapter for {@link RecyclerView}.
 */
public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    /**
     * Current app context
     */
    private Context context;

    /**
     * Data, list of all rectangles to be drawn
     */
    private List<Rectangle> rectangles;

    /**
     * Click listener, is triggered with a click on its {@link FieldViewHolder}.
     * See also {@link RectangleClickListener}
     */
    private RectangleClickListener rectangleClickListener;

    /**
     * Height and width of one item and its {@link FieldViewHolder}
     */
    private int itemHeight, itemWidth;

    /**
     * Field dimensions
     */
    private int rows, columns;

    /**
     * A basic constructor, sets {@link FieldAdapter#setHasStableIds(boolean)} to {@code true}.
     * @param context {@link FieldAdapter#context}
     * @param rectangles {@link FieldAdapter#rectangles}
     * @param rows field row count
     * @param columns field column count
     */
    public FieldAdapter(Context context, List<Rectangle> rectangles, int rows, int columns) {
        this.rectangles = rectangles;
        this.context = context;
        this.rows = rows;
        this.columns = columns;
        this.setHasStableIds(true);
    }

    /**
     * Creates new views (invoked by the layout manager)
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.rectangle_cell, parent, false);
        return new FieldViewHolder(v);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        calculateDimensions();
        if(itemWidth != 0 && itemHeight != 0) {
            holder.rectangleView.getLayoutParams().height = itemHeight;
            holder.rectangleView.getLayoutParams().width = itemWidth;
        }
        holder.setDetails(rectangles.get(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return rectangles == null ? 0 : rectangles.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return rectangles.get(position).getId();
    }

    /**
     * @return {@link FieldAdapter#itemHeight}
     */
    public int getItemHeight() {
        return itemHeight;
    }

    /**
     * @return {@link FieldAdapter#itemWidth}
     */
    public int getItemWidth() {
        return itemWidth;
    }

    /**
     * Calculates dimensions of a single rectangle cell
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void calculateDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int usableHeight = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;

        //todo dobiti razliku izmedu donjeg ruba recycleviewa i dna ekrana

        if (realHeight > usableHeight)
            itemHeight = (int)(2*realHeight - usableHeight) / rows;
        else
            itemHeight = (int)(usableHeight) / rows;
        itemWidth = width / columns;
    }

    /**
     * Provides a reference to the views for each data item.
     */
    public class FieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * One rectangle cell is a colored {@link TextView}, with text set to {@link Rectangle#getId()}
         */
        public TextView rectangleView;

        /**
         * View holder's only constructor, binds {@link FieldViewHolder#rectangleView} to the input argument.
         * @param v input {@link TextView}
         */
        public FieldViewHolder(TextView v) {
            super(v);
            rectangleView = v;
            v.setOnClickListener(this);
        }

        /**
         * Sets view's background, selection bounding box and text to the values taken from rectangle {@code r}.
         * @param r rectangle which provides data for view's background and text
         */
        public void setDetails(Rectangle r) {
            rectangleView.setBackgroundColor(Color.parseColor(r.getColor().getHex()));
            handleSelection(r);
            rectangleView.setText(String.valueOf(r.getId()));
            rectangleView.setClickable(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            if(rectangleClickListener != null) {
                rectangleClickListener.onRectangleClick(v, getAdapterPosition());
            }
        }

        /**
         * Activates the view and draws the bounding box if rectangle is selected.
         * @param rectangle corresponding {@link Rectangle} object
         */
        private void handleSelection(Rectangle rectangle) {
            if(rectangle.isSelected()) {
                rectangleView.setActivated(true);
                Drawable d = ContextCompat.getDrawable(context, R.drawable.selected_cell);
                assert d != null;
                rectangleView.setBackground(d);
            } else {
                rectangleView.setActivated(false);
            }
        }
    }

    /**
     * Binds a {@link RectangleClickListener} to this adapter.
     * @param listener provided listener which is to be added to the list of active listeners
     */
    public void setClickListener(RectangleClickListener listener) {
        this.rectangleClickListener = listener;
    }

    /**
     * Functional interface which should be implemented by classes using this adapter, if they need to handle
     * click events. Interface's method provides the user of this adapter with the view which was clicked,
     * as well as its position within the parent {@link RecyclerView}.
     */
    @FunctionalInterface
    public interface RectangleClickListener {
        void onRectangleClick(View v, int position);
    }
}

