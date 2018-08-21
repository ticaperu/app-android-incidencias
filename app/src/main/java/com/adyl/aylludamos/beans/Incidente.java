package com.adyl.aylludamos.beans;

import android.os.Bundle;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LENOVO on 23/06/2018.
 */

public class Incidente extends SugarRecord implements Serializable{

    private int idIncidente;
    private String fecha;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private int idTipo;
    private String tipo;
    private int idEstado;
    private String estado;

    private String ciudadano;
    private String detalle;
    private String media;
    private String atencion;

    private String urbanizacion;
    private String territorio;

    private String polilyne;

    @Ignore
    private double altitud;
    @Ignore
    private double idUrb;
    @Ignore
    private String creador;
    @Ignore
    private int idNivelAgua;
    @Ignore
    private int tipoObstaculo;
    @Ignore
    private List<Multimedia>ListMultimedia;


    public Incidente() {
    }

    public Incidente(int idIncidente, String fecha, String descripcion, String direccion, double latitud, double longitud, int idTipo, String tipo, int idEstado, String estado, String ciudadano, String detalle, String media, String atencion, String urbanizacion, String territorio,String polilyne) {
        this.idIncidente = idIncidente;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.idTipo = idTipo;
        this.tipo = tipo;
        this.idEstado = idEstado;
        this.estado = estado;
        this.ciudadano = ciudadano;
        this.detalle = detalle;
        this.media = media;
        this.atencion = atencion;
        this.urbanizacion=urbanizacion;
        this.territorio=territorio;
        this.polilyne=polilyne;
    }

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudadano() {
        return ciudadano;
    }

    public void setCiudadano(String ciudadano) {
        this.ciudadano = ciudadano;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getAtencion() {
        return atencion;
    }

    public void setAtencion(String atencion) {
        this.atencion = atencion;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getTerritorio() {
        return territorio;
    }

    public void setTerritorio(String territorio) {
        this.territorio = territorio;
    }

    public double getAltitud() {
        return altitud;
    }

    public void setAltitud(double altitud) {
        this.altitud = altitud;
    }

    public double getIdUrb() {
        return idUrb;
    }

    public void setIdUrb(double idUrb) {
        this.idUrb = idUrb;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public int getIdNivelAgua() {
        return idNivelAgua;
    }

    public void setIdNivelAgua(int idNivelAgua) {
        this.idNivelAgua = idNivelAgua;
    }

    public int getTipoObstaculo() {
        return tipoObstaculo;
    }

    public void setTipoObstaculo(int tipoObstaculo) {
        this.tipoObstaculo = tipoObstaculo;
    }

    public List<Multimedia> getListMultimedia() {
        return ListMultimedia;
    }

    public void setListMultimedia(List<Multimedia> listMultimedia) {
        ListMultimedia = listMultimedia;
    }

    public String getPolilyne() {
        return polilyne;
    }

    public void setPolilyne(String polilyne) {
        this.polilyne = polilyne;
    }
}
