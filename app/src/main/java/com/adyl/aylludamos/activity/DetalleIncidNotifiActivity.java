package com.adyl.aylludamos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.CardAtencion;
import com.adyl.aylludamos.adapters.CardDetalleIncidencia;
import com.adyl.aylludamos.beans.Atencion;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Media;
import com.adyl.aylludamos.beans.Notificaciones;
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

public class DetalleIncidNotifiActivity extends AppCompatActivity {
    Incidente incidente = null;
    Notificaciones notificaciones=null;
    Persona persona=null;
    WebServices webServices;
    List<Media> mediaList =new ArrayList<>();
    List<Atencion> atenionList=new ArrayList<>();
    boolean estado=false;//validar si registro  LA RESPUESTA
    LinearLayoutManager horizontalLayoutManagaer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvFecha)  TextView tvFecha;
    @BindView(R.id.tvDireccion)  TextView tvDireccion;
    @BindView(R.id.tvDescripcion)  TextView tvDescripcion;
    @BindView(R.id.tvReportado)  TextView tvReportado;
    @BindView(R.id.tvUrbanizacion)  TextView tvUrbanizacion;
    @BindView(R.id.tvTerritorio)  TextView tvTerritorio;
    @BindView(R.id.tvDetalleNivel)  TextView tvDetalleNivel;
    @BindView(R.id.tvDetalleObst)  TextView tvDetalleObst;
    @BindView(R.id.tvNivel)  TextView tvNivel;
    @BindView(R.id.rvMedia) RecyclerView rvMedia;
    @BindView(R.id.rvAtencion) RecyclerView rvAtencion;
    @BindView(R.id.cvFoto) CircleImageView cvFoto;

    @BindView(R.id.llSubir) LinearLayout llSubir;
    @BindView(R.id.llEnviar) LinearLayout llEnviar;

    @BindView(R.id.btnConfirmar)
    Button btnConfirmar;
    @BindView(R.id.btnRechazar) Button btnRechazar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_incidencia);
        ButterKnife.bind(this);
        persona= Select.from(Persona.class).first();
        webServices=new WebServices(DetalleIncidNotifiActivity.this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Incidente");
        }

        if (getIntent().getExtras()!=null){
            notificaciones=(Notificaciones) getIntent().getExtras().getSerializable("incidente");
        }

        horizontalLayoutManagaer= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvMedia.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvMedia.setLayoutManager(llm);

        rvAtencion.setHasFixedSize(true);
        RecyclerView.LayoutManager lla = new LinearLayoutManager(this);
        rvAtencion.setLayoutManager(lla);
        if (notificaciones!=null){
            webservicesListarIncidenciaId(notificaciones);
        }
        setSupportActionBar(toolbar);
    }

    public  void  llenarDatos(Incidente incidente){
        llSubir.setVisibility(View.GONE);
        llEnviar.setVisibility(View.GONE);
        if (incidente!=null){
            tvFecha.setText(Metodos.formatoFecha(incidente.getFecha()));
            tvDireccion.setText(incidente.getDireccion());
            tvDescripcion.setText(incidente.getDescripcion());
            tvUrbanizacion.setText(incidente.getUrbanizacion());
            tvTerritorio.setText(incidente.getTerritorio());
            try {
                JSONObject ciudadano=new JSONObject(incidente.getCiudadano());
                JSONObject detalle=new JSONObject(incidente.getDetalle());
                tvReportado.setText(ciudadano.getString("nombres"));
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
                            Media media=new Media(
                                    jsmedia.getInt("id"),
                                    jsmedia.getString("tipo_media"),
                                    jsmedia.getString("incidente_media_url")
                            );

                            mediaList.add(media);
                        }


                       // mediaList.add(medias);
                        rvMedia.setAdapter(new CardDetalleIncidencia(mediaList,DetalleIncidNotifiActivity.this));
                        rvMedia.setLayoutManager(horizontalLayoutManagaer);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(DetalleIncidNotifiActivity.this,MenuActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        a.putExtra(getString(R.string.parametro_evento),"Validar Incidente");
        startActivity(a);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent a = new Intent(DetalleIncidNotifiActivity.this,MenuActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        a.putExtra(getString(R.string.parametro_evento),"Validar Incidente");
        startActivity(a);
        finish();
        return super.onSupportNavigateUp();
    }

    @OnClick({R.id.cvFoto,R.id.btnConfirmar,R.id.btnRechazar})
    void onclick(View button) {
        switch (button.getId()){

            case R.id.cvFoto:
                if (incidente!=null){
                    Intent intent=new Intent(DetalleIncidNotifiActivity.this,MapsActivity.class);
                    intent.putExtra("latitud",incidente.getLatitud());
                    intent.putExtra("longitud",incidente.getLongitud());
                    intent.putExtra("polyline",incidente.getPolilyne());
                    startActivity(intent);
                }
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

    private void webservicesListarIncidenciaId(final Notificaciones notificaciones) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(DetalleIncidNotifiActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(DetalleIncidNotifiActivity.this,"Cargando datos");
        progressDialog.show();


        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.GET, webServices.Url(17)+notificaciones.getIdIncidencia(), null
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
                        llenarDatos(incidente);

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
                }
            }


        });
    }

    private void webservicesValidarIncidente(final int valor) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(DetalleIncidNotifiActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(DetalleIncidNotifiActivity.this,"Validando");
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
                    Toast.makeText(DetalleIncidNotifiActivity.this,response.getString("message"),Toast.LENGTH_SHORT).show();
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
                        Intent a = new Intent(DetalleIncidNotifiActivity.this,MenuActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        a.putExtra(getString(R.string.parametro_evento),"Validar Incidente");
                        startActivity(a);
                    }
                }
            }


        });
    }
}
