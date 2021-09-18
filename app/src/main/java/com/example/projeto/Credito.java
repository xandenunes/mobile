package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Credito extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_credito);
    }
}