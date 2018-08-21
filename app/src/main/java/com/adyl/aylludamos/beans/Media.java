package com.adyl.aylludamos.beans;

public class Media {

    private int tipo;
    private String descripcion;
    private String url;

    public Media() {
    }

    public Media(int tipo, String descripcion, String url) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.url = url;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
