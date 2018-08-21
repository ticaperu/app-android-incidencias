package com.adyl.aylludamos.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.MenuActivity;
import com.adyl.aylludamos.activity.PrincipalActivity;
import com.adyl.aylludamos.activity.SplashScreenActivity;
import com.adyl.aylludamos.adapters.CardIncidencia;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.services.ConexionInterna;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.adyl.aylludamos.utils.Maps.TouchableMapFragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncidentesFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {

    WebServices webServices;
    List<Incidente> incidenteList=new ArrayList<>();
    LatLng position;
    TouchableMapFragment mapFragment;
    GoogleMap mGoogleMap;
    private TipoUsuario tipoUsuario=null;
    private Persona persona=null;
    private GoogleApiClient client;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = new LatLng(0, 0);
        webServices=new WebServices(getActivity());
        tipoUsuario= ConexionInterna.tipo();
        persona= Select.from(Persona.class).first();

    }

    @BindView(R.id.btnRegistrar)
    Button btnRegistrar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = null;
        if (googleServicesAvailable()) {
            view = inflater.inflate(R.layout.fragment_incidentes, container, false);
            ButterKnife.bind(this,view);
            intMap();
            if (Metodos.estadoConexionWifioDatos(getActivity())){
                webservicesListarIncidente();
            }
            client = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();
            if (tipoUsuario!=null){
                //Se agrega esta validación ya que tanto el ciudadano como el alcalde vecinal y comite de gestión pueden registrar incidencias
                if (tipoUsuario.getTipoPersona()==2 || tipoUsuario.getTipoPersona()==4 || tipoUsuario.getTipoPersona()==5){
                    btnRegistrar.setVisibility(View.VISIBLE);
                }else{
                    btnRegistrar.setVisibility(View.GONE);
                }
            }else{
                btnRegistrar.setVisibility(View.GONE);
            }

        }
        return view;
    }

    private void intMap() {
        mapFragment = (TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment = (TouchableMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cant connect is play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);

    }

    @OnClick(R.id.btnRegistrar)
    void onclick(Button button){
        if (tipoUsuario!=null){
            if (tipoUsuario.getEstado().equalsIgnoreCase("Activo")){
                Intent intent =new Intent(getActivity(), MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(getString(R.string.parametro_evento),"Nuevo Incidente");
                startActivity(intent);
                getActivity().finish();
            }else{
                webservicesValidar(persona);
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String locAddress = marker.getTitle();
        int id =Integer.parseInt(marker.getSnippet());
        for (int i = 0; i <incidenteList.size() ; i++) {
            if (id==incidenteList.get(i).getIdIncidente()){
                int valor= (incidenteList.get(i).getIdEstado()==2)?3 :1;
                ShowDialog.showDialogIncidente(getActivity(),incidenteList.get(i),valor);
                break;

            }
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }


    private void webservicesListarIncidente() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Cargando");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.GET, webServices.Url(19), null
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
                    if (Metodos.VerificarLista(incidenteList)){
                        mGoogleMap.setOnMarkerClickListener(IncidentesFragment.this);
                    }
                    try {
                        geoLocate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }});
    }

    public void geoLocate() throws IOException {
        String location =  "Trujillo,Peru";

        if (!location.equals("")) {
            Geocoder gc = new Geocoder(getActivity());
            List<Address> list = gc.getFromLocationName(location, 1);

            if (list.size() > 0) {
                Address address = list.get(0);
                String locality = address.getAddressLine(0);

                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                mGoogleMap.animateCamera(update);
            } else {
                Toast.makeText(getActivity(), "Dirección no encontrada.\nInténtelo de nuevo o busque manualmente", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void webservicesValidar(final Persona persona) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Verificando estado");
        progressDialog.show();
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
                    TipoUsuario tipoUsu=new TipoUsuario(
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
                    tipoUsu.save();
                    tipoUsuario=tipoUsu;



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
                    if (tipoUsuario.getEstado().equalsIgnoreCase("Activo")){
                        Intent intent =new Intent(getActivity(), MenuActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(getString(R.string.parametro_evento),"Nuevo Incidente");
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(),"Funcionalidad inhabilitada",Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
    }

}
