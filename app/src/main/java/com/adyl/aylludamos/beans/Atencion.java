package com.adyl.aylludamos.beans;

public class Atencion {

    private int idAtencion;
    private String descripcion;
    private String reportado;

    public Atencion() {
    }

    public Atencion(int idAtencion, String descripcion, String reportado) {
        this.idAtencion = idAtencion;
        this.descripcion = descripcion;
        this.reportado = reportado;
    }

    public int getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(int idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReportado() {
        return reportado;
    }

    public void setReportado(String reportado) {
        this.reportado = reportado;
    }
}
