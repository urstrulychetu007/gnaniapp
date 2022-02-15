package com.gnani.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.eclipse.sisu.launch.Main;

public class WelcomeActivity extends AppCompatActivity {
    private ImageButton eh,he;
    private String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        eh = findViewById(R.id.eh);
        he = findViewById(R.id.he);
        eh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "eng_IN";
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("lang",lang);
            }
        });
        he.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "hin_IN";
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("lang",lang);
            }
        });
    }
}