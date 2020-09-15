package com.example.bethereorbesquare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.shapes.Rectangle;

import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private Context context;
    private List<Rectangle> rectangles;

    public FieldAdapter(Context context, List<Rectangle> rectangles) {
        this.rectangles = rectangles;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.rectangle_view, parent, false);
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
        return rectangles.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView rectangleView;

        public FieldViewHolder(TextView v) {
            super(v);
            rectangleView = v.findViewById(R.id.rectangle_view);
        }

        public void setDetails(Rectangle r) {
//            Random rand = new Random();
//            rectangleView.setBackgroundColor(rand.nextInt(256));
            rectangleView.setBackgroundColor(Color.parseColor(r.getColor().getHex()));
            rectangleView.setText(r.getIndex());

            rectangleView.setFocusableInTouchMode(true);
            rectangleView.setEnabled(true);
            rectangleView.setClickable(true);
        }
    }
}

