package com.example.bethereorbesquare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private Context context;
    private List<Rectangle> rectangles;
    private RectangleClickListener rectangleClickListener;
//    private SelectionTracker<Long> selectionTracker;

    public FieldAdapter(Context context, List<Rectangle> rectangles) {
        this.rectangles = rectangles;
        this.context = context;
        this.setHasStableIds(true);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.rectangle_cell, parent, false);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams()
//        TextView v = new TextView(context);
        return new FieldViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FieldViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setDetails(rectangles.get(position));
    }

    @Override
    public int getItemCount() {
        return rectangles == null ? 0 : rectangles.size();
    }

    @Override
    public long getItemId(int position) {
        return rectangles.get(position).getId();
    }

    //    public SelectionTracker<Long> getSelectionTracker() {
//        return selectionTracker;
//    }
//
//    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
//        this.selectionTracker = selectionTracker;
//    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class FieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView rectangleView;

        public FieldViewHolder(TextView v) {
            super(v);
            rectangleView = v.findViewById(R.id.rectangle_view);
            v.setOnClickListener(this);
        }

        public void setDetails(Rectangle r) {
            rectangleView.setBackgroundColor(Color.parseColor(r.getColor().getHex()));
            handleSelection(r);
            rectangleView.setText(String.valueOf(r.getIndex() + 1));
            rectangleView.setFocusableInTouchMode(true);
            rectangleView.setClickable(true);
            rectangleView.setEnabled(true);
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new FieldItemDetail(getAdapterPosition(), rectangles.get(getAdapterPosition()).getId());
        }

        @Override
        public void onClick(View v) {
            if(rectangleClickListener != null) rectangleClickListener.onRectangleClick(v, getAdapterPosition());
        }

        private void handleSelection(Rectangle rectangle) {
            if(rectangle.isSelected()) {
                rectangleView.setActivated(true);
                Drawable d = ContextCompat.getDrawable(context, R.drawable.selected_cell);
                rectangleView.setBackground(d);
            } else {
                rectangleView.setActivated(false);
            }
        }
    }

    public void setClickListener(RectangleClickListener listener) {
        this.rectangleClickListener = listener;
    }

    public interface RectangleClickListener {
        void onRectangleClick(View v, int position);
    }
}

