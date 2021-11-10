package com.example.projeto;

public class Local {
    int codigo;
    double latitude, longitude;
    String data;

    public void Local(){
    }

    public void Local(int _codigo, double _latitude, double _longitude, String _data){
        this.codigo = _codigo;
        this.latitude = _latitude;
        this.longitude = _longitude;
        this.data = _data;
    }

    public void Local(double _latitude, double _longitude, String _data){
        this.latitude = _latitude;
        this.longitude = _longitude;
        this.data = _data;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
