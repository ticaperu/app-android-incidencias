package com.adyl.aylludamos.fragments;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.PrincipalActivity;
import com.adyl.aylludamos.activity.RegistrarActivity;
import com.adyl.aylludamos.adapters.CardDirectorio;
import com.adyl.aylludamos.beans.Directorio;
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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class DirectorioFragment extends Fragment {

    WebServices webServices;
    List<Directorio>  directorioList =new ArrayList<>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @BindView(R.id.rvDirectorio)  RecyclerView rvDirectorio;
    /*@BindView(R.id.tvNombre) TextView tvNombre;
    @BindView(R.id.tvDireccion) TextView tvDireccion;
    @BindView(R.id.ibTelefono) ImageButton ibTelefono;*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_directorio, container, false);
        ButterKnife.bind(this,view);
        webServices=new WebServices(getActivity());
        rvDirectorio.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getActivity());
        rvDirectorio.setLayoutManager(llm);
        webservicesListarIncidente();
        return view;
    }

    private void webservicesListarIncidente() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(7), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes =response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            Directorio directorio= new Directorio(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("nombre"),
                                    jsonObject.getString("direccion") ,
                                    jsonObject.getString("telefono"),
                                    "Activo"
                            );
                            directorioList.add(directorio);
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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (Metodos.VerificarLista(directorioList)){
                        rvDirectorio.setAdapter(new CardDirectorio(directorioList,getActivity()));
                    }
                }
            }


        });
    }
}
