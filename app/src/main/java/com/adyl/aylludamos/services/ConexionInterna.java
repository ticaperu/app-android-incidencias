package com.adyl.aylludamos.services;

import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.orm.query.Select;

import java.util.List;

public class ConexionInterna {

    public static  Persona ultimoIngreso() {
        Persona persona = null;
        try {

            List<Persona> personaList = Select.from(Persona.class).list();
            if (personaList != null) {
                if (personaList.size() > 0) {
                    persona = personaList.get(0);
                }else{
                    persona=null;
                }
            } else {
                persona = null;
            }
        } catch (Exception error) {
            persona = null;
            error.printStackTrace();
        }
        return persona;
    }

    public static TipoUsuario tipo() {
        TipoUsuario tipo= null;
        try {

            List<TipoUsuario> tipoUsuarioList = Select.from(TipoUsuario.class).list();
            if (tipoUsuarioList != null) {
                if (tipoUsuarioList.size() > 0) {
                    tipo = tipoUsuarioList.get(0);
                }else{
                    tipo=null;
                }
            } else {
                tipo = null;
            }
        } catch (Exception error) {
            tipo = null;
            error.printStackTrace();
        }
        return tipo;
    }

    public static  void registrarPersona(Persona persona){
        Persona.deleteAll(Persona.class);
        persona.save();
    }


}
