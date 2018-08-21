package com.adyl.aylludamos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.CardAtencion;
import com.adyl.aylludamos.adapters.CardDetalleIncidenciaM;
import com.adyl.aylludamos.beans.Atencion;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Multimedia;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ValidarIncidenteActivity extends AppCompatActivity {
    Incidente incidente = null;
    Persona persona=null;
    WebServices webServices;
    boolean estado=false;//validar si registro  LA RESPUESTA
    LinearLayoutManager horizontalLayoutManagaer;
    List<Multimedia> mediaLista =new ArrayList<>();

    @BindView(R.id.toolbar) Toolbar toolbar;

  //  @BindView(R.id.tipo) TextView tipo;
    @BindView(R.id.tvFecha) TextView tvFecha;
    @BindView(R.id.tvUsuario) TextView tvUsuario;
    @BindView(R.id.tvDescripcion) TextView tvDescripcion;
    @BindView(R.id.tvNivel) TextView tvNivel;
    @BindView(R.id.tvDireccion) TextView tvDireccion;
    @BindView(R.id.tvUrbanizacion) TextView tvUrbanizacion;
    @BindView(R.id.tvTerritorio) TextView tvTerritorio;
    @BindView(R.id.tvDetalleNivel) TextView tvDetalleNivel;
    @BindView(R.id.tvDetalleObst) TextView tvDetalleObst;
    @BindView(R.id.cvFoto) CircleImageView cvFoto;
    @BindView(R.id.btnConfirmar) Button btnConfirmar;
    @BindView(R.id.btnRechazar) Button btnRechazar;
    @BindView(R.id.rvMedia)
    RecyclerView rvMedia;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_incidencia);
        webServices=new WebServices(ValidarIncidenteActivity.this);
        persona= Select.from(Persona.class).first();
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("VALIDAR");
        }

        if (getIntent().getExtras()!=null){
            incidente=(Incidente) getIntent().getExtras().getSerializable("incidente");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        horizontalLayoutManagaer= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMedia.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvMedia.setLayoutManager(llm);

        cvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (incidente!=null){
            webservicesListarIncidenciaId(incidente);
        }

        cvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ValidarIncidenteActivity.this,MapsActivity.class);
                intent.putExtra("latitud",incidente.getLatitud());
                intent.putExtra("longitud",incidente.getLongitud());
                intent.putExtra("polyline",incidente.getPolilyne());
                startActivity(intent);
            }
        });
        //llenarDatos();
    }

    public  void  llenarDatos(Incidente incidente){
        //llSubir.setVisibility(View.GONE);
        //llEnviar.setVisibility(View.GONE);
        if (incidente!=null){
            tvFecha.setText( Metodos.formatoFecha(incidente.getFecha()));
            tvDireccion.setText(incidente.getDireccion());
            tvDescripcion.setText(incidente.getDireccion());
            tvUrbanizacion.setText(incidente.getUrbanizacion());
            //  tvNivel.setText(incidente.get());
            tvTerritorio.setText(incidente.getTerritorio());

            try {
                JSONObject ciudadano=new JSONObject(incidente.getCiudadano());
                JSONObject detalle=new JSONObject(incidente.getDetalle());
                tvUsuario.setText(ciudadano.getString("nombres"));
                //tvTelefono.setText(ciudadano.getString("telefono"));
                if (incidente.getIdTipo()==1){
                    tvDetalleObst.setVisibility(View.GONE);
                    tvNivel.setText(detalle.getString("nivel_agua"));
                }else{
                    tvDetalleNivel.setVisibility(View.GONE);
                    tvNivel.setText(detalle.getString("tipo_obstaculo"));
                }

                if(!incidente.getMedia().equalsIgnoreCase("null")){
                    JSONArray jsonArrayM=new JSONArray(incidente.getMedia());
                    if (Metodos.ValidarJsonArray(jsonArrayM)) {
                        for (int i = 0; i < jsonArrayM.length(); i++) {
                            JSONObject jsmedia = jsonArrayM.getJSONObject(i);
                           /* Media media=new Media(
                                    jsmedia.getInt("id"),
                                    jsmedia.getString("tipo_media"),
                                    jsmedia.getString("incidente_media_url")
                            );*/

                            Multimedia media = new Multimedia();
                            media.setIdmedia(jsmedia.getInt("id"));
                            media.setTipo(jsmedia.getString("tipo_media"));
                            media.setPath_file(jsmedia.getString("incidente_media_url"));

                            mediaLista.add(media);
                        }


                        // mediaList.add(medias);
                        rvMedia.setAdapter(new CardDetalleIncidenciaM(mediaLista,ValidarIncidenteActivity.this,0));
                        rvMedia.setLayoutManager(horizontalLayoutManagaer);
                    }
                }

               /* if(!incidente.getAtencion().equalsIgnoreCase("null")){
                    JSONArray jsonArrayA=new JSONArray(incidente.getAtencion());
                    if (Metodos.ValidarJsonArray(jsonArrayA)) {
                        for (int i = 0; i < jsonArrayA.length(); i++) {
                            JSONObject jsmedia = jsonArrayA.getJSONObject(i);
                            Atencion atenion= new Atencion(
                                    jsmedia.getInt("id"),
                                    jsmedia.getString("descripcion"),
                                    jsmedia.getString("persona_atencion")
                            );
                            atenionList.add(atenion);
                        }
                    }
                }

                if (Metodos.VerificarLista(atenionList)){
                    rvAtencion.setAdapter(new CardAtencion(atenionList,DetalleIncidActivity.this));
                }*/


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
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

    @OnClick({
            R.id.cvFoto,
            R.id.btnConfirmar,
            R.id.btnRechazar
    })
    void  onclick(View view){
        switch(view.getId()) {
            case R.id.cvFoto:

                break;

            case R.id.btnConfirmar:
                //Aquí confirmamos que el incidente es cierto
                webservicesValidarIncidente(2);
                break;
            case R.id.btnRechazar:
                //Aquí damos como falso positivo al incidente
                webservicesValidarIncidente(3);
                break;
        }
    }

    private void webservicesListarIncidenciaId(final Incidente incid) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(ValidarIncidenteActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(ValidarIncidenteActivity.this,"Cargando datos");
        progressDialog.show();


        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.GET, webServices.Url(17)+incid.getIdIncidente(), null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsincidencia=new JSONObject(response.getString("incidencia"));
                    JSONObject jsdata=new JSONObject(jsincidencia.getString("data"));

                    incidente= new Incidente(
                            jsdata.getInt("id"),
                            jsdata.getString("fecha"),
                            jsdata.getString("descripcion"),
                            jsdata.getString("direccion"),
                            jsdata.getDouble("latitud"),
                            jsdata.getDouble("longitud"),
                            jsdata.getInt("tipo_incidente_id"),
                            jsdata.getString("tipo_incidente"),
                            jsdata.getInt("estado_incidente_id"),
                            jsdata.getString("estado_incidente_descripcion"),
                            jsincidencia.getString("ciudadano"),
                            jsincidencia.getString("detalle_incidente"),
                            jsincidencia.getString("media"),
                            jsincidencia.getString("atencion"),
                            jsdata.getString("urbanizacion_nombre"),
                            jsdata.getString("territorio_vecinal_nombre"),
                            jsincidencia.getString("polilyne")
                    );


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
                    llenarDatos(incidente);
                }
            }


        });
    }

    private void webservicesValidarIncidente(final int valor) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(ValidarIncidenteActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(ValidarIncidenteActivity.this,"Validando");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id",incidente.getIdIncidente());
            object.put("estado_incidente_id",valor);
            object.put("persona_id_validator",persona.getIdPersona());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(16), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("success");
                    Toast.makeText(ValidarIncidenteActivity.this,response.getString("message"),Toast.LENGTH_SHORT).show();
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
                    if (estado){
                        estado=false;
                        Intent a = new Intent(ValidarIncidenteActivity.this,MenuActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        a.putExtra(getString(R.string.parametro_evento),"Validar Incidente");
                        startActivity(a);
                    }
                }
            }


        });
    }
}
