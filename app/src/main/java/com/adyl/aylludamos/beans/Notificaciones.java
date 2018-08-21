package com.adyl.aylludamos.beans;

import java.io.Serializable;

public class Notificaciones implements Serializable{

    private String titulo;
    private String descripcion;
    private String plataforma;
    private String fecha;
    private int idIncidencia;

    public Notificaciones() {
    }

    public Notificaciones(String titulo, String descripcion, String plataforma, String fecha, int idIncidencia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.plataforma = plataforma;
        this.fecha = fecha;
        this.idIncidencia = idIncidencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }
}
