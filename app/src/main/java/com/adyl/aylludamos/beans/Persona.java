package com.adyl.aylludamos.beans;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by LENOVO on 25/06/2018.
 */

public class Persona extends SugarRecord implements Serializable {

    private  int idUsuario;
    private int idPersona;
    private String nombre;
    private String ape_paterno;
    private String ape_materno;
    private int dni;
    private String email;
    private String direccion;
    private String telefono;
    private String password;
    private int idUrbanizacion; //comno
    private String urbanizacion; //del como
    private int idTipo; //2
    private String tipo; //ciudadani

    private int idTerritVecinal = 0;
    private String descTerritVecinal = "";


    public Persona() {
    }

    public Persona(int idUsuario, int idPersona, String nombre, String ape_paterno, String ape_materno, int dni, String email, String direccion, String telefono, String password, int idUrbanizacion, String urbanizacion, int idTipo, String tipo) {
        this.idUsuario = idUsuario;
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.ape_paterno = ape_paterno;
        this.ape_materno = ape_materno;
        this.dni = dni;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.password = password;
        this.idUrbanizacion = idUrbanizacion;
        this.urbanizacion = urbanizacion;
        this.idTipo = idTipo;
        this.tipo = tipo;
    }


    public int getIdTerritVecinal() {
        return idTerritVecinal;
    }

    public void setIdTerritVecinal(int idTerritVecinal) {
        this.idTerritVecinal = idTerritVecinal;
    }

    public String getDescTerritVecinal() {
        return descTerritVecinal;
    }

    public void setDescTerritVecinal(String descTerritVecinal) {
        this.descTerritVecinal = descTerritVecinal;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApe_paterno() {
        return ape_paterno;
    }

    public void setApe_paterno(String ape_paterno) {
        this.ape_paterno = ape_paterno;
    }

    public String getApe_materno() {
        return ape_materno;
    }

    public void setApe_materno(String ape_materno) {
        this.ape_materno = ape_materno;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdUrbanizacion() {
        return idUrbanizacion;
    }

    public void setIdUrbanizacion(int idUrbanizacion) {
        this.idUrbanizacion = idUrbanizacion;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
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
}
