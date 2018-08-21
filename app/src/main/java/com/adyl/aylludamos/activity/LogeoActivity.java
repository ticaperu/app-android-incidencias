package com.adyl.aylludamos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.MainActivity;
import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.Recordar;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.SupportMapFragment;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LENOVO on 24/06/2018.
 */

public class LogeoActivity extends AppCompatActivity {
    boolean estado=false;
    boolean recu=false;
    Persona persona=null;
    Recordar recordar=null;
    WebServices webServices;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPass)
    EditText etPass;
    @BindView(R.id.tvOlvido)
    TextView tvOlvido;
    @BindView(R.id.tvRegistrar) TextView tvRegistrar;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.cbRecordar) CheckBox cbRecordar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        webServices=new WebServices(LogeoActivity.this);
        recordar= Select.from(Recordar.class).first();

        tvOlvido.setPaintFlags(tvOlvido.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvRegistrar.setPaintFlags(tvRegistrar.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

       if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("INCIDENCIAS");
        }

        if (recordar!=null){
           etEmail.setText(recordar.getEmail().trim());
            etPass.setText(recordar.getPassword().trim());
            cbRecordar.setChecked(true);
        }

        etPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etEmail.getText().toString()) && emailValidator(etEmail.getText().toString().trim())){
                    etEmail.setError(null);
                }else{
                    etEmail.setError("Ingrese email");
                    etEmail.requestFocus();
                }
            }
        });
    }

    @OnClick({
            R.id.tvOlvido,
            R.id.tvRegistrar,
            R.id.btnLogin
    })
    void  onclick(View view){
        switch(view.getId()) {
            case R.id.tvOlvido:
                showDialogIncidente(LogeoActivity.this);
                break;

            case R.id.tvRegistrar:
                Intent registrar=new Intent(LogeoActivity.this,RegistrarActivity.class);
                startActivity(registrar);
                finish();
                break;
            case R.id.btnLogin:
                if (validarSession()){
                    if (Metodos.estadoConexionWifioDatos(LogeoActivity.this)){
                        webservicesLogeo(etEmail.getText().toString(),etPass.getText().toString());
                    }else{
                        //MENSAJE DE QUE DEBE TENER INTERNET -> EN UN DIALOG O TOAKS
                        Toast.makeText(LogeoActivity.this,"Debe tener conexión a internet",Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    private boolean validarSession() {
        boolean cancel = true;
        View focusView = null;
        if (TextUtils.isEmpty(etPass.getText().toString())) {
            etPass.setError(getString(R.string.error_invalid_password));
            focusView = etPass;
            cancel = false;
        }else{
            etPass.setError(null);
        }
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = false;
        }else{
            etPass.setError(null);
        }

        if (!cancel){
            focusView.requestFocus();
        }
        return  cancel;
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

    private void webservicesLogeo(final String usuario, final String password) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(LogeoActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(LogeoActivity.this,"Iniciar Sessión");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_email),usuario);
            object.put(getString(R.string.parametro_service_pass),password);
            object.put(getString(R.string.parametro_service_token),Metodos.TokenFirebase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(2), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   estado =response.getBoolean("status");
                    Toast.makeText(LogeoActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                    //idUsuario, int idPersona, String nombre, String ape_paterno, String ape_materno,
                    // int dni, String email, String direccion, String telefono, String password, int idUrbanizacion, String urbanizacion, int idTipo, String tipo) {

                        persona=new Persona(
                            response.getInt("user_id"),
                            response.getInt("user_persona_id"),
                            response.getString("user_persona_nombres"),
                            response.getString("user_persona_ape_paterno"),
                            response.getString("user_persona_ape_materno"),
                            response.getInt("user_persona_dni"),
                            response.getString("user_email"),
                            response.getString("user_persona_direccion"),
                            response.getString("user_persona_telefono"), password,
                            response.getInt("user_urbanizacion_id"),
                            response.getString("user_urbanizacion_name"),
                            response.getInt("user_tipo_persona_id"),
                            response.getString("user_tipo_persona_name")
                    );

                    persona.setDescTerritVecinal(response.getString("user_territoriovecinal_name"));
                    persona.save();

                    TipoUsuario tipoUsuario=new TipoUsuario(
                            response.getInt("user_persona_id"),
                            response.getString("user_state"),
                            response.getInt("user_tipo_persona_id"),
                            response.getString("user_tipo_persona_name"),
                            response.getString("user_nivel_ciudadano_name"),
                            response.getString("user_nivel_ciudadano_icono_src"),
                            response.getString("user_puntuacion_persona_puntos"),
                            response.getString("user_incidencias_registradas"),
                            response.getString("user_incidencias_atendidas"),
                            response.getString("user_incidencias_no_atendidas")
                    );
                    tipoUsuario.save();


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

                        if(cbRecordar.isChecked()){
                            Recordar.deleteAll(Recordar.class);
                            Recordar recordar=new Recordar(usuario,password);
                            recordar.save();
                        }else{
                            Recordar.deleteAll(Recordar.class);
                        }
                        Intent intent = new Intent(LogeoActivity.this,MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }


        });
    }


    public void showDialogIncidente(final Activity activity){
        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_recuperar_contra);
        final TextView etCorreo=(TextView)dialog.findViewById(R.id.etCorreo);
        Button btnSolicitar=(Button)dialog.findViewById(R.id.btnSolicitar);

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Metodos.emailValidator(etCorreo.getText().toString())){
                    webservicesRecuperar(dialog,etCorreo.getText().toString());
                    etCorreo.setError(null);
                }else{
                    etCorreo.setError("Ingrese email");
                }
            }
        });




        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        Window window=dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void webservicesRecuperar(final Dialog dialog, final String correo) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(LogeoActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(LogeoActivity.this,"Solicitando contraseña");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_correo),correo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(18), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    recu =response.getBoolean("status");
                    Toast.makeText(LogeoActivity.this,response.getString("message"),Toast.LENGTH_SHORT).show();
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
                    if (recu){
                        recu=false;
                       dialog.dismiss();
                    }
                }
            }


        });
    }
}
