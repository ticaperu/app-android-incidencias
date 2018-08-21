package com.adyl.aylludamos.beans;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Multimedia implements Serializable {
    /**
     * Tipo ( "Imagen", "Video" )
     */
    int idmedia = 0;
    String tipo = "";
    String media_file = "";
    String file_name = "";
    String path_file = "";
    String strBase64 = "";
    Bitmap bitmap = null;
    int eliminar=0;   //0 es noooo y 1 es siiii

    public Multimedia() {

    }

    public String getPath_file() {
        return path_file;
    }

    public void setPath_file(String path_file) {
        this.path_file = path_file;
    }

    public int getIdmedia() {
        return idmedia;
    }

    public void setIdmedia(int idmedia) {
        this.idmedia = idmedia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMedia_file() {
        return media_file;
    }

    public void setMedia_file(String media_file) {
        this.media_file = media_file;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getStrBase64() {
        return strBase64;
    }

    public void setStrBase64(String strBase64) {
        this.strBase64 = strBase64;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getEliminar() {
        return eliminar;
    }

    public void setEliminar(int eliminar) {
        this.eliminar = eliminar;
    }
}


