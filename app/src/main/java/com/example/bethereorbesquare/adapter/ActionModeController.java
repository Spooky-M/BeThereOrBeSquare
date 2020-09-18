package com.example.bethereorbesquare.adapter;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.selection.SelectionTracker;

public class ActionModeController implements ActionMode.Callback {

    private Context context;
    private SelectionTracker<Long> selectionTracker;

    public ActionModeController(Context context, SelectionTracker<Long> selectionTracker) {
        this.context = context;
        this.selectionTracker = selectionTracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionTracker.clearSelection();
    }
}
