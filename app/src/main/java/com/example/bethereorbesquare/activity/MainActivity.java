package com.example.bethereorbesquare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.Util;
import com.example.bethereorbesquare.entity.CustomColor;
import com.example.bethereorbesquare.entity.Rectangle;
import com.example.bethereorbesquare.network.GetColorService;
import com.example.bethereorbesquare.network.RetrofitInstance;
import com.example.bethereorbesquare.repository.ColorRepository;
import com.example.bethereorbesquare.view.AppViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Home page and main Activity, contains a title label and 2 buttons. "Start" is for opening
 * an AlertDialog for creating a new rectangular field with arbitrary number of rows and columns.
 * "Continue" button will restart the last active rectangular field, if any. On creating activity,
 * colors are fetched using {@code Retrofit 2.0}, if necessary.
 *
 * {@see <a href="https://square.github.io/retrofit/">https://square.github.io/retrofit/<a/>}
 */
public class MainActivity extends AppCompatActivity {

    /**
     * A title {@link TextView}
     */
    private TextView title;

    /**
     * Two function buttons for starting a new field, or continuing with the last saved
     * instance of rectangles field.
     */
    private Button startButton, continueButton;

    private List<CustomColor> colors;

    /**
     * App's view model which contains methods for communication with {@code Room} database
     */
    private AppViewModel viewModel;

    /**
     * A progress bar element which is shown while the colors are being loaded
     */
    private ProgressBar progressBar;

    /**
     * Creates a {@link ColorLoadingAsyncTask} for loading colors if necessary. A click
     * on {@code Start} creates a new {@link AlertDialog}. When {@code AlertDialog} element
     * is filled with suitable row and column numbers, {@link Field} Activity is called.
     * A click on {@code Continue} tries to load the last saved state.
     * @param savedInstanceState Bundle of last saved state
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.title);
        startButton = findViewById(R.id.start_button);
        continueButton = findViewById(R.id.continue_button);
        progressBar = new ProgressBar(MainActivity.this);

        //TODO 1) Implementiraj Room umjesto SQLiteOpenHelper
        // dokumentacija -> https://developer.android.com/training/data-storage/room
        // codelab -> https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#13
        // primjer -> https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        List<CustomColor> colors = viewModel.getAllColors().getValue();

        ColorLoadingAsyncTask asyncTaskColors;
        if(colors == null || colors.isEmpty()) {
            asyncTaskColors = new ColorLoadingAsyncTask();
            asyncTaskColors.execute();
        }

        SharedPreferences preferences = getSharedPreferences(String.valueOf(getText(R.string.continue_preferences)), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        startButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("New Field");
            final View customLayout = getLayoutInflater().inflate(R.layout.activity_newfield, null);
            builder.setView(customLayout); // set the custom layout

            final TextInputEditText inputFieldRows = customLayout.findViewById(R.id.editTextRows);
            final TextInputEditText inputFieldColumns = customLayout.findViewById(R.id.editTextColumns);

            builder.setPositiveButton("Next", (dialog, which) -> {    // add a button
                // read data and send it from the AlertDialog to the Activity
                int rows, columns;
                int maxRows = getResources().getInteger(R.integer.max_rows);
                int maxColumns = getResources().getInteger(R.integer.max_columns);
                if(maxRows <= 0 || maxColumns <= 0) {
                    Log.wtf("Error",
                            "Values for rows and columns need to be positive, " +
                                    "row count has to be < " + maxRows +
                                    " and column count has to be < " + maxColumns + ".");
                }

                try {
                    rows = Integer.parseInt(String.valueOf(inputFieldRows.getText()));
                    columns = Integer.parseInt(String.valueOf(inputFieldColumns.getText()));
                } catch(IllegalArgumentException e) {
                    buildAlertDialog("Illegal values",
                            "Values for rows and columns need to be positive, " +
                                    "row count has to be < " + maxRows +
                                    " and column count has to be < " + maxColumns + ".");
                    return;
                }
                if(rows <= 0 || columns <= 0 || rows > maxRows || columns > maxColumns) {
                    buildAlertDialog("Illegal values",
                            "Values for rows and columns need to be positive, " +
                                    "row count has to be < " + maxRows +
                                    " and column count has to be < " + maxColumns + ".");
                    return;
                }
                if(colors == null) {
                    buildAlertDialog("Error",
                            "Colors have not been loaded. Try restarting the app.");
                    return;
                }

                RectangleCreationAsyncTask asyncTask = new RectangleCreationAsyncTask();
                asyncTask.execute(rows, columns);

                editor.putBoolean(String.valueOf(getText(R.string.continue_key)), false);
                editor.apply();
                Bundle dimensions = new Bundle();
                dimensions.putInt("rows", rows);
                dimensions.putInt("columns", columns);

                Intent intent = new Intent(MainActivity.this, Field.class);
                intent.putExtra("dimensions", dimensions);
                startActivity(intent);
            });        // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        continueButton.setOnClickListener(v -> {
            List<Rectangle> rectangles = viewModel.getAllRectangles().getValue();
            boolean rowsValidityCheck = preferences.getInt(String.valueOf(getText(R.string.rows)), -1) > 0;
            if(rectangles == null || rectangles.isEmpty() || !rowsValidityCheck) {
                buildAlertDialog("Error",
                        "There's no saved state. Create a new field by choosing \"Start\".");
            } else {
                editor.putBoolean(String.valueOf(getText(R.string.continue_key)), true);
                editor.apply();
                startActivity(new Intent(MainActivity.this, Field.class));
            }
        });
    }

    /**
     * Builds an {@link AlertDialog} window with {@code title}, {@code message}
     * and positive "OK" button and shows it.
     * @param title window title
     * @param message window display message
     */
    private void buildAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog d = builder.create();
        d.show();
    }

    /**
     * Class for asynchronous colors loading in a background thread.
     */
    private class ColorLoadingAsyncTask extends AsyncTask<Void, Integer, List<CustomColor>> {

        /**
         * Internally saved colors
         */
        private List<CustomColor> colors;

        /**
         *
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            continueButton.setEnabled(false);
            startButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Obtains colors using {@link RetrofitInstance}. Fetches colors from
         * <a href="https://goo.gl/gEhgzs/">https://goo.gl/gEhgzs/</a>
         * and stores them with {@link ColorRepository} into Room database. On successful response
         * colors table is filled. While waiting for the response, {@link ProgressBar} is present.
         * @return list of colors
         */
        @Override
        protected List<CustomColor> doInBackground(Void... voids) {
            GetColorService service = RetrofitInstance.getRetrofitInstance().create(GetColorService.class);
            Call<List<CustomColor>> call = service.getColorData();

            call.enqueue(new Callback<List<CustomColor>>() {
                @Override
                public void onResponse(Call<List<CustomColor>> call, Response<List<CustomColor>> response) {
                    List<CustomColor> colorsList = response.body();
                    for(CustomColor c : colorsList) {
                        c.setName(c.getName().replaceAll("'", ""));
                    }
                    viewModel.insertAllColors(colorsList);
                    MainActivity.this.colors = colorsList;
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<CustomColor>> call, Throwable t) {
                    Toast.makeText(MainActivity.this,
                            "Something went wrong... Please try later!", Toast.LENGTH_LONG).show();
                }
            });
            return colors;
        }

        /**
         *
         * @param colors list of colors loaded with {@link ColorLoadingAsyncTask#doInBackground(Void...)}
         */
        @Override
        protected void onPostExecute(List<CustomColor> colors) {
            super.onPostExecute(colors);
            continueButton.setEnabled(true);
            startButton.setEnabled(true);
            MainActivity.this.colors = colors;
        }
    }

    /**
     * Class for asynchronous colors loading in a background thread.
     */
    private class RectangleCreationAsyncTask extends AsyncTask<Integer, Integer, List<Rectangle>> {

        /**
         *
         * @param dimensions
         * @return
         */
        @Override
        protected List<Rectangle> doInBackground(Integer... dimensions) {
            int rows = dimensions[0], columns = dimensions[1];
            List<Rectangle> rectangles = Util.makeRectangles(rows*columns, colors);
            if(!rectangles.isEmpty()) viewModel.insertAllRectangles(rectangles);
            return rectangles;
        }

        /**
         *
         * @param rectangles
         */
        @Override
        protected void onPostExecute(List<Rectangle> rectangles) {
            super.onPostExecute(rectangles);
            progressBar.setVisibility(View.GONE);

        }
    }
}