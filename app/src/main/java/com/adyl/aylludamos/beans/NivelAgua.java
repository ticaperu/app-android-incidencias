package com.adyl.aylludamos.beans;


public class NivelAgua {
    int idNivel = 0;
    String descripcion = "";
    // altura de cada elemento por defecto 40
    int alturaItem = 50;

    // altura normal, es decir cuando avanza de uno en uno
    int alturaPorNivel = 50;
    // guardo tb su progreso del seekbar
    // "1" - "2" - "3" - "4,5" - "6,7"
    String progreso = "";
    // resource
    int imagenBg = 0;
    // string - url
    String img_url = "";

    public NivelAgua() {
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getImagenBg() {
        return imagenBg;
    }

    public void setImagenBg(int imagenBg) {
        this.imagenBg = imagenBg;
    }

    public String getProgreso() {
        return progreso;
    }

    public void setProgreso(String progreso) {
        this.progreso = progreso;
    }

    public int getAlturaPorNivel() {
        return alturaPorNivel;
    }

    public void setAlturaPorNivel(int alturaPorNivel) {
        this.alturaPorNivel = alturaPorNivel;
    }

    public int getAlturaItem() {
        return alturaItem;
    }

    public void setAlturaItem(int alturaItem) {
        this.alturaItem = alturaItem;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
