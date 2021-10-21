package com.example.projeto;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.projeto.databinding.ActivityHistoricoBinding;

public class Historico extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private ActivityHistoricoBinding binding;
    private Marker maker = null;
    private SharedPreferences sharedPrefs;

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
            Toast.makeText(this, "Localização desativada", Toast.LENGTH_SHORT).show();
            finish();
        }
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
        int i = sharedPrefs.getInt("Contador", 0);
        while(i!=0){
            double latitude = (double) sharedPrefs.getFloat("Latitude" + i, 0);
            double longitude = (double) sharedPrefs.getFloat("Longitude" + i, 0);
            LatLng ponto = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(ponto).title("atualizando"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ponto, 15));
            i--;
        }
    }


    public static void mudaMapa(boolean tipo, boolean trafego){
        mMap.setTrafficEnabled(trafego);

        if(tipo==true) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if(tipo==false) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }}