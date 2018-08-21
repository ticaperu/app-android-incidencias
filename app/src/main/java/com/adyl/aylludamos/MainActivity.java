package com.adyl.aylludamos;

import android.*;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {
    WebServices webServices;

    private List<Incidente> incidenteList=new ArrayList<>();
    private LatLng latLngfinal;
    GoogleMap mGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        webServices=new WebServices(MainActivity.this);
        servicies_listarIncidente();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("INCIDENCIAS");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);


    }

    private void servicies_listarIncidente() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, new HurlStack());
        /*final ProgressDialog progressDialog = constantes
                .createProgressDialog("Descargando Eventos");
        progressDialog.show();*/
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(1), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes =response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            Incidente incidente= new Incidente();
                            LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("altitud")),Double.parseDouble(jsonObject.getString("latitud")));
                            MarkerOptions marker = new MarkerOptions().position(
                                    latLng)
                                    .title("ChoferUrgent ").snippet(""+jsonObject.getInt("incidente_id"));
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_eliminar));
                            latLngfinal=latLng;
                            mGoogleMap.addMarker(marker);
                            incidenteList.add(incidente);
                        }
                    }
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
        jsonObjectRequest_incidencia.setRetryPolicy(webServices.Parametros());
        jsonObjectRequest_incidencia.setShouldCache(false);
        requestQueue.add(jsonObjectRequest_incidencia);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (Metodos.VerificarLista(incidenteList)){
                   // CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngfinal, 19);
                    //mGoogleMap.animateCamera(update);
                     mGoogleMap.setOnMarkerClickListener(MainActivity.this);
                    // mGoogleMap.setOnInfoWindowClickListener(MainActivity.this);

                }
            }


        });
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String locAddress = marker.getTitle();
        int id =Integer.parseInt(marker.getSnippet());
        for (int i = 0; i <incidenteList.size() ; i++) {
            if (id==incidenteList.get(i).getIdIncidente()){
                ShowDialog.showDialogIncidente(MainActivity.this,incidenteList.get(i),1);
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


}
