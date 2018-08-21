package com.adyl.aylludamos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.SpinnerUrbanizacionAdapter;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.beans.Urbanizacion;
import com.adyl.aylludamos.services.ConexionInterna;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LENOVO on 24/06/2018.
 */

public class RegistrarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private boolean estado=false; // para validar el servicio
    private SpinnerUrbanizacionAdapter spinnerUrbanizacionAdapter;

    private List<Urbanizacion> urbanizacionList=new ArrayList<>();
    private Urbanizacion urbanizacion=null;
    private Persona persona=null;
    private WebServices webServices;
    private Drawable upArrow;

    @BindView(R.id.etTelefonoValidar) EditText etTelefonoValidar;
    @BindView(R.id.etTelefono) EditText etTelefono;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPass) EditText etPass;
    @BindView(R.id.etRePass) EditText etRePass;
    @BindView(R.id.etNombre) EditText etNombre;
    @BindView(R.id.etApellidoPa) EditText etApellidoPa;
    @BindView(R.id.etApellidoMa) EditText etApellidoMa;
    @BindView(R.id.etDni) EditText etDni;
    @BindView(R.id.etDireccion) EditText etDireccion;
    @BindView(R.id.spUrbanizacion) CustomSearchableSpinner spUrbanizacion;
    @BindView(R.id.svRegistrar) ScrollView svRegistrar;
    @BindView(R.id.llRegistrar) LinearLayout llRegistrar;
    @BindView(R.id.llValidar) LinearLayout llValidar;
    @BindView(R.id.btnSiguiente) Button btnSiguiente;
    @BindView(R.id.btnIngresar) Button btnIngresar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivTelefono) ImageView ivTelefono ;
    @BindView(R.id.ivEmail) ImageView ivEmail;
    @BindView(R.id.ivPass) ImageView ivPass;
    @BindView(R.id.ivRePass) ImageView ivRePass;
    @BindView(R.id.ivNombre) ImageView ivNombre;
    @BindView(R.id.ivPaterno) ImageView ivPaterno;
    @BindView(R.id.ivMaterno) ImageView ivMaterno;
    @BindView(R.id.ivDni) ImageView ivDni;
    @BindView(R.id.ivDireccion) ImageView ivDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
        ButterKnife.bind(this);
        webServices=new WebServices(RegistrarActivity.this);
        //webservicesListarUrbanizacion();
       if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("REGISTRAR");
        }

        spUrbanizacion.setTitle("Lista de Urbanizaciones");
        spUrbanizacion.setPositiveButton("CERRAR");


        etPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etEmail.getText().toString()) && emailValidator(etEmail.getText().toString())){
                    etEmail.setError(null);
                    ivEmail.setVisibility(View.VISIBLE);
                }else{
                    etEmail.setError("Ingrese email");
                    etEmail.requestFocus();
                }
            }
        });

        etRePass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etPass.getText().toString())){
                    etPass.setError(null);
                    ivPass.setVisibility(View.VISIBLE);
                }else{
                    etPass.setError("Ingrese password");
                    etPass.requestFocus();
                }
            }
        });

        etNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etRePass.getText().toString())){
                    if (etPass.getText().toString().equals(etRePass.getText().toString())){
                        etRePass.setError(null);
                        ivRePass.setVisibility(View.VISIBLE);
                    }else{
                        etRePass.setError("Los password no coinciden");
                        etRePass.requestFocus();
                    }
                }else{
                    etRePass.setError("Ingrese contrasña");
                    etRePass.requestFocus();
                }


            }
        });

        etApellidoPa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etNombre.getText().toString())){
                    etNombre.setError(null);
                    ivNombre.setVisibility(View.VISIBLE);
                }else{
                    etNombre.setError("Ingrese nombre");
                    etNombre.requestFocus();
                }
            }
        });

        etApellidoMa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etApellidoPa.getText().toString())){
                    etApellidoPa.setError(null);
                    ivPaterno.setVisibility(View.VISIBLE);
                }else{
                    etApellidoPa.setError("Ingrese apellido");
                    etApellidoPa.requestFocus();
                }
            }
        });

        etDni.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etApellidoMa.getText().toString())){
                    etApellidoMa.setError(null);
                    ivMaterno.setVisibility(View.VISIBLE);
                }else{
                    etApellidoMa.setError("Ingrese apellido");
                    etApellidoMa.requestFocus();
                }
            }
        });

        etDireccion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (etDni.length()>0){
                    etDni.setError(null);
                    ivDni.setVisibility(View.VISIBLE);
                }else{
                    etDni.setError("Ingrese dni");
                    etDni.requestFocus();
                }
            }
        });

        spUrbanizacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spUrbanizacion.isSpinnerDialogOpen = false;
                urbanizacion=spinnerUrbanizacionAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSupportActionBar(toolbar);
        upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.celeste), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.back));
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        completar();
    }

    public void completar(){
        ivEmail.setVisibility(View.INVISIBLE);
        ivPass.setVisibility(View.INVISIBLE);
        ivRePass.setVisibility(View.INVISIBLE);
        ivNombre.setVisibility(View.INVISIBLE);
        ivPaterno.setVisibility(View.INVISIBLE);
        ivMaterno.setVisibility(View.INVISIBLE);
        ivDni.setVisibility(View.INVISIBLE);
        ivDireccion.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RegistrarActivity.this,PrincipalActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent=new Intent(RegistrarActivity.this,PrincipalActivity.class);
        startActivity(intent);
        finish();
        return super.onSupportNavigateUp();
    }

    @OnClick({
            R.id.btnSiguiente,
            R.id.btnIngresar
    })
    void  onclick(Button button){
        switch(button.getId()) {
            case R.id.btnSiguiente:
                if (!TextUtils.isEmpty(etTelefonoValidar.getText().toString())){
                    //servicio de validar por sms
                    webservicesListarUrbanizacion();
                    svRegistrar.setVisibility(View.VISIBLE);
                    llRegistrar.setVisibility(View.VISIBLE);
                    llValidar.setVisibility(View.GONE);
                    etTelefono.setText(etTelefonoValidar.getText().toString());
                    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                    getSupportActionBar().setCustomView(R.layout.custom_logo);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.back));
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    etEmail.requestFocus();

                }else{
                    etTelefonoValidar.setError("Ingrese numero de celular");
                }
                break;

            case R.id.btnIngresar:
                if (validar()){
                    if (urbanizacion!=null){
                        persona=new Persona();
                        persona.setIdUsuario(0);
                        persona.setIdPersona(0);
                        persona.setNombre(etNombre.getText().toString());
                        persona.setApe_paterno(etApellidoPa.getText().toString());
                        persona.setApe_materno(etApellidoMa.getText().toString());
                        persona.setDni(Integer.parseInt(etDni.getText().toString()));
                        persona.setTelefono(etTelefono.getText().toString());
                        persona.setEmail(etEmail.getText().toString());
                        persona.setDireccion(etDireccion.getText().toString());
                        persona.setIdUrbanizacion(urbanizacion.getIdUrbanizacion());
                        persona.setUrbanizacion(urbanizacion.getTerritorio());
                        persona.setIdTipo(2);
                        persona.setTipo("Ciudadano");
                        persona.setPassword(etPass.getText().toString());
                        webservicesRegistar(persona);
                    }
                }
                break;
        }
    }

    public  boolean validar(){
        boolean retorno=false;
        if (TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError("Ingrese email");
            etEmail.requestFocus();
            return false;
        }else{
            if (emailValidator(etEmail.getText().toString())){
                etEmail.setError(null);
                ivEmail.setVisibility(View.VISIBLE);
                retorno=true;
            }else{
                etEmail.setError("Email incorrecto");
                etEmail.requestFocus();
                return false;
            }
        }
        if (TextUtils.isEmpty(etPass.getText().toString())){
            etPass.setError("Ingrese password");
            etPass.requestFocus();
            return false;
        }else{
            etPass.setVisibility(View.VISIBLE);
            etPass.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etRePass.getText().toString())){
            etRePass.setError("Repita password");
            etRePass.requestFocus();
            return false;
        }else{
            etRePass.setError(null);
            etRePass.setVisibility(View.VISIBLE);
            retorno=true;
        }

       if (TextUtils.isEmpty(etNombre.getText().toString())){
            etNombre.setError("Ingrese nombre");
            etNombre.requestFocus();
            return false;
        }else{
            etNombre.setError(null);
            etNombre.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etApellidoPa.getText().toString())){
            etApellidoPa.setError("Ingrese apellido");
            etApellidoPa.requestFocus();
            return false;
        }else{
            etApellidoPa.setError(null);
            etApellidoPa.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etApellidoMa.getText().toString())){
            etApellidoMa.setError("Ingrese apellido");
            etApellidoMa.requestFocus();
            return false;
        }else{
            etApellidoMa.setError(null);
            etApellidoMa.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etDni.getText().toString())){
            etDni.setError("Ingrese DNI");
            etDni.requestFocus();
            return false;
        }else{
            etDni.setError(null);
            etDni.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etDireccion.getText().toString())){
            etDireccion.setError("Ingrese dirección");
            etDireccion.requestFocus();
            return false;
        }else{
            etDireccion.setError(null);
            etDireccion.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (etPass.getText().toString().equals(etRePass.getText().toString())){
            retorno=true;
            etRePass.setError(null);
        }else{
            etRePass.setError("Los password no coinciden");
            return false;
        }

        return  retorno;
    }

    public  boolean validados(){
        boolean retorno=false;
        if (TextUtils.isEmpty(etEmail.getText().toString())){
            etEmail.setError("Ingrese email");
            etEmail.requestFocus();
            return false;
        }else{
            if (emailValidator(etEmail.getText().toString())){
                etEmail.setError(null);
                ivEmail.setVisibility(View.VISIBLE);
                retorno=true;
            }else{
                etEmail.setError("Email incorrecto");
                etEmail.requestFocus();
                return false;
            }
        }
        if (TextUtils.isEmpty(etPass.getText().toString())){
            etPass.setError("Ingrese password");
            etPass.requestFocus();
            return false;
        }else{
            etPass.setVisibility(View.VISIBLE);
            etPass.setError(null);
            retorno=true;
        }

        if (TextUtils.isEmpty(etRePass.getText().toString())){
            etRePass.setError("Repita password");
            etRePass.requestFocus();
            return false;
        }else{
            etRePass.setError(null);
            etRePass.setVisibility(View.VISIBLE);
            retorno=true;
        }

       if (TextUtils.isEmpty(etNombre.getText().toString())){
            etNombre.setError("Ingrese nombre");
            etNombre.requestFocus();
            return false;
        }else{
            etNombre.setError(null);
            etNombre.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etApellidoPa.getText().toString())){
            etApellidoPa.setError("Ingrese apellido");
            etApellidoPa.requestFocus();
            return false;
        }else{
            etApellidoPa.setError(null);
            etApellidoPa.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etApellidoMa.getText().toString())){
            etApellidoMa.setError("Ingrese apellido");
            etApellidoMa.requestFocus();
            return false;
        }else{
            etApellidoMa.setError(null);
            etApellidoMa.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etDni.getText().toString())){
            etDni.setError("Ingrese DNI");
            etDni.requestFocus();
            return false;
        }else{
            etDni.setError(null);
            etDni.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (TextUtils.isEmpty(etDireccion.getText().toString())){
            etDireccion.setError("Ingrese dirección");
            etDireccion.requestFocus();
            return false;
        }else{
            etDireccion.setError(null);
            etDireccion.setVisibility(View.VISIBLE);
            retorno=true;
        }

        if (etPass.getText().toString().equals(etRePass.getText().toString())){
            retorno=true;
            etRePass.setError(null);
        }else{
            etRePass.setError("Los password no coinciden");
            return false;
        }

        return  retorno;
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void webservicesListarUrbanizacion() {
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarActivity.this,"Cargando datos");
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
                        spinnerUrbanizacionAdapter = new SpinnerUrbanizacionAdapter(RegistrarActivity.this, android.R.layout.simple_spinner_dropdown_item, urbanizacionList);
                        spUrbanizacion.setAdapter(spinnerUrbanizacionAdapter);
                    }
                }
            }


        });
    }


    private void webservicesRegistar(final Persona persona) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarActivity.this,"Iniciar Sessión");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_paterno),persona.getApe_paterno());
            object.put(getString(R.string.parametro_service_materno),persona.getApe_materno());
            object.put(getString(R.string.parametro_service_nombre),persona.getNombre());
            object.put(getString(R.string.parametro_service_dni),persona.getDni());
            object.put(getString(R.string.parametro_service_telefono),persona.getTelefono());
            object.put(getString(R.string.parametro_service_email),persona.getEmail());
            object.put(getString(R.string.parametro_service_direccion),persona.getDireccion());
            object.put(getString(R.string.parametro_service_pass),persona.getPassword());
            object.put(getString(R.string.parametro_service_idurba),persona.getIdUrbanizacion());
            object.put(getString(R.string.parametro_service_token),Metodos.TokenFirebase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(4), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("status");
                    Toast.makeText(RegistrarActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                    persona.setIdPersona(response.getInt("persona_id"));

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
                        ConexionInterna.registrarPersona(persona);
                        TipoUsuario tipoUsuario=new TipoUsuario(
                                persona.getIdPersona(),
                               "Inactivo",
                                2,
                                "Ciudadano",
                                "Bronce",
                                "bronce",
                                "0","0","0","0"
                        );
                        tipoUsuario.save();
                        Intent intent = new Intent(RegistrarActivity.this,MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }


        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
