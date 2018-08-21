package com.adyl.aylludamos.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.MainActivity;
import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.LogeoActivity;
import com.adyl.aylludamos.activity.MenuActivity;
import com.adyl.aylludamos.activity.PrincipalActivity;
import com.adyl.aylludamos.activity.RegistrarActivity;
import com.adyl.aylludamos.activity.SplashScreenActivity;
import com.adyl.aylludamos.adapters.CardDirectorio;
import com.adyl.aylludamos.adapters.CardFamilia;
import com.adyl.aylludamos.beans.Familia;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Persona;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LENOVO on 25/06/2018.
 */

public class MiFamiliaFragment extends Fragment {

    Persona persona=null;
    WebServices webServices;
    boolean estado=false;//validar si registro familiar
    List<Familia> familiaList=new ArrayList<>();
    @BindView(R.id.rvFamilia) RecyclerView rvFamilia;
    @BindView(R.id.bAgregar) Button bAgregar;
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
        View view = inflater.inflate(R.layout.fragment_familia, container, false);
        webServices=new WebServices(getActivity());
        persona= Select.from(Persona.class).first();
        ButterKnife.bind(this,view);
        webservicesListarFamilia(persona);
        rvFamilia.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(getActivity());
        rvFamilia.setLayoutManager(llm);
        return view;
    }


    private void webservicesListarFamilia( final Persona persona ){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Cargando ");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,  webServices.Url(9), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mView.showMessage(response);
                try {
                    JSONArray jsonarrayIncidentes=new JSONArray(response);
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        familiaList.clear();
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            Familia familia=new Familia(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("nombres"),
                                    jsonObject.getString("telefono")
                            );
                            familiaList.add(familia);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(getString(R.string.parametro_service_idpersona), String.valueOf(persona.getIdPersona()));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        stringRequest.setRetryPolicy(webServices.Parametros());
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (Metodos.VerificarLista(familiaList)){
                        rvFamilia.setAdapter(new CardFamilia(familiaList,getActivity()));
                    }
                }
            }


        });
    }

    @OnClick({
            R.id.bAgregar
    })
    void  onclick(Button button){
        switch(button.getId()) {
            case R.id.bAgregar:
                showDialogRegistrar(getActivity());
                break;

        }
    }



    /*public  String contactIdByPhoneNumber(Context ctx, String phoneNumber) {
        String contactId = null;
        if (phoneNumber != null && phoneNumber.length() > 0) {
            ContentResolver contentResolver = ctx.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[] { ContactsContract.PhoneLookup._ID };

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }
        }
        return contactId;
    }*/



    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "Contacto No Encontrado";

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String nombre = "";

    private void checkPermission(String number) {
        String id = "";
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("Necesitas conceder permiso a tus Contactos",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[] {Manifest.permission.READ_CONTACTS},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        nombre = getContactDisplayNameByNumber(   number );
    }



    //int veces = 0;
    public void showDialogRegistrar(final Activity activity){
        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_registrar_familiar);
        final EditText etTelefono=(EditText)dialog.findViewById(R.id.etTelefono);
        final EditText etNombre=(EditText)dialog.findViewById(R.id.etNombre);
        Button btnRegistrar=(Button) dialog.findViewById(R.id.btnRegistrar);
        ImageView imgFindContact=(ImageView) dialog.findViewById(R.id.imgFindContact);

        etTelefono.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.i("canttt=====","=="+s.length());
                //Log.i("canttt=====","=="+count);
                if( s.length() >= 9 ){

                    /*if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        checkPermission( s.toString()) ;
                        etNombre.setText(nombre);
                    }
                    else {
                        // Pre-Marshmallow
                        Log.i("anthonyyyyyy","jejejej elseee");
                        nombre = getContactDisplayNameByNumber(   s.toString() );
                    }
                    etNombre.setText(nombre);*/
                }
                else{
                    etNombre.setText("");
                }
            }
        });


        /*etTelefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!TextUtils.isEmpty(etNombre.getText().toString())){
                    etNombre.setError(null);
                }
                else{
                    if( veces > 0 ) {
                        etNombre.setError("Ingrese nombre");
                        etNombre.requestFocus();
                    }
                    veces++;
                }
            }
        });*/

        imgFindContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = etTelefono.getText().toString().trim();
                if( tel.length() >= 9 ) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        checkPermission(tel);
                        etNombre.setText(nombre);
                    } else {
                        // Pre-Marshmallow
                        Log.i("anthonyyyyyy", "jejejej elseee::::" + tel);
                        nombre = getContactDisplayNameByNumber(tel);
                    }
                    etNombre.setText(nombre);

                }
                else{
                    etTelefono.setError("Complete teléfono");
                    etTelefono.requestFocus();
                }
            }


        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar(etNombre,etTelefono)){
                    webservicesRegistarFamilia(etNombre.getText().toString(),etTelefono.getText().toString(),dialog);
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

    public boolean validar(EditText nombre, EditText telefono){
        boolean retorno=false;

        if (!TextUtils.isEmpty(telefono.getText().toString())){
            telefono.setError(null);
            retorno=true;
        }else{
            telefono.setError("Ingrese teléfono");
            telefono.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(nombre.getText().toString())){
            nombre.setError(null);
            retorno=true;
        }else{
            nombre.setError("Ingrese nombre");
            nombre.requestFocus();
            return false;
        }

        return  retorno;
    }

    private void webservicesRegistarFamilia(final String usuario, final String celular, final Dialog dialog) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Espere un momento");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_nombre),usuario);
            object.put(getString(R.string.parametro_service_telefono),celular);
            object.put(getString(R.string.parametro_service_idpersona),persona.getIdPersona());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(11), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("status");
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
                    if (estado){
                        estado=false;
                        dialog.dismiss();
                        webservicesListarFamilia(persona);
                    }
                }
            }


        });
    }
}
