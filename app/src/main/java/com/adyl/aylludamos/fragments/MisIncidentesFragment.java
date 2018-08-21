package com.adyl.aylludamos.fragments;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.PrincipalActivity;
import com.adyl.aylludamos.adapters.CardDirectorio;
import com.adyl.aylludamos.adapters.CardIncidencia;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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

/**
 * Created by LENOVO on 25/06/2018.
 */

public class MisIncidentesFragment extends Fragment {
    Persona persona=null;
    WebServices webServices;
    List<Incidente> incidenteList=new ArrayList<>();
    @BindView(R.id.rvDirectorio)
    RecyclerView rvDirectorio;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mis_incidentes, container, false);
        ButterKnife.bind(this,view);
        webServices=new WebServices(getActivity());
        persona= Select.from(Persona.class).first();
        rvDirectorio.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getActivity());
        rvDirectorio.setLayoutManager(llm);
        if (Metodos.estadoConexionWifioDatos(getActivity())){
            webservicesListarIncidente(persona);
        }
        return view;
    }

    private void webservicesListarIncidente(final Persona persona) {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Cargando");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.GET, webServices.Url(1)+"/ciudadano/"+persona.getIdPersona(), null
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
                        rvDirectorio.setAdapter(new CardIncidencia(incidenteList,getActivity(),1));
                    }
                }

            }});
    }
}
