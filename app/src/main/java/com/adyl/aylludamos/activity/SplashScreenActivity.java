package com.adyl.aylludamos.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.WebServices;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 1000;
    Persona persona=null;
    WebServices webServices;

    boolean estado=false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        webServices=new WebServices(SplashScreenActivity.this);
        //List<Persona> personaList= Select.from(Persona.class).list();
        persona=Select.from(Persona.class).first();
        /***
         *   List<Persona> personaList= Select.from(Persona.class).list();
         if (Metodos.VerificarLista(personaList)){
         persona=personaList.get(0);
         if (persona!=null){
         webservicesValidar(persona);
         }else{
         IngresarPrincipal();
         }
         }else {
         IngresarPrincipal();
         }
         * */
        if (persona!=null){
            webservicesValidar(persona);
        }else{
            IngresarPrincipal();
        }

    }



    @Override
    public void onStart() {
        super.onStart();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheckLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheckLocation != PackageManager.PERMISSION_GRANTED) {
                Log.i("Loca", "No se tiene permiso para localizacion");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            } else {
                Log.i("Loca", "Se tiene permiso para localizacion");
            }
        }

    }

    private void IngresarPrincipal() {
       // if (persona != null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (persona!=null){
                        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MenuActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }else{
                        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, PrincipalActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            };
            // Simulate a long loading process on application startup.
            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
       /// }
    }


    private void webservicesValidar(final Persona persona) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(SplashScreenActivity.this, new HurlStack());

        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_idpersona),persona.getIdPersona());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(8), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //estado =response.getBoolean("status");
                  //  Toast.makeText(SplashScreenActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                    //idUsuario, int idPersona, String nombre, String ape_paterno, String ape_materno,
                    // int dni, String email, String direccion, String telefono, String password, int idUrbanizacion, String urbanizacion, int idTipo, String tipo) {

                        TipoUsuario tipo=Select.from(TipoUsuario.class).first();
                        TipoUsuario.deleteAll(TipoUsuario.class);
                        TipoUsuario tipoUsuario=new TipoUsuario(
                                response.getInt("user_persona_id"),
                                response.getString("user_state"),
                                tipo.getTipoPersona(),
                                tipo.getTipo(),
                                response.getString("user_nivel_ciudadano_name"),
                                response.getString("user_nivel_ciudadano_icono_src"),
                                response.getString("user_puntuacion_persona_puntos"),
                                response.getString("user_incidencias_registradas"),
                                response.getString("user_incidencias_atendidas"),
                                response.getString("user_incidencias_no_atendidas")
                        );
                        tipoUsuario.save();



                } catch (JSONException error) {
                    error.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        jsonObjectRequest_logeo.setRetryPolicy(webServices.Parametros());
        jsonObjectRequest_logeo.setShouldCache(false);
        requestQueue.add(jsonObjectRequest_logeo);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                Log.e("token",Metodos.TokenFirebase());
                Log.e("persona",persona.getIdPersona()+"");
                IngresarPrincipal();
            }


        });
    }

}
