package com.adyl.aylludamos.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.adyl.aylludamos.MainActivity;
import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Coordenadas;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {
    WebServices webServices;
    Incidente incidente;
    double latitud=0;
    double longitud=0;
    String polyline;
    private List<Incidente> incidenteList=new ArrayList<>();
    private List<LatLng> latLngList;
    private List<Coordenadas> coordenadasList=new ArrayList<>();
    private LatLng latLngfinal;
    GoogleMap mGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_mapa);
        ButterKnife.bind(this);
        latitud=(double) getIntent().getExtras().getDouble("latitud");
        longitud=(double) getIntent().getExtras().getDouble("longitud");
        polyline=(String) getIntent().getExtras().getString("polyline");
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
        mGoogleMap.clear();
        LatLng latLng = new LatLng(latitud,longitud);
        MarkerOptions marker = new MarkerOptions().position(
                latLng);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_eliminar));
        mGoogleMap.addMarker(marker);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        if (!polyline.equalsIgnoreCase("null")){

            try {
                JSONArray jsonArrayP=new JSONArray(polyline);
                if (Metodos.ValidarJsonArray(jsonArrayP)) {

                    for (int i = 0; i < jsonArrayP.length(); i++) {
                        JSONObject poli = jsonArrayP.getJSONObject(i);
                        JSONArray coordenadas =new JSONArray(poli.getString("coordinates"));
                        if (Metodos.ValidarJsonArray(coordenadas)) {
                            latLngList=new ArrayList<>();
                            for (int j = 0; j < coordenadas.length(); j++) {
                                JSONArray item = coordenadas.getJSONArray(j);
                                String a=item.getString(0);
                                String b=item.getString(1);
                                LatLng ln=new LatLng(item.getDouble(0),item.getDouble(1));
                                latLngList.add(ln);
                            }

                            PolylineOptions polyOptions = new PolylineOptions();
                            polyOptions.color(Color.RED);
                            polyOptions.width(5);
                            polyOptions.addAll(latLngList);
                            mGoogleMap.addPolyline(polyOptions);
                        }


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }




    @Override
    public boolean onMarkerClick(Marker marker) {
        ///aqui solo debemos poner la info nada mas
        String locAddress = marker.getTitle();
        int id =Integer.parseInt(marker.getSnippet());
        for (int i = 0; i <incidenteList.size() ; i++) {
            if (id==incidenteList.get(i).getIdIncidente()){
                ShowDialog.showDialogIncidente(MapsActivity.this,incidenteList.get(i),1);
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
