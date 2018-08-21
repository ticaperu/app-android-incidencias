package com.adyl.aylludamos.beans;

public class Direccion {

    private int idDireccion;
    private String fecha;
    private String descripcion;
    private String latitud;
    private String longitud;

    public Direccion() {
    }

    public Direccion(int idDireccion, String fecha, String descripcion, String latitud, String longitud) {
        this.idDireccion = idDireccion;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
