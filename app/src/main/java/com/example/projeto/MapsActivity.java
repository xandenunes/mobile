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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    private Marker maker, maker2 = null;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor; //usando para a tela histórico
    private Circle circuloDaLocalizacao = null;
    private TextView txtLatitude, txtLongitude, txtVelocidade;
    List<Location> local = new ArrayList<Location>(); //usando para a tela histórico

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

        //recuperando dados da tabela referente a tipo e tráfego
        boolean tipo = sharedPrefs.getBoolean("Imagem", false);
        boolean trafego = sharedPrefs.getBoolean("Informacao",false);
        mudaMapa(tipo, trafego);

        //mMap.setMyLocationEnabled(true);
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
        //recuperando dados da tabela referente a orientação
        boolean orientacao2 = sharedPrefs.getBoolean("North", false);
        boolean orientacao3 = sharedPrefs.getBoolean("Course", false);

            //pegando a locazição do usuário
            client.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location == null) {
                                //localização padrão
                                txtLatitude.setText(coordenadas(-33.87365));
                                txtLongitude.setText(coordenadas(151.20689));
                                txtVelocidade.setText(velocidade(0.0));
                                LatLng cidade = new LatLng(-33.87365, 151.20689);
                                maker2 = mMap.addMarker(new MarkerOptions()
                                        .position(cidade)
                                        .title("Local padrão")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.makerlocation)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cidade, 15));
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

        /* Testa se os servições de localização estão ativados
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
         */

        //atualizando a localização
        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                for(Location location: locationResult.getLocations()){
                    if(location != null) {
                        //salvando uma tabela do tipo Localização
                        local.add(location); //usando na tela histórico

                        float rotacao = location.getBearing();
                        //exibindo a localização na barra de status
                        txtLatitude.setText(coordenadas(location.getLatitude()));
                        txtLongitude.setText(coordenadas(location.getLongitude()));
                        txtVelocidade.setText(velocidade(location.getSpeed()));

                        LatLng origem = new LatLng(location.getLatitude(), location.getLongitude());

                        //adicionando marcador do carro
                        if (maker2 != null) {
                            maker2.remove();
                        }
                        maker2 = mMap.addMarker(new MarkerOptions()
                                .position(origem)
                                .title("Estou aqui")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.makerlocation))
                                .rotation(rotacao));

                        //adicionando o círculo
                        if(circuloDaLocalizacao != null){
                            circuloDaLocalizacao.remove();
                        }
                        circuloDaLocalizacao = mMap.addCircle(new CircleOptions()
                                .center(origem)
                                .radius(18)
                                .strokeColor(R.color.transparence)
                                .fillColor(R.color.transparence)
                                .strokeWidth(location.getAccuracy())
                        );

                        //mudando orientação
                        if (orientacao2 == true) {
                            mMap.getUiSettings().setRotateGesturesEnabled(false);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origem, 19));
                        }
                        if (orientacao3 == true) {
                            //atualizando posição da câmera
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(origem)
                                    .bearing(rotacao)
                                    .zoom(19)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
                        }
                        if (orientacao2 == false && orientacao3 == false) {
                            mMap.getUiSettings().setRotateGesturesEnabled(true);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origem, 19));
                        }
                    }
                }
            }
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability){
            }
        };

        //enviando a atualização da localização para o cliente
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public String velocidade(double velo){
        String result = "";
        String identificador = "VELOCIDADE";
        double velocidade = velo;
        Boolean unidadeMh = sharedPrefs.getBoolean("MH", false);

        if(unidadeMh == false){
            double Km = velocidade*3.60;
            result = formatarFloat(Km, identificador) + " Km/h";
        }
        else{
            double mh = velocidade*2.24;
            result = formatarFloat(mh, identificador) + " mph";
        }
        return result;
    }

    public String formatarFloat(double numero, String identificador){
        String retorno = "";
        if(identificador.equals("VELOCIDADE")){
            DecimalFormat formatter = new DecimalFormat("0.00");
            try{
                retorno = formatter.format(numero);
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        if(identificador.equals("COORDENADA")){
            DecimalFormat formatter = new DecimalFormat("00");
            try{
                retorno = formatter.format(numero);
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        if(identificador.equals("COORDENADA2")){
            DecimalFormat formatter = new DecimalFormat("#.00");
            try{
                retorno = formatter.format(numero);
                retorno = retorno.replace(",", "");
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        if(identificador.equals("COORDENADA3")){
            DecimalFormat formatter = new DecimalFormat("#.000");
            try{
                retorno = formatter.format(numero);
                retorno = retorno.replace(",", "");
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        return retorno;
    }

    public String coordenadas(double lat){
        double valGrau, valMin, valSeg, auxSeg = 0;
        String result = "";
        String retorno = "";
        String identificador = "COORDENADA";
        String identificador2 = "COORDENADA2";
        String identificador3 = "COORDENADA3";
        double aux = lat;

        lat = Math.abs(lat);

        Boolean Coordenada2 = sharedPrefs.getBoolean("Coordenada_2", false);
        Boolean Coordenada3 = sharedPrefs.getBoolean("Coordenada_3", false);

        if(Coordenada2 == false && Coordenada3 == false){
            //para grau
            lat = Math.abs(lat);

            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificador) + "º";

            auxSeg = Math.floor((lat - valGrau) * 60);
            valMin = Math.floor((lat - valGrau) * 60) / 60;
            result += formatarFloat(valMin, identificador2);

            valSeg = (Math.floor((lat - valGrau - auxSeg / 60) * 3600 * 1000) / 1000) / 3600;
            result += formatarFloat(valSeg, identificador3);
        }

        if(Coordenada2 == true){
            //para grau e minutos
            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificador) + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += formatarFloat(valMin, identificador) + ".";

            valSeg = (Math.floor((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000) / 3600;
            result += formatarFloat(valSeg, identificador3) + "'";
        }

        if(Coordenada3 == true){
            //para grau, minutos e segundos
            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificador) + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += formatarFloat(valMin, identificador) + "'";

            valSeg = Math.round((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000;
            result += formatarFloat(valSeg, identificador) + "''";
        }
        if(aux <= -0){
            retorno = "-" + result;
        }
        else{
            retorno = result;
        }
        return retorno;
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
        maker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Estou aqui"));
    }

    @Override
    public void onPause(){
        //usando na tela histórico
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

    public void mudaMapa(boolean tipo, boolean trafego){
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