package com.adyl.aylludamos.beans;

import com.orm.SugarRecord;

public class TipoUsuario extends SugarRecord {

    private int idPersona;
    private String estado;
    private int tipoPersona;
    private String tipo;
    private String nivel;
    private String imagen;
    private String puntuacion;
    private String inRegistradas;
    private String inAtendidas;
    private String inNoAtendidas;

    public TipoUsuario() {
    }

    public TipoUsuario(int idPersona, String estado, int tipoPersona, String tipo, String nivel, String imagen, String puntuacion) {
        this.idPersona = idPersona;
        this.estado = estado;
        this.tipoPersona = tipoPersona;
        this.tipo = tipo;
        this.nivel = nivel;
        this.imagen = imagen;
        this.puntuacion = puntuacion;
    }

    public TipoUsuario(int idPersona, String estado, int tipoPersona, String tipo, String nivel, String imagen, String puntuacion, String inRegistradas, String inAtendidas, String inNoAtendidas) {
        this.idPersona = idPersona;
        this.estado = estado;
        this.tipoPersona = tipoPersona;
        this.tipo = tipo;
        this.nivel = nivel;
        this.imagen = imagen;
        this.puntuacion = puntuacion;
        this.inRegistradas = inRegistradas;
        this.inAtendidas = inAtendidas;
        this.inNoAtendidas = inNoAtendidas;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(int tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getInRegistradas() {
        return inRegistradas;
    }

    public void setInRegistradas(String inRegistradas) {
        this.inRegistradas = inRegistradas;
    }

    public String getInAtendidas() {
        return inAtendidas;
    }

    public void setInAtendidas(String inAtendidas) {
        this.inAtendidas = inAtendidas;
    }

    public String getInNoAtendidas() {
        return inNoAtendidas;
    }

    public void setInNoAtendidas(String inNoAtendidas) {
        this.inNoAtendidas = inNoAtendidas;
    }
}
