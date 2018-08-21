package com.adyl.aylludamos.beans;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Coordenadas {

    private int idCoordenada;
    private List<LatLng> latLngList;

    public Coordenadas(int idCoordenada, List<LatLng> latLngList) {
        this.idCoordenada = idCoordenada;
        this.latLngList = latLngList;
    }

    public int getIdCoordenada() {
        return idCoordenada;
    }

    public void setIdCoordenada(int idCoordenada) {
        this.idCoordenada = idCoordenada;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }
}
