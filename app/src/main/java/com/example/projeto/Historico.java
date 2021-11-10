package com.example.projeto;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.projeto.databinding.ActivityHistoricoBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Historico extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private ActivityHistoricoBinding binding;
    private SharedPreferences sharedPrefs;
    List<Local> locais = new ArrayList<Local>();
    List<LatLng> decodedPath = new ArrayList<LatLng>();
    BancoDados db = new BancoDados(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoricoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Historico.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        clickFPBtn();
    }
    public void clickFPBtn(){
        Intent l = new Intent(this, LogActvity.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(l);
            }
        });
    }

    public void addRota(LatLng ponto){
        decodedPath.add(ponto); // latitude e longitude
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(R.color.white));
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(20.0f);

        //Recuperando dados da tabela
        boolean tipo = sharedPrefs.getBoolean("Imagem", false);
        boolean trafego = sharedPrefs.getBoolean("Informacao",false);
        mudaMapa(tipo, trafego);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Localização ao longo do tempo
        locais =  db.lerDados();
        if(locais.size() > 1){
            for(Local lat : locais){
                LatLng ponto = new LatLng(lat.getLatitude(), lat.getLongitude());
                addRota(ponto);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ponto, 18));
            }

            int i = locais.size();
            LatLng pontoOrigem = new LatLng(locais.get(0).getLatitude(), locais.get(0).getLongitude());
            LatLng pontoDestino = new LatLng(locais.get(i-1).getLatitude(), locais.get(i-1).getLongitude());
            addMarcadorComum(pontoOrigem);
            addMarcadorCarro(pontoDestino);
            db.close();
        }
        else if(locais.size() == 1){
            LatLng pontoOrigem = new LatLng(locais.get(0).getLatitude(), locais.get(0).getLongitude());
            addMarcadorCarro(pontoOrigem);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pontoOrigem, 18));
            db.close();
        }
        db.close();
    }

    public void addMarcadorComum(LatLng pontoOrigem ){
        mMap.addMarker(new MarkerOptions()
                .position(pontoOrigem)
                .title("Origem"));

    }


    public void addMarcadorCarro(LatLng pontoDestino){
        mMap.addMarker(new MarkerOptions()
                .position(pontoDestino)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.makerlocation))
                .title("Destino"));
    }

    public static void mudaMapa(boolean tipo, boolean trafego){
        mMap.setTrafficEnabled(trafego);

        if(tipo==true) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if(tipo==false) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    recreate();
                }
                else{
                    Toast.makeText(this, "Localização desativada", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
    }
}

