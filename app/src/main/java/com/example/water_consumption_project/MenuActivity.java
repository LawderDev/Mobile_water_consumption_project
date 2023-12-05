package com.example.water_consumption_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        ImageButton closeButton = findViewById(R.id.menu_button);
        closeButton.setOnClickListener((v) ->{
            finish();
        });
    }
}
