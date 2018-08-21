package com.adyl.aylludamos.beans;

import java.io.Serializable;

/**
 * Created by LENOVO on 25/06/2018.
 */

public class Urbanizacion  implements Serializable{

    private int idUrbanizacion;
    private String descripcion;
    private String territorio;

    public Urbanizacion() {
    }

    public Urbanizacion(int idUrbanizacion, String descripcion, String territorio) {
        this.idUrbanizacion = idUrbanizacion;
        this.descripcion = descripcion;
        this.territorio = territorio;
    }

    public int getIdUrbanizacion() {
        return idUrbanizacion;
    }

    public void setIdUrbanizacion(int idUrbanizacion) {
        this.idUrbanizacion = idUrbanizacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTerritorio() {
        return territorio;
    }

    public void setTerritorio(String territorio) {
        this.territorio = territorio;
    }

    public String toString(){
        return descripcion;
    }
}
