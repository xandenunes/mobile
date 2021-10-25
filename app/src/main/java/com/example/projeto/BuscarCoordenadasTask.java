package com.example.projeto;

import static androidx.core.content.ContextCompat.getSystemService;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

public class BuscarCoordenadasTask extends AsyncTask<Void, Void, Void> {
    private LocationManager locationManager;
    private Location userLocation;
    private GoogleMap mMap;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (ActivityCompat.checkSelfPermission(null, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(null, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, locationListener);
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0.0f, locationListener);
            }
        } else {
            // Nenhum provedor de localização, cancela requisição
            cancel(true);
        }
    }


    @Override
    protected Void doInBackground(Void... params) {
        while (userLocation == null) {}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        locationManager.removeUpdates(locationListener);
        LatLng ponto = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());


        // Adiciona localização do usuário no mapa, com novo ícone
        mMap.addMarker(new MarkerOptions().position(ponto).icon(BitmapDescriptorFactory.fromResource(R.drawable.makerlocation)));
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        locationManager.removeUpdates(locationListener);
    }
}
