package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity implements OnClickListener {
private long mLastClickTime = 0;

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
    public void onResume(){
        super.onResume();
        //Testar se o Play services não está desativado ou desatualizado
        int erro = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (erro){
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, erro,
                        0, new DialogInterface.OnCancelListener(){
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        }).show();
                break;
            case ConnectionResult.SUCCESS:
        }
    }

    @Override
    public void onClick(View view) {
        //evita que o usuário dê double click
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch(view.getId()){
           case R.id.button_nav:
                   Intent m = new Intent(this,MapsActivity.class);
                   startActivity(m);
               break;

           case R.id.button_gnss:
                Intent g = new Intent(this, Gnss.class);
                startActivity(g);
               break;

           case R.id.button_config:
                Intent j = new Intent(this, Configuracao.class);
                startActivity(j);
                break;

           case R.id.button_histo:
               Intent h = new Intent(this, Historico.class);
               startActivity(h);
               break;

           case R.id.button_credito:
               Intent i = new Intent(this, Credito.class);
               startActivity(i);
               break;
       }
    }
}