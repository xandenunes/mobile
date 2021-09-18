package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button button_nav = (Button) findViewById(R.id.button_nav);
        Button button_gnss = (Button) findViewById(R.id.button_gnss);
        Button button_config = (Button) findViewById(R.id.button_config);
        Button button_histo = (Button) findViewById(R.id.button_histo);
        Button button_credito = (Button) findViewById(R.id.button_credito);
        button_nav.setOnClickListener(this);
        button_gnss.setOnClickListener(this);
        button_config.setOnClickListener(this);
        button_histo.setOnClickListener(this);
        button_credito.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
       switch(view.getId()){
           case R.id.button_nav:

               break;

           case R.id.button_gnss:

               break;

           case R.id.button_config:
                Intent j = new Intent(this, Configuracao.class);
                startActivity(j);
                break;

           case R.id.button_histo:


               break;

           case R.id.button_credito:
               Intent i = new Intent(this, Credito.class);
               startActivity(i);
               break;
       }
    }
}