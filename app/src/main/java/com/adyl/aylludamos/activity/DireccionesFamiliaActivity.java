package com.adyl.aylludamos.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.CardDirecciones;
import com.adyl.aylludamos.adapters.CardDirectorio;
import com.adyl.aylludamos.beans.Direccion;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.beans.Familia;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DireccionesFamiliaActivity extends AppCompatActivity {
    WebServices webServices;
    List<Direccion> direccionList=new ArrayList<>();
    Familia familia=null;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvDirecciones) RecyclerView rvDirecciones;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direcciones);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Direcciones");
        }


        if (getIntent().getExtras()!=null){
            familia=(Familia) getIntent().getExtras().getSerializable("familia");
        }

        webServices=new WebServices(this);
        rvDirecciones.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvDirecciones.setLayoutManager(llm);

        if (Metodos.estadoConexionWifioDatos(this) && familia!=null){
            webservicesListarDirecciones(familia);
        }
        setSupportActionBar(toolbar);
    }


    private void webservicesListarDirecciones(final Familia familia) {
        RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(this,"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(13)+familia.getIdPersona(), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if (Metodos.ValidarJsonArray(response)){
                        for (int i = 0; i <response.length() ; i++) {
                            JSONObject jsonObject= response.getJSONObject(i);
                            Direccion direccion = new Direccion(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("fecha"),
                                    jsonObject.getString("descripcion"),
                                    jsonObject.getString("latitude"),
                                    jsonObject.getString("longitude")

                            );

                            direccionList.add(direccion);
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
                    if (Metodos.VerificarLista(direccionList)){
                        rvDirecciones.setAdapter(new CardDirecciones(direccionList,DireccionesFamiliaActivity.this));
                    }
                }
            }


        });
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
