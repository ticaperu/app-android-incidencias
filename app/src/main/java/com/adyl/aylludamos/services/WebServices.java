package com.adyl.aylludamos.services;

import android.app.Activity;
import com.adyl.aylludamos.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

/**
 * Created by LENOVO on 23/06/2018.
 */

public class WebServices {
    private Activity activity;

    public WebServices(Activity activity) {
        this.activity = activity;
    }

    public RetryPolicy Parametros() {
        return new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public String Url(int tipo) {
        String url = activity.getString(R.string.url_conexion);
        //String url = "http://192.168.137.55/CIMA/Intrenet/ProyectoCima/";//activity.getString(R.string.url_conexion);
        switch (tipo) {
            case 1://incidencias
                url += activity.getString(R.string.url_incidencias);
                break;
            case 2://logeo
                url += activity.getString(R.string.url_logeo);
                break;
            case 3://listar urbanizacion
                url += activity.getString(R.string.url_urbanizacion);
                break;
            case 4://registrar usuario
                url += activity.getString(R.string.url_registrar_usuario);
                break;
            case 5://actualizar datos usuario
                url += activity.getString(R.string.url_actualizar_datos);
                break;
            case 6://actualizar password usuario
                url += activity.getString(R.string.url_actualizar_password);
                break;
            case 7://listar directoroo
                url += activity.getString(R.string.url_directorio);
                break;
            case 8://verificar estdo de persona y nivel
                url += activity.getString(R.string.url_validar);
                break;
            case 9://listar familiares
                url += activity.getString(R.string.url_familia);
                break;
            case 10://eliminar familiar
                url += activity.getString(R.string.url_familia_delte);
                break;
            case 11://registrar familiar
                url += activity.getString(R.string.url_familia_nueva);
                break;
            case 12://registrar ubicacion registrarUbicacionFamiliar
                url += activity.getString(R.string.url_registrar_ubicacion);
                break;
            case 13://listar UbicacionFamiliar
                url += activity.getString(R.string.url_listar_ubicacion);
                break;
            case 14://listar incidencias sin validadr
                url += activity.getString(R.string.url_incidencia_sinconfirmar);
                break;
            case 15://listar incidencias validadas
                url += activity.getString(R.string.url_incidencia_validadas);
                break;
            case 16://confirmar o dar falso positivo a la incidencia
                url += activity.getString(R.string.url_incidencia_responder);
                break;
            case 17://clistar incidencia por id
                url += activity.getString(R.string.url_incidencia_id);
                break;
            case 18://recuperar contraseña
                url += activity.getString(R.string.url_recuperar_contra);
                break;
            case 19://listar incidentes confirmadas y atendidad
                url += activity.getString(R.string.url_incidencia_estado);
                break;
            case 20://listar urbanizacion_
                url += activity.getString(R.string.url_urbanizacion_alcalde);
                break;

            // ANTHONY
            case 101: // listar niveles de agua
                url += activity.getString(R.string.url_nivelesagua);
                break;

            case 102: // listar tipos de obstaculizacion
                url += activity.getString(R.string.url_tiposobstaculo);
                break;

            case 103: // registrar incidencia
                url += activity.getString(R.string.url_registrar_incidencia);
                break;

            case 104: // registrar media de la incidencia
                url += activity.getString(R.string.url_registrar_media);
                break;

            case 105: // obtener la cantidad de archivos permitidos - configuracion
                url += activity.getString(R.string.url_configuraciones);
                break;

        }
        return url;
    }
}
