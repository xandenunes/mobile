package com.example.projeto;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.projeto.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private static GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    private Marker maker = null;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    List<Location> local = new ArrayList<Location>();
    private TextView txtLatitude;
    private TextView txtLongitude;
    private TextView txtVelocidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            client = LocationServices.getFusedLocationProviderClient(this);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            txtLatitude = (TextView) findViewById(R.id.text_latitude);
            txtLongitude = (TextView) findViewById(R.id.text_longitude);
            txtVelocidade = (TextView) findViewById(R.id.text_Velocidade);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(20.0f);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean tipo = sharedPrefs.getBoolean("Imagem", false);
        boolean trafego = sharedPrefs.getBoolean("Informacao",false);
        mudaMapa(tipo, trafego);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //zoom para teste no emulador
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Pedi permissão para usar a localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
            client.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null){
                                LatLng origem = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origem, 15));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        LocationRequest locationRequest = LocationRequest.create();
        //intervalo da busca da localização
        locationRequest.setInterval(10 * 1000);
        //intervalo da busca da localização caso outros apps que também estejam usando o maps
        locationRequest.setFastestInterval(5 * 1000);
        //precisão do gps
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ResolvableApiException){
                            try{
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MapsActivity.this, 10);
                            } catch(IntentSender.SendIntentException e1){
                            }
                        }
                    }
                });

        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                for(Location location: locationResult.getLocations()){
                    local.add(location);
                    txtLatitude.setText(coordenadas(location.getLatitude()));
                    txtLongitude.setText(coordenadas(location.getLongitude()));
                    txtVelocidade.setText(velocidade(location.getSpeed()));
                }
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability){
            }
        };
        client.requestLocationUpdates(locationRequest, locationCallback, null);

    }


    public String velocidade(double velo){
        String result = "";
       Boolean unidadeMh = sharedPrefs.getBoolean("MH", false);
        if(unidadeMh == false){
            result = String.valueOf(velo * 1.61);
        }
        else{
            result = String.valueOf(velo);
        }
        return result;
    }

    public String coordenadas(double lat){
        double valGrau, valMin, valSeg = 0;
        String result = "";
        lat = Math.abs(lat);

        Boolean Coordenada2 = sharedPrefs.getBoolean("Coordenada_2", false);
        Boolean Coordenada3 = sharedPrefs.getBoolean("Coordenada_3", false);

        if(Coordenada2 == false && Coordenada2 == false){
            //para grau
            lat = Math.abs(lat);

            valGrau = Math.floor(lat);
            result = valGrau + "º";

            valMin = Math.floor((lat - valGrau) * 3600);
            result += valMin;

        }

        if(Coordenada2 == true){
            //para grau e minutos
            valGrau = Math.floor(lat);
            result = valGrau + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += valMin + "'";

            valSeg = Math.floor((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000;
            result += valSeg;

        }

        if(Coordenada3 == true){
            valGrau = Math.floor(lat);
            result = valGrau + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += valMin + "'";

            valSeg = Math.round((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000;
            result += valSeg + "''";

        }
        return result;
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

    @Override
    public void onMapClick(LatLng latLng) {
        //testa se já existe um marcador e o remove
        if(maker != null){
           maker.remove();
        }
        //add um novo marcador
        maker = mMap.addMarker(new MarkerOptions().position(latLng).title("Estou aqui"));
    }

    @Override
    public void onPause(){
        super.onPause();
        sharedPrefsEditor = sharedPrefs.edit();
        if(sharedPrefsEditor!=null){
            int i = 1;
            for(Location l: local ){
                float longitude = (float) l.getLongitude();
                float latitude = (float) l.getLatitude();
                sharedPrefsEditor.putFloat("Latitude" + i, latitude);
                sharedPrefsEditor.putFloat("Longitude" + i, longitude);
                i++;
                sharedPrefsEditor.putInt("Contador", i);
                sharedPrefsEditor.commit();
            }
        }
    }

    public static void mudaMapa(boolean tipo, boolean trafego){
        mMap.setTrafficEnabled(trafego);

        mMap.moveCamera(CameraUpdateFactory.scrollBy(0,0));

       if(tipo==true) {
           mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
       }
       if(tipo==false) {
           mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
       }


    }
}