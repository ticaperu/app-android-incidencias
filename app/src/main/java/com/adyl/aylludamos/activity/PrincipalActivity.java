package com.adyl.aylludamos.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.*;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.adyl.aylludamos.utils.Maps.TouchableMapFragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by LENOVO on 24/06/2018.
 */

public class PrincipalActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener
        {
// GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener
    WebServices webServices;

    private List<Incidente> incidenteList=new ArrayList<>();
    GoogleMap mGoogleMap;
    TouchableMapFragment mapFragment;
    private GoogleApiClient client;
    boolean recu=false;
    /**
     * Método heredado por la clase AppCompatActivity
     * Funcionalidad: activar la interfaz y llamar los View implementados en el layout.
     * También hacer una referencia de las view y la invocación de los eventos de cada view.
     *
     * @param savedInstanceState Objeto de la clase Bundle.
     */
    @BindView(R.id.tvOlvido) TextView tvOlvido;
    @BindView(R.id.tvRegistrar) TextView tvRegistrar;
    @BindView(R.id.btnLogin) Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        ButterKnife.bind(this);
        webServices=new WebServices(PrincipalActivity.this);
        webservicesListarIncidente();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        tvOlvido.setPaintFlags(tvOlvido.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvRegistrar.setPaintFlags(tvRegistrar.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("INCIDENCIAS");
        }
        if (googleServicesAvailable()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrincipalActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 225);

            }
        }

        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();

        List<Persona> personas=Select.from(Persona.class).list();
        if (personas.size()>0){
            Intent registrar=new Intent(PrincipalActivity.this,MenuActivity.class);
            startActivity(registrar);
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            int permissionCheckLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheckLocation != PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG", "No se tiene permiso para localizacion");

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            } else {
                Log.i("TAG", "Se tiene permiso para localizacion");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);

    }

    @OnClick({
            R.id.tvOlvido,
            R.id.tvRegistrar,
            R.id.btnLogin
    })
    void  onclick(View view){
        switch(view.getId()) {
            case R.id.tvOlvido:
                showDialogIncidente(PrincipalActivity.this);
                break;

            case R.id.tvRegistrar:
                Intent registrar=new Intent(PrincipalActivity.this,RegistrarActivity.class);
                startActivity(registrar);
                finish();
                break;
            case R.id.btnLogin:
                Intent login=new  Intent(PrincipalActivity.this,LogeoActivity.class);
                startActivity(login);
                finish();
                break;
        }
    }






    @Override
    public boolean onMarkerClick(Marker marker) {
        String locAddress = marker.getTitle();
        int id =Integer.parseInt(marker.getSnippet());
        for (int i = 0; i <incidenteList.size() ; i++) {
            if (id==incidenteList.get(i).getIdIncidente()){
                ShowDialog.showDialogIncidente(PrincipalActivity.this,incidenteList.get(i),1);
                break;

            }
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
            if (isAvailable == ConnectionResult.SUCCESS) {
                return true;
            } else if (api.isUserResolvableError(isAvailable)) {
                Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
                dialog.show();
            } else {
                Toast.makeText(this, "Cant connect is play services", Toast.LENGTH_LONG).show();
            }
        return false;
    }

    private void intMap() {
        mapFragment = (TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(PrincipalActivity.this);
    }

    public void geoLocate() throws IOException {
                String location =  "Trujillo,Peru";

                if (!location.equals("")) {
                    Geocoder gc = new Geocoder(this);
                    List<Address> list = gc.getFromLocationName(location, 1);

                    if (list.size() > 0) {
                        Address address = list.get(0);
                        String locality = address.getAddressLine(0);

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 11);
                        mGoogleMap.animateCamera(update);
                    } else {
                        Toast.makeText(this, "Dirección no encontrada.\nInténtelo de nuevo o busque manualmente", Toast.LENGTH_SHORT).show();
                    }
                }
    }


     private void webservicesListarIncidente() {

        RequestQueue requestQueue = Volley.newRequestQueue(PrincipalActivity.this, new HurlStack());
        /*final ProgressDialog progressDialog = ShowDialog.createProgressDialog(PrincipalActivity.this,"Iniciar Sessión");
        progressDialog.show();*/

        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.GET, webServices.Url(1), null
            , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray incidenciaJson=new JSONArray(response.getString("incidencia"));
                    if (Metodos.ValidarJsonArray(incidenciaJson)){
                        for (int i = 0; i <incidenciaJson.length() ; i++) {
                            JSONObject jsonObject= incidenciaJson.getJSONObject(i);

                            JSONObject data=new JSONObject(jsonObject.getString("data"));
                            //JSONObject objetodata=data.getJSONObject(0);
                            Incidente incidente= new Incidente(
                                    data.getInt("id"),
                                    data.getString("fecha"),
                                    data.getString("descripcion"),
                                    data.getString("direccion"),
                                    data.getDouble("latitud"),
                                    data.getDouble("longitud"),
                                    data.getInt("tipo_incidente_id"),
                                    data.getString("tipo_incidente"),
                                    data.getInt("estado_incidente_id"),
                                    data.getString("estado_incidente_descripcion"),
                                    jsonObject.getString("ciudadano"),
                                    jsonObject.getString("detalle_incidente"),
                                    jsonObject.getString("media"),
                                    jsonObject.getString("atencion"),
                                    data.getString("urbanizacion_nombre"),
                                    data.getString("territorio_vecinal_nombre"),
                                    jsonObject.getString("polilyne")
                            );

                            LatLng latLng = new LatLng(Double.parseDouble(data.getString("latitud")),Double.parseDouble(data.getString("longitud")));
                            MarkerOptions marker = new MarkerOptions().position(
                                    latLng)
                                    .title( data.getString("tipo_incidente")).snippet(""+data.getInt("id"));
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_eliminar));
                            mGoogleMap.addMarker(marker);
                            incidenteList.add(incidente);
                        }
                    }
                    /*JSONArray incidencia=new JSONArray(response.getString("data"));
                    JSONArray ciudadano=new JSONArray(response.getString("ciudadano"));
                    JSONArray detalle=new JSONArray(response.getString("detalle_incidente"));
                    JSONArray media=new JSONArray(response.getString("media"));
                    JSONArray atencion=new JSONArray(response.getString("atencion"));*/

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
                if (Metodos.VerificarLista(incidenteList)){
                    mGoogleMap.setOnMarkerClickListener(PrincipalActivity.this);
                }
                try {
                    geoLocate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
    }


    public void showDialogIncidente(final Activity activity){
                final Dialog dialog=new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_recuperar_contra);
                final TextView etCorreo=(TextView)dialog.findViewById(R.id.etCorreo);
                Button btnSolicitar=(Button)dialog.findViewById(R.id.btnSolicitar);

                btnSolicitar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Metodos.emailValidator(etCorreo.getText().toString())){
                            webservicesRecuperar(dialog,etCorreo.getText().toString());
                            etCorreo.setError(null);
                        }else{
                            etCorreo.setError("Ingrese email");
                        }
                    }
                });




                WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
                Window window=dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                lp.width=WindowManager.LayoutParams.MATCH_PARENT;
                lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

    private void webservicesRecuperar(final Dialog dialog, final String correo) {
                //final boolean[] estado = new boolean[0];
                //estado[0]=false;
                RequestQueue requestQueue = Volley.newRequestQueue(PrincipalActivity.this, new HurlStack());
                final ProgressDialog progressDialog = ShowDialog.createProgressDialog(PrincipalActivity.this,"Solicitando contraseña");
                progressDialog.show();
                JSONObject object = new JSONObject();
                try {
                    object.put(getString(R.string.parametro_service_correo),correo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(18), object
                        , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            recu =response.getBoolean("status");
                            Toast.makeText(PrincipalActivity.this,response.getString("message"),Toast.LENGTH_SHORT).show();
                            //idUsuario, int idPersona, String nombre, String ape_paterno, String ape_materno,
                            // int dni, String email, String direccion, String telefono, String password, int idUrbanizacion, String urbanizacion, int idTipo, String tipo) {



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
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            if (recu){
                                recu=false;
                                dialog.dismiss();
                            }
                        }
                    }


                });
            }
}
