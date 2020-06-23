package com.example.adam.poolapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button playersButton;
    private Button teamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        addButtonClickHandlers();
    }

    private void addButtonClickHandlers() {
        playersButton = findViewById(R.id.playersButton);
        teamButton = findViewById(R.id.teamButton);
        playersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, PlayerSummaryActivity.class);
            startActivity(intent);
        });
    }
}
