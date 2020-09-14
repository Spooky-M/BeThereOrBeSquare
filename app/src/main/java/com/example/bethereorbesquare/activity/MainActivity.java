package com.example.bethereorbesquare.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bethereorbesquare.R;
import com.example.bethereorbesquare.model.CustomColor;
import com.example.bethereorbesquare.model.CustomColorList;
import com.example.bethereorbesquare.network.GetColorService;
import com.example.bethereorbesquare.network.RetrofitInstance;
import com.example.bethereorbesquare.service.DatabaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<CustomColor> colors;

    private TextView title;
    private Button startButton, continueButton;
    private ProgressBar progressBar;

    private View popupView;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        startButton = findViewById(R.id.start_button);
        continueButton = findViewById(R.id.continue_button);
        progressBar = findViewById(R.id.progress_bar);

        //TODO 3) Napravi spremanje boja u lokalnu bazu kako bi izbjegao dohvaćanje boja pri svakom pokretanju aplikacije
        // dodatno za vježbu možeš napravit neku vrstu keshiranja da se nakon svakih 10 min dohvate nove boje

        dbHelper = new DatabaseHelper(this);

        colors = dbHelper.getAllColors();
        if(colors == null || colors.isEmpty()) {
            fetchColors();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 5) Pošto NewField služi samo za unos stupaca i redaka, probaj ga izdvojit u AlertDialog i podesit postavljanje parametara unutar dialoga
                // primjer -> https://medium.com/@suragch/creating-a-custom-alertdialog-bae919d2efa5
                // hint: u viewu za unos texta (EditText) možeš postavit da želiš samo brojeve (android:inputType="number")

                dbHelper.initNewRectanglesTable(); // inicializiraj novu tablicu tek kad korisnik unese i potvrdi parametre, u ovoj situaciji korisnik može odustat i vratit se back tipkom a nema spremljenu tablicu jer si dropnuo tablicu
                startActivity(new Intent(MainActivity.this, NewField.class));
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                if(dbHelper == null || dbHelper.getAllRectangles().isEmpty()) {
                    //TODO 4) Zamjeni popup sa AlertDialog prikazom
                    // primjer -> https://medium.com/@suragch/making-an-alertdialog-in-android-2045381e2edb

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
                } else {
                    startActivity(new Intent(MainActivity.this, Field.class));
                }
            }
        });
    }

    private void fetchColors() {
        //TODO 1) Napraviti dohvat boja s interneta (npr. https://goo.gl/gEhgzs)
        // stariji nacin -> https://medium.com/@JasonCromer/android-asynctask-http-request-tutorial-6b429d833e28
        // napredniji nacin -> https://medium.com/@jacinth9/android-retrofit-2-0-tutorial-89de3c714c63, dokumentacija za retrofit2:  https://square.github.io/retrofit/

        //TODO 2) Prilikom dohvaćanja boja onemogući prikaz buttona, stavi loading progress (npr ProgressBar) dok se dohvaća s backenda, kada dobiješ rezultate prikaži buttone

        startButton.setEnabled(false);
        continueButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        GetColorService service = RetrofitInstance.getRetrofitInstance().create(GetColorService.class);
        Call<CustomColorList> call = service.getColorData();

        Log.wtf("URL Called", call.request().url() + "");
        call.enqueue(new Callback<CustomColorList>() {
            @Override
            public void onResponse(Call<CustomColorList> call, Response<CustomColorList> response) {
                colors = response.body().getColorsList();
                dbHelper.initNewColorsTable();
                dbHelper.insertAllColors(colors);

                startButton.setEnabled(true);
                continueButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<CustomColorList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();

                startButton.setEnabled(true);
                continueButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
    }
}