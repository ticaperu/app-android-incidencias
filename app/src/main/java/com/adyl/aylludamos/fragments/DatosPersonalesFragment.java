package com.adyl.aylludamos.fragments;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.MenuActivity;
import com.adyl.aylludamos.activity.RegistrarActivity;
import com.adyl.aylludamos.adapters.SpinnerUrbanizacionAdapter;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.beans.Urbanizacion;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.adyl.aylludamos.utils.CustomSearchableSpinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
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

/**
 * Created by LENOVO on 25/06/2018.
 */

public class DatosPersonalesFragment extends Fragment {

    int tab=1;
    boolean estadoRegistro=false;
    boolean estadoPassword=false;
    Persona persona=null;
    TipoUsuario tipoUsuario=null;
    private List<Urbanizacion> urbanizacionList=new ArrayList<>();
    private Urbanizacion urbanizacion=null;
    private SpinnerUrbanizacionAdapter spinnerUrbanizacionAdapter;
    WebServices webServices;
    int valor=0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        //webservicesListarUrbanizacion();
        persona= Select.from(Persona.class).first();
        tipoUsuario=Select.from(TipoUsuario.class).first();
        tipoUsuario();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @BindView(android.R.id.tabhost) TabHost tabhost;
    //

    @BindView(R.id.cvFoto) CircleImageView cvFoto;
    @BindView(R.id.tvNombre) TextView tvNombre;
    @BindView(R.id.tvNivel) TextView tvNivel;
    @BindView(R.id.tvPuntos) TextView tvPuntos;
    @BindView(R.id.tvIncidencias) TextView tvIncidencias;
    @BindView(R.id.tvPorAtender) TextView tvPorAtender;
    @BindView(R.id.tvAtendidas) TextView tvAtendidas;


    @BindView(R.id.etTelefono) EditText etTelefono;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etNombre) EditText etNombre;
    @BindView(R.id.etApellidoPa) EditText etApellidoPa;
    @BindView(R.id.etApellidoMa) EditText etApellidoMa;
    @BindView(R.id.etDireccion) EditText etDireccion;
    @BindView(R.id.spUrbanizacion) CustomSearchableSpinner spUrbanizacion;
    @BindView(R.id.etTerritorio) EditText etTerritorio;
    @BindView(R.id.btnActualizarDatos) Button btnActualizarDatos;

    @BindView(R.id.etPasAntiguo) EditText etPasAntiguo;
    @BindView(R.id.etPass) EditText etPass;
    @BindView(R.id.etRePass) EditText etRePass;
    @BindView(R.id.btnActualizarContra) Button btnActualizarContra;
    int idx = 0;
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_datospersonales, container, false);
        ButterKnife.bind(this,view);
        webServices=new WebServices(getActivity());

        tabhost.setup();
        TabHost.TabSpec spec = tabhost.newTabSpec("Datos");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Datos");
        tabhost.addTab(spec);

        spec = tabhost.newTabSpec("Información");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Información");
        tabhost.addTab(spec);

        spec = tabhost.newTabSpec("Password");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Password");
        tabhost.addTab(spec);
        if (getArguments() != null) {
            tab = Integer.parseInt(getArguments().getString("tab"));
        }

        tabhost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
        tabhost.setCurrentTab(0);

        spUrbanizacion.setTitle("Lista de Urbanizaciones");
        spUrbanizacion.setPositiveButton("CERRAR");

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(view.getResources().getColor(R.color.white));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(11);
        }
        setTabColor(tabhost);
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {
                setTabColor(tabhost);
                if (arg0.equalsIgnoreCase("Datos")) {
                    tipoUsuario();

                }else{
                    if (arg0.equalsIgnoreCase("Información")) {
                        llenarDatos();
                        if (!Metodos.VerificarLista(urbanizacionList)){
                            webservicesListarUrbanizacion(persona.getIdUrbanizacion());
                            // Aca debe seleccionar por defecto la urbanización a la que pertenece el ciudadano
                        }

                    }
                }
            }
        });

        spUrbanizacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spUrbanizacion.isSpinnerDialogOpen = false;
                urbanizacion=spinnerUrbanizacionAdapter.getItem(position);


                // setar el territorio vecinal dependiendo de la urb
                if (valor!=0){
                    etTerritorio.setText(urbanizacion.getTerritorio());
                }else{
                    valor=1;
                    spUrbanizacion.setSelection(idx);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public static void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.color.colorPrimary); // unselected
        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.color.colorPrimaryDark);
    }

    @OnClick({
            R.id.btnActualizarDatos,
            R.id.btnActualizarContra
    })
    void  onclick(Button button){
        switch(button.getId()) {
            case R.id.btnActualizarDatos:
                if (validarDatos()){
                    if (Metodos.estadoConexionWifioDatos(getActivity())){
                        webservicesActualizarDatos(persona);
                    }else{
                        Toast.makeText(getActivity(),getActivity().getString(R.string.error_internet),Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.btnActualizarContra:
                if (validarPassword()){
                    if (Metodos.estadoConexionWifioDatos(getActivity())){
                        webservicesActualizarPassword(persona);
                    }else{
                        Toast.makeText(getActivity(),getActivity().getString(R.string.error_internet),Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void llenarDatos(){

        if (persona!=null){
            etTelefono.setText(persona.getTelefono());
            etEmail.setText(persona.getEmail());
            etNombre.setText(persona.getNombre());
            etApellidoPa.setText(persona.getApe_paterno());
            etApellidoMa.setText(persona.getApe_materno());
            etDireccion.setText(persona.getDireccion());
            //etTerritorio.setText(persona.getUrbanizacion());
            //Log.i("========", "======="+persona.getDescTerritVecinal());
            etTerritorio.setText(persona.getDescTerritVecinal());
            //spUrbanizacion.setSelection(0);
        }
    }

    public void tipoUsuario(){
        if (tipoUsuario!=null && persona!=null){
            Metodos.GlideImagen(getActivity(),tipoUsuario.getImagen(),cvFoto);
            tvNombre.setText(persona.getNombre());
            tvNivel.setText("(Nivel "+tipoUsuario.getNivel()+")");
            tvPuntos.setText(tipoUsuario.getPuntuacion());
            tvIncidencias.setText(tipoUsuario.getInRegistradas());
            tvAtendidas.setText(tipoUsuario.getInAtendidas());
            tvPorAtender.setText(tipoUsuario.getInNoAtendidas());
        }
    }

    public boolean validarDatos(){
        boolean retorno=false;

        if (TextUtils.isEmpty(etTelefono.getText().toString())){
            etTelefono.setError("Ingrese email");
            etTelefono.requestFocus();
            return false;
        }else{
            etTelefono.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError("Ingrese email");
            etEmail.requestFocus();
            return false;
        }else{
            etEmail.setError(null);
            retorno=true;
        }
        if (TextUtils.isEmpty(etNombre.getText().toString())){
            etNombre.setError("Ingrese nombre");
            etNombre.requestFocus();
            return false;
        }else{
            etNombre.setError(null);
            retorno=true;
        }
        if (TextUtils.isEmpty(etApellidoPa.getText().toString())){
            etApellidoPa.setError("Ingrese apellido");
            etApellidoPa.requestFocus();
            return false;
        }else{
            etApellidoPa.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etApellidoMa.getText().toString())){
            etApellidoMa.setError("Ingrese apellido");
            etApellidoMa.requestFocus();
            return false;
        }else{
            etApellidoMa.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etDireccion.getText().toString())){
            etDireccion.setError("Ingrese dirección");
            etDireccion.requestFocus();
            return false;
        }else{
            etDireccion.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etTerritorio.getText().toString())){
            etTerritorio.setError("Ingrese dirección");
            etTerritorio.requestFocus();
            return false;
        }else{
            etTerritorio.setError(null);
            retorno=true;
        }

        return  retorno;
    }

    public boolean validarPassword(){
        boolean retorno=false;

        if (TextUtils.isEmpty(etPasAntiguo.getText().toString())){
            etPasAntiguo.setError("Ingrese password");
            etPasAntiguo.requestFocus();
            return false;
        }else{
            etPasAntiguo.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etPass.getText().toString())){
            etPass.setError("Ingrese password");
            etPass.requestFocus();
            return false;
        }else{
            etPass.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etRePass.getText().toString())){
            etRePass.setError("Repita password");
            etRePass.requestFocus();
            return false;
        }else{
            etRePass.setError(null);
            retorno=true;
        }

        if (etPass.getText().toString().equals(etRePass.getText().toString())){
            retorno=true;
            etRePass.setError(null);
        }else{
            etRePass.setError("Los password no coinciden");
            return false;
        }
        return true;
    }

    private void webservicesActualizarDatos(final Persona persona) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Actualizando datos");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_idpersona),persona.getIdPersona());
            object.put(getString(R.string.parametro_service_paterno),etApellidoPa.getText().toString());
            object.put(getString(R.string.parametro_service_materno),etApellidoMa.getText().toString());
            object.put(getString(R.string.parametro_service_nombre),etNombre.getText().toString());
            object.put(getString(R.string.parametro_service_direccion),etDireccion.getText().toString());
            object.put(getString(R.string.parametro_service_idurba),urbanizacion.getIdUrbanizacion());
        } catch (JSONException e) {
            e.printStackTrace();

    }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(5), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estadoRegistro =response.getBoolean("status");
                    Toast.makeText(getActivity(),response.getString("mensaje"),Toast.LENGTH_SHORT).show();

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
                    if (estadoRegistro){
                        estadoRegistro=false;
                        persona.setIdUrbanizacion(urbanizacion.getIdUrbanizacion());
                        persona.setDescTerritVecinal(urbanizacion.getTerritorio());
                        Persona per = Persona.findById(Persona.class,persona.getId());
                        per.setDescTerritVecinal(urbanizacion.getTerritorio());
                        persona.save();
                        List<Persona>pers= Select.from(Persona.class).list();
                        etTerritorio.setText(urbanizacion.getTerritorio());

                    }
                }
            }


        });
    }

    private void webservicesActualizarPassword(final Persona persona) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Actualizando password");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_idpersona),persona.getIdPersona());
            object.put(getString(R.string.parametro_service_pass),etPasAntiguo.getText().toString());
            object.put(getString(R.string.parametro_service_nuevopass),etPass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();

        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(6), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estadoPassword =response.getBoolean("status");
                    Toast.makeText(getActivity(),response.getString("mensaje"),Toast.LENGTH_SHORT).show();

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
                    if (estadoPassword){
                        estadoPassword=false;
                        limpiar();
                    }
                }
            }


        });
    }

    public void limpiar(){
        etPasAntiguo.setText("");
        etPass.setText("");
        etRePass.setText("");
    }


    private void webservicesListarUrbanizacion(final int idurbselected) {
        idx = 0;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(3), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes =response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            Urbanizacion urbanizacion= new Urbanizacion(
                                    jsonObject.getInt("urbanizacion_id"),
                                    jsonObject.getString("descripcion"),
                                    jsonObject.getString("territorio_vecinal_descripcion")
                            );
                            urbanizacionList.add(urbanizacion);

                            if( urbanizacion.getIdUrbanizacion() == idurbselected ){
                                idx = i;
                            }
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
                if (Metodos.VerificarLista(urbanizacionList)){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        spinnerUrbanizacionAdapter = new SpinnerUrbanizacionAdapter(getActivity()   , android.R.layout.simple_spinner_dropdown_item, urbanizacionList);
                        spUrbanizacion.setAdapter(spinnerUrbanizacionAdapter);
                        spUrbanizacion.setSelection(idx);

                    }
                }
            }


        });
    }
}
