package com.example.bethereorbesquare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bethereorbesquare.R;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private Button startButton, continueButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        startButton = findViewById(R.id.start_button);
        continueButton = findViewById(R.id.continue_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewField.class));
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savedInstanceState != null) {
                    Intent intent = new Intent(MainActivity.this, Field.class);
                    intent.putExtra("field", savedInstanceState.getBundle("field"));
                    startActivity(intent);
                }
            }
        });
    }
}