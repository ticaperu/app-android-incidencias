package com.adyl.aylludamos.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.CardAtencion;
import com.adyl.aylludamos.adapters.CardDetalleIncidencia;
import com.adyl.aylludamos.adapters.CardDetalleIncidenciaM;
import com.adyl.aylludamos.beans.Atencion;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Media;
import com.adyl.aylludamos.beans.Multimedia;
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
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleIncidActivity extends AppCompatActivity {
    Incidente incidente = null;
    int agregado=0;
    int valor =0;
    Persona persona=null; //validar si puede subir o no imagenes
    //List<Media> mediaList =new ArrayList<>();
    List<Multimedia> mediaLista =new ArrayList<>();
    List<Atencion> atenionList=new ArrayList<>();
    LinearLayoutManager horizontalLayoutManagaer;

    //CardDetalleIncidencia mAdapterMM = null;
    private String formatosMultiMedia[][] = new String[5][2];
    private boolean estado=false; // para validar el servicio
    private WebServices webServices = null;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvTipo)  TextView tvTipo;
    @BindView(R.id.tvFecha)  TextView tvFecha;
    @BindView(R.id.tvDireccion)  TextView tvDireccion;
    //@BindView(R.id.tvEstado) TextView tvEstado;
    @BindView(R.id.tvDescripcion)  TextView tvDescripcion;
    @BindView(R.id.tvReportado)  TextView tvReportado;
    @BindView(R.id.tvUrbanizacion)  TextView tvUrbanizacion;
    @BindView(R.id.tvTerritorio)  TextView tvTerritorio;
    @BindView(R.id.tvDetalleNivel)  TextView tvDetalleNivel;
    @BindView(R.id.tvDetalleObst)  TextView tvDetalleObst;
    @BindView(R.id.tvNivel)  TextView tvNivel;
    @BindView(R.id.rvMedia) RecyclerView rvMedia;
    @BindView(R.id.rvAtencion) RecyclerView rvAtencion;

    @BindView(R.id.llSubir) LinearLayout llSubir;
    @BindView(R.id.llEnviar) LinearLayout llEnviar;
    @BindView(R.id.cvFoto) CircleImageView cvFoto;




    @BindView(R.id.btnMasArchivos)
    ImageView btnMasArchivos;

    @BindView(R.id.btnRegresar)
    Button btnRegresar;

    @BindView(R.id.btnEnviar)
    Button btnEnviar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deta_incidencia);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Incidente");
        }

        if (getIntent().getExtras()!=null){
            incidente=(Incidente) getIntent().getExtras().getSerializable("incidente");
            valor=getIntent().getExtras().getInt("valor");
        }
        persona= Select.from(Persona.class).first();

        horizontalLayoutManagaer= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        webServices = new WebServices(DetalleIncidActivity.this);
        rvMedia.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvMedia.setLayoutManager(llm);

        rvAtencion.setHasFixedSize(true);
        RecyclerView.LayoutManager lla = new LinearLayoutManager(this);
        rvAtencion.setLayoutManager(lla);
        //llenarDatos();
        setSupportActionBar(toolbar);
        /**antony
         * **/
        if (incidente!=null){
            webservicesListarIncidenciaId(incidente);
        }
        requestStoragePermission();
        cargarFormatosPermitidos();
    }

    public  void  llenarDatos(Incidente incidente){
        if (valor!=2){
            if (valor==3){
                llSubir.setVisibility(View.VISIBLE);
                llEnviar.setVisibility(View.VISIBLE);
            }else{
                llSubir.setVisibility(View.GONE);
                llEnviar.setVisibility(View.GONE);
            }
        }else{
            if (incidente.getIdEstado()==1){
                llSubir.setVisibility(View.VISIBLE);
                llEnviar.setVisibility(View.VISIBLE);
            }else{
                llSubir.setVisibility(View.GONE);
                llEnviar.setVisibility(View.GONE);
            }
        }

        if( persona != null ) {
            if (persona.getIdTipo() != 2) {
                llSubir.setVisibility(View.GONE);
                llEnviar.setVisibility(View.GONE);
            }
        }
        else{
            llSubir.setVisibility(View.GONE);
            llEnviar.setVisibility(View.GONE);
        }

        if (incidente!=null){
            tvFecha.setText(Metodos.formatoFecha(incidente.getFecha()));
            tvDireccion.setText(incidente.getDireccion());
            //tvEstado.setText(incidente.getEstado());
            tvDescripcion.setText(incidente.getDescripcion());
            tvUrbanizacion.setText(incidente.getUrbanizacion());
            tvTerritorio.setText(incidente.getTerritorio());
            try {
                JSONObject ciudadano=new JSONObject(incidente.getCiudadano());
                JSONObject detalle=new JSONObject(incidente.getDetalle());
                tvReportado.setText(ciudadano.getString("ape_paterno") + " " + ciudadano.getString("ape_materno") + ", " + ciudadano.getString("nombres"));
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
                        rvMedia.setAdapter(new CardDetalleIncidenciaM(mediaLista,DetalleIncidActivity.this,0));
                        rvMedia.setLayoutManager(horizontalLayoutManagaer);
                    }
                }


                Log.i("DUPLICADOSSSS===",incidente.getAtencion());
                if(!incidente.getAtencion().equalsIgnoreCase("null")){
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


                Log.i("DUPLICADOSSSS===", "====="+atenionList.size());

                if (Metodos.VerificarLista(atenionList)){
                    rvAtencion.setAdapter(new CardAtencion(atenionList,DetalleIncidActivity.this));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /*public  void  llenarDatos(){

        if (valor!=2){
            if (valor==3){
                llSubir.setVisibility(View.VISIBLE);
                llEnviar.setVisibility(View.VISIBLE);
            }else{
                llSubir.setVisibility(View.GONE);
                llEnviar.setVisibility(View.GONE);
            }
        }else{
            if (incidente.getIdEstado()==1){
                llSubir.setVisibility(View.VISIBLE);
                llEnviar.setVisibility(View.VISIBLE);
            }else{
                llSubir.setVisibility(View.GONE);
                llEnviar.setVisibility(View.GONE);
            }
        }

        if (incidente!=null){


            tvTipo.setText(incidente.getTipo().toUpperCase());
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
                            Media mediaa=new Media(
                                    jsmedia.getInt("id"),
                                    jsmedia.getString("tipo_media"),
                                    jsmedia.getString("incidente_media_url")
                            );
                            Multimedia media = new Multimedia();
                            media.setIdmedia(jsmedia.getInt("id"));
                            media.setTipo(jsmedia.getString("tipo_media"));
                            media.setPath_file(jsmedia.getString("incidente_media_url"));
                            //Log.i("BDDDDD::::","== " + media.getPath_file());
                            //mediaList.add(media);
                            mediaLista.add(media);
                        }

                        //Media medias=new Media(
                          //     0,
                          //     "Video",
                          //      "https://drive.google.com/file/d/1EaNSMpTJ-L19yCHxJMgvXVjuo7RMd1p-/view"
                        //);
                        //mediaList.add(medias);
                        rvMedia.setAdapter(new CardDetalleIncidenciaM(mediaLista,DetalleIncidActivity.this,0));
                        rvMedia.setLayoutManager(horizontalLayoutManagaer);
                    }
                }

                if(!incidente.getAtencion().equalsIgnoreCase("null")){
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
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        //if (agregado==0){
            finish();
       /* }else{
            Intent intent=new Intent(DetalleIncidActivity.this,MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(getString(R.string.parametro_evento),"Mis Incidentes");
            startActivity(intent);
        }*/
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @OnClick({R.id.btnEnviar,R.id.btnMasArchivos,R.id.cvFoto})
     void onclick(View button) {
        switch (button.getId()){
            case R.id.btnEnviar:

                int agregados = 0;
                for( int i=0; i<mediaLista.size(); i++ ){
                    if (mediaLista.get(i).getIdmedia() == 0) {
                        agregados++;
                    }
                }

                if( agregados > 0 ){
                    incidente.setListMultimedia(this.mediaLista);
                    wsActualizarIncidente(incidente);
                }
                else{
                    Toast.makeText(DetalleIncidActivity.this, "Por favor agregue una imagen o video para continuar", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnMasArchivos:
                showFileChooser();break;
            case R.id.cvFoto:
                Intent intent=new Intent(DetalleIncidActivity.this,MapsActivity.class);
                intent.putExtra("latitud",incidente.getLatitud());
                intent.putExtra("longitud",incidente.getLongitud());
                intent.putExtra("polyline",incidente.getPolilyne());
                startActivity(intent);
        }
    }

    /*******************ANTONY****************/


    /**
     * Servicio para actualizar media de un incidente
     * @param incidente - obj incidente con los detalles de la incidencia
     */
    private void wsActualizarIncidente(final Incidente incidente) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(DetalleIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(DetalleIncidActivity.this,"Actualizando...");
        progressDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.param_servc_id_incidente), incidente.getIdIncidente());

            JSONArray jsonArrayMM = new JSONArray();
            for( int i=0; i<mediaLista.size(); i++ ) {
                if( incidente.getListMultimedia().get(i).getIdmedia() == 0 ) {
                    JSONObject objectMM = new JSONObject();
                    objectMM.put(getString(R.string.param_servc_tipo_media), incidente.getListMultimedia().get(i).getTipo());
                    objectMM.put(getString(R.string.param_servc_media_file), incidente.getListMultimedia().get(i).getStrBase64());
                    objectMM.put(getString(R.string.param_servc_incid_media_name), incidente.getListMultimedia().get(i).getFile_name());
                    //object.accumulate(getString(R.string.param_servc_media), objectMM);
                    jsonArrayMM.put(objectMM);
                }
            }
            object.put(getString(R.string.param_servc_media), jsonArrayMM);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(104), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("success");
                    Toast.makeText(DetalleIncidActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();

                } catch (JSONException error) {
                    error.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
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
                        agregado=1;
                    }
                }
            }


        });
    }



    private void cargarFormatosPermitidos(){
        //( extension, tipo )
        // imagenes
        formatosMultiMedia[0][0] = "png";
        formatosMultiMedia[0][1] = "Imagen";

        formatosMultiMedia[1][0] = "jpg";
        formatosMultiMedia[1][1] = "Imagen";

        formatosMultiMedia[2][0] = "jpeg";
        formatosMultiMedia[2][1] = "Imagen";

        // videos
        formatosMultiMedia[3][0] = "mp4";
        formatosMultiMedia[3][1] = "Video";
    }





    /**
     * Codifica un archivo a base64
     * @param bitmap
     * @return
     */
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        String imgString = "";
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
            imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        }
        catch (OutOfMemoryError  ex) {
            Log.i("PESOOOOOOOO:::: ", ""+ex.getMessage());
            Toast.makeText(DetalleIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return imgString;
    }

    /**
     * Codifica un video a base64
     * @param path
     * @return
     */
    public String getEncoded64VideoStringFromBitmap(String path){
        //Log.i("pathpathpathpath===", path);
        String encodedString = "";
        try {

            // Before doing something that requires a lot of memory,
            // check to see whether the device is in a low memory state.
            ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

            if (!memoryInfo.lowMemory) {
                // Do memory intensive work ...
                Log.i("MEMORIA::", "DISPONIBLE:::"+memoryInfo.availMem);
                Log.i("MEMORIA::", "LIMITEE===:::"+memoryInfo.threshold);

                // https://stackoverflow.com/questions/26842768/how-to-decode-video-from-base64
                // https://gist.github.com/utsengar/1276960/9e99b409e8d57c624e2a16793faa9c16e3745db2
                // https://developer.android.com/topic/performance/memory
                // https://github.com/coocood/oom-research/blob/master/oomresearch/src/net/coocood/oomresearch/MainActivity.java
                // https://www.programcreek.com/java-api-examples/?class=android.app.ActivityManager&method=getMemoryInfo
                // https://medium.com/@ssaurel/learn-to-get-memory-info-at-runtime-on-android-84168d8c73d
                // https://stackoverflow.com/questions/2298208/how-do-i-discover-memory-usage-of-my-application-in-android
                //Encode Video To String With mig Base64. /storage/emulated/0/DCIM/Camera/VID_20180705_182453_260.mp4
                File tempFile = new File(path);
                //File tempFile = new File("/storage/emulated/0/DCIM/Camera/VID_20180705_182453_260.mp4");
                InputStream inputStream = new FileInputStream(tempFile);

                byte[] bytes;
                //Log.i("====80000000====","==="+(int)tempFile.length() / (1024 * 1024) ); MB
                Log.i("====80000000====","==="+(int)tempFile.length());
                byte[] buffer = new byte[(int)tempFile.length()];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                bytes = output.toByteArray();
                encodedString = Base64.encodeToString(bytes, 0);

                bytes = null;
                buffer = null;
            }
            else{
                Toast.makeText(DetalleIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError  ex) {
            Log.i("PESOOOOOOOO:::: ", ""+ex.getMessage());
            Toast.makeText(DetalleIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encodedString;

        //Decode String To Video With mig Base64.
        /*byte[] decodedBytes = Base64.decode(encodedString.getBytes(), 0);
        try {
            //FileOutputStream out = new FileOutputStream( Environment.getExternalStorageDirectory()  + "/my/Convert.mp4");
            //FileOutputStream out = new FileOutputStream( "/storage/emulated/0/DCIM/Camera/anthony.mp4");
            FileOutputStream out = new FileOutputStream( path );
            out.write(decodedBytes);
            out.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Error", e.toString());

        }*/
    }



    /**
     * Get a MemoryInfo object for the device's current memory status.
     * @return
     */
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }




    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    //method to show file chooser
    private void showFileChooser() {
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        startActivityForResult(pickIntent, PICK_IMAGE_REQUEST);
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(DetalleIncidActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(DetalleIncidActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(DetalleIncidActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(DetalleIncidActivity.this, Manifest.permission.CAMERA)&&
                ActivityCompat.shouldShowRequestPermissionRationale(DetalleIncidActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(DetalleIncidActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(DetalleIncidActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == DetalleIncidActivity.this.RESULT_OK && data != null && data.getData() != null) {
            //Uri selectedMediaUri = data.getData();
            filePath = data.getData();
            String encodedImageData = "";
            String path = "";
            boolean peso=false;
            if (filePath.toString().contains("image")) {
                //handle image
                Log.i("idxExt:::","image");

                //filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(DetalleIncidActivity.this.getContentResolver(), filePath);
                    //imageView.setImageBitmap(bitmap);

                    encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
                    int tam=Metodos.byteSizeImage(bitmap);
                    if (tam<3145728){
                        peso=true;
                    }
                    path = getPath(filePath);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


            }
            else if (filePath.toString().contains("video")) {
                Log.i("idxExt:::","video:::");
                //handle video
                //String path = getPath(filePath);
                // content://media/external/video/media/156
                //Log.i("idxExt:::","video:::"+path);

                //File myFile = new File(filePath.getPath());
                //Log.i("idxExt:::","video:::"+getRealVideoPathFromURI( this.getContentResolver(), filePath ));
                // /storage/emulated/0/DCIM/Camera/VID_20180705_182453_260.mp4
                path = getRealVideoPathFromURI( this.getContentResolver(), filePath );
                long tamanio=Metodos.byteSizeVideo(DetalleIncidActivity.this,filePath);
                if (tamanio<3145728){
                    peso=true;
                }
                encodedImageData = getEncoded64VideoStringFromBitmap(path);
            }

            if( !encodedImageData.equals("") ) {
                if (peso) {

                    Multimedia objMM = new Multimedia();
                    objMM.setBitmap(bitmap);
                    objMM.setStrBase64(encodedImageData);
                    // OBTENER IMG
                    String name = "";
                    name = path;
                    // /storage/emulated/0/Pictures/Screenshots/Screenshot_2018-07-02-00-08-27.png
                    int index = path.lastIndexOf("/");
                    if (index > -1) {
                        name = path.substring(index + 1, path.length());
                    }
                    Log.i("path===", path);
                    //////////////////////////
                    objMM.setFile_name(name);
                    objMM.setPath_file(path);
                    String tipo = getTipoArchivo(name);
                    objMM.setTipo(tipo);
                    objMM.setIdmedia(0);
                    objMM.setEliminar(1);
                    mediaLista.add(objMM);
                    //mAdapterMM.notifyDataSetChanged();
                    rvMedia.setAdapter(new CardDetalleIncidenciaM(mediaLista, DetalleIncidActivity.this, 0));
                }else{
                    Toast.makeText(DetalleIncidActivity.this,"Peso máximo de cada archivo multimedia es: 3mb",Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            onCaptureImageResult(data);

            //img.setImageBitmap(photo);
        }

    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        obtenerFoto(destination,data);
    }

    public void obtenerFoto(File file, Intent data){
        Uri filePath= getImageContentUri(DetalleIncidActivity.this,file);
        String encodedImageData = "";
        String path = "";
        try {
            bitmap = MediaStore.Images.Media.getBitmap(DetalleIncidActivity.this.getContentResolver(), filePath);
            encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            path = getPath(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if( !encodedImageData.equals("") ) {
            Multimedia objMM = new Multimedia();
            objMM.setBitmap(bitmap);
            objMM.setStrBase64(encodedImageData);
            String name = "";
            name = path;
            int index = path.lastIndexOf("/");
            if (index > -1) {
                name = path.substring(index + 1, path.length());
            }
            objMM.setFile_name(name);
            objMM.setPath_file(path);
            String tipo = getTipoArchivo(name);
            objMM.setTipo(tipo);
            objMM.setEliminar(1);
            mediaLista.add(objMM);

            //mAdapterMM.notifyDataSetChanged();
            rvMedia.setAdapter(new CardDetalleIncidenciaM(mediaLista,DetalleIncidActivity.this,0));
            rvMedia.setLayoutManager(horizontalLayoutManagaer);

        }
    }

    /**
     * *
     * @param context actividad
     * @param imageFile el archivo que se guardo
     * @return devuelve donde se guardo el archivo
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    /**
     *  Obtiene el tipo de archivo segùn la extension del mismo
     * @param file - nombre del archivo
     * @return tipo del archivo, sino coincide con algunos de los tipos permitos, devolvera un texto por defecto
     */
    private String getTipoArchivo(String file){
        String type = "Formato no soportado";
        String extension = "";
        try{
            int idxExt = file.lastIndexOf(".");
            Log.i("fillle:::",file);
            Log.i("idxExt:::",String.valueOf(idxExt));
            if( idxExt > -1 ){

                extension = file.substring(idxExt+1, file.length());
                Log.i("extension:::",extension);
                for( int i=0; i<formatosMultiMedia.length; i++ ){
                    if( formatosMultiMedia[i][0].equals(extension)){
                        type = formatosMultiMedia[i][1];
                        break;
                    }
                }
            }
        }
        catch ( Exception e ){

        }

        return  type;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(DetalleIncidActivity.this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(DetalleIncidActivity.this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * method to get the file path from uri - images
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = DetalleIncidActivity.this.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = DetalleIncidActivity.this.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        catch ( CursorIndexOutOfBoundsException e ){
            Log.i("===============", e.getMessage());
        }
        return path;
    }

    /**
     * method to get the file path from uri - videos
     * @param contentResolver
     * @param contentURI
     * @return
     */
    public String getRealVideoPathFromURI(ContentResolver contentResolver, Uri contentURI) {
        Cursor cursor = contentResolver.query(contentURI, null, null, null,
                null);
        if (cursor == null)
            return contentURI.getPath();
        else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            try {
                return cursor.getString(idx);
            } catch (Exception exception) {
                return "";
            }
        }
    }


    private void webservicesListarIncidenciaId(final Incidente incid) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(DetalleIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(DetalleIncidActivity.this,"Cargando datos");
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
                    // no se porque aqui, si ya esta abajo (doble llamada)
                    //llenarDatos(incidente);

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
                    if( incidente != null ){
                        llenarDatos(incidente);
                    }
                }
            }


        });
    }

    public void shoDialogOpcion(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_opcion);
        Button btnTomar=(Button) dialog.findViewById(R.id.btnTomar);
        Button btnGaleria=(Button) dialog.findViewById(R.id.btnGaleria);

        btnTomar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                showCamera();
                dialog.dismiss();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    @SuppressLint("NewApi")
    private void showCamera(){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED&& checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_APN_SETTINGS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
}
