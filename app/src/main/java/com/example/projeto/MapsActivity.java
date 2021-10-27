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
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient client;
    private Marker maker, maker2 = null;
    private CameraPosition cameraPosition  = null;
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
            //Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //declaração das tabelas e das labels
            sharedPrefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            txtLatitude = (TextView) findViewById(R.id.text_latitude);
            txtLongitude = (TextView) findViewById(R.id.text_longitude);
            txtVelocidade = (TextView) findViewById(R.id.text_Velocidade);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        //limite do zoom que pode ser aplicado pelo usuário
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(20.0f);

        //pede permissão para usar a localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //recuperando dados da tabela referente a tipo e tráfego
        boolean tipo = sharedPrefs.getBoolean("Imagem", false);
        boolean trafego = sharedPrefs.getBoolean("Informacao",false);
        //aplicando configurações referente a tipo e tráfego
        mudaMapa(tipo, trafego);

        //ativando localização do usuário
        mMap.setMyLocationEnabled(true);
        //ativando botão de localização do usuário
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //ativando a bússola
        mMap.getUiSettings().setCompassEnabled(true);
        //zoom para teste no emulador
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //pede permissão para usar a localização
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
                            //caso a localização não retorne um valor nulo, ou seja: serviços de localização desativados
                            if (location == null) {
                                //localização padrão
                                double latitude = -33.87365;
                                double longitude = 151.20689;
                                double velocidade = 0;
                                LatLng cidade = new LatLng(latitude, longitude);
                                escreverNaBarraDeStatus(latitude, longitude, velocidade);
                                addMarcadorDoCarro(cidade, 0);
                                atualizandoCameraComZoom(cidade);
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
        locationRequest.setInterval(5 * 1000);
        //intervalo da busca da localização caso outros apps que também estejam usando o maps
        locationRequest.setFastestInterval(2 * 1000);
        //precisão do gps
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /* Testa se os serviços de localização estão ativados
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
                        float rotacao = location.getBearing();
                        float acuracia = location.getAccuracy();
                        float aprox = mMap.getCameraPosition().zoom;
                        LatLng origem = new LatLng(location.getLatitude(), location.getLongitude());

                        //exibindo a localização na barra de status
                        escreverNaBarraDeStatus(location);

                        //chamada do método para adicionar o marcador
                        addMarcadorDoCarro(origem, rotacao);

                        //chamada do método para adicionar o círculo
                         addCirculo(origem, acuracia);

                        //chamada do método para mudar a orientação
                        mudaOrientacao(rotacao, origem, aprox, orientacao2, orientacao3);

                        //salvando uma tabela do tipo Localização
                        local.add(location); //usando na tela histórico
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

    public void escreverNaBarraDeStatus(double latitude, double longitude, double velocidade){
        txtLatitude.setText(coordenadas(latitude));
        txtLongitude.setText(coordenadas(longitude));
        txtVelocidade.setText(velocidade(velocidade));
    }

    public void escreverNaBarraDeStatus(Location location){
        txtLatitude.setText(coordenadas(location.getLatitude()));
        txtLongitude.setText(coordenadas(location.getLongitude()));
        txtVelocidade.setText(velocidade(location.getSpeed()));
    }

    public void addMarcadorDoCarro(LatLng origem, float rotacao){
        //adicionando marcador do carro
        if (maker2 != null) {
            maker2.remove();
        }
        maker2 = mMap.addMarker(new MarkerOptions()
                .position(origem)
                .flat(true)
                .rotation(rotacao)
                .title("Estou aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.makerlocation))
                .anchor(0.5f, 0.5f)
        );
    }

    public void addCirculo(LatLng origem, float acuracia){
        //adicionando o círculo
        if(circuloDaLocalizacao != null){
            circuloDaLocalizacao.remove();
        }
        circuloDaLocalizacao = mMap.addCircle(new CircleOptions()
                        .center(origem)
                        .radius(acuracia)
                        .strokeColor(R.color.transparence)
                        .fillColor(0x00000000)
                //      .strokeWidth(location.getAccuracy())
        );
    }

    public void posicaoDaCamera(float aprox, float rotacao, LatLng origem){
        cameraPosition = new CameraPosition.Builder()
                .target(origem)
                .bearing(rotacao)
                .zoom(aprox)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
    }

    public void atualizandoCameraComZoom(LatLng origem){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origem, 18.0f));
    }

    public void atualizaCameraSemZoom(LatLng origem){
        mMap.animateCamera(CameraUpdateFactory.newLatLng(origem));
    }

    public void mudaOrientacao(float rotacao, LatLng origem, float aprox, boolean orientacao2, boolean orientacao3){
        float zoom = mMap.getCameraPosition().zoom;
        //mudando orientação
        if (orientacao2 == true) {
            //modo north up
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            //atualizando a posição da câmera para caso o zoom seja padrão
            if(zoom<=6.5f){
                atualizandoCameraComZoom(origem);
            }
            //atualizando a posição da câmera mantendo o zoom dado pelo usuário
            else if(zoom>6.5f){
                atualizaCameraSemZoom(origem);
            }
        }
        if (orientacao3 == true) {
            //atualizando posição da câmera no modo course up
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            //criando uma nova posição de câmera para caso o zoom seja padrão
            if(zoom<=6.5f){
                posicaoDaCamera(18.0f, rotacao, origem);
            }
            //criando uma nova posição de câmera mantendo o zoom dado pelo usuário
            else if(zoom>6.5f){
                posicaoDaCamera(aprox, rotacao, origem);
            }
        }

        if (orientacao2 == false && orientacao3 == false) {
            //modo nenhuma
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            //atualizando a posição da câmera para caso o zoom seja padrão
            if(zoom<=6.5f){
                atualizandoCameraComZoom(origem);
            }
            //atualizando a posição da câmera mantendo o zoom dado pelo usuário
            else if(zoom>6.5f){
                atualizaCameraSemZoom(origem);
            }
        }
    }

    public String velocidade(double velo){
        String result = "";
        String identificador = "VELOCIDADE";
        double velocidade = velo;
        Boolean unidadeMh = sharedPrefs.getBoolean("MH", false);

        //velocidade em km
        if(unidadeMh == false){
            double Km = velocidade*3.60;
            result = formatarFloat(Km, identificador) + " Km/h";
        }
        //velocidade em mh
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
                formatter.setRoundingMode(RoundingMode.UP);
                retorno = formatter.format(numero);
                retorno = retorno.replace(",", "").replace("0", "");
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        if(identificador.equals("COORDENADA3")){
            DecimalFormat formatter = new DecimalFormat("#.0000");
            try{
                retorno = formatter.format(numero);
                retorno = retorno
                        .replace(",", "")
                        .replaceFirst("0", "")
                        .replaceFirst("1", "");
            }catch(Exception ex){
                System.err.println("Erro ao formatar numero: " + ex);
            }
        }
        if(identificador.equals("COORDENADA4")){
            DecimalFormat formatter = new DecimalFormat("#.000");
            try{
                retorno = formatter.format(numero);
                retorno = retorno
                        .replace(",", "");
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
        String[] identificadores = new String[]{"COORDENADA", "COORDENADA2", "COORDENADA3", "COORDENADA4"};
        double aux = lat;
        lat = Math.abs(lat);

        Boolean Coordenada2 = sharedPrefs.getBoolean("Coordenada_2", false);
        Boolean Coordenada3 = sharedPrefs.getBoolean("Coordenada_3", false);

        if(Coordenada2 == false && Coordenada3 == false){
            //para grau
            //lat = Math.abs(lat);

            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificadores[0]) + "º";

            auxSeg = Math.floor((lat - valGrau) * 60);
            valMin = Math.floor((lat - valGrau) * 60) / 59.7;
            result += formatarFloat(valMin, identificadores[1]);

            valSeg = (Math.floor((lat - valGrau - auxSeg / 60) * 3600 * 1000) / 1000) / 3599;
            result += formatarFloat(valSeg, identificadores[2]);

        }

        if(Coordenada2 == true){
            //para grau e minutos
            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificadores[0]) + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += formatarFloat(valMin, identificadores[0]) + ".";

            valSeg = (Math.floor((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000) / 60;
            result += formatarFloat(valSeg, identificadores[3]) + "'";
        }

        if(Coordenada3 == true){
            //para grau, minutos e segundos
            valGrau = Math.floor(lat);
            result = formatarFloat(valGrau, identificadores[0]) + "º";

            valMin = Math.floor((lat - valGrau) * 60);
            result += formatarFloat(valMin, identificadores[0]) + "'";

            valSeg = Math.round((lat - valGrau - valMin / 60) * 3600 * 1000) / 1000;
            result += formatarFloat(valSeg, identificadores[0]) + "''";
        }

        //testando se a coordenada original é positiva ao negativa
        if(aux <= 0){
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
        //add um novo marcador do tipo 1
        maker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marcador aqui"));
    }

    @Override
    public void onPause(){
        //usando na tela histórico
        //enviando dados para a sharedpreferences
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
        //ligano/desligando o tráfego
        mMap.setTrafficEnabled(trafego);

        //alterando o tipo do mapa
       if(tipo==true) {
           mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
       }
       if(tipo==false) {
           mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
       }
    }
}