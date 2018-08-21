package com.adyl.aylludamos.activity;

import  com.adyl.aylludamos.utils.SeekbarWithIntervals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.adapters.CardDetalleIncidenciaM;
import com.adyl.aylludamos.adapters.MultimediaAdapter;
import com.adyl.aylludamos.adapters.NivelAguaAdapter;
import com.adyl.aylludamos.adapters.SpinnerUrbanizacionAdapter;
import com.adyl.aylludamos.adapters.TipoObstaculoAdapter;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.beans.Multimedia;
import com.adyl.aylludamos.beans.NivelAgua;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.beans.TiposObstaculo;
import com.adyl.aylludamos.beans.Urbanizacion;
import com.adyl.aylludamos.fragments.NuevoIncidenteFragment;
import com.adyl.aylludamos.services.ConexionInterna;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.adyl.aylludamos.utils.CustomSearchableSpinner;
import com.adyl.aylludamos.utils.SeekbarWithIntervals;
import com.adyl.aylludamos.utils.TouchImageViewHelper;
import com.adyl.aylludamos.utils.VolleyRequest.CustomVolleyRequest;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import android.Manifest;
import android.widget.VideoView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegistrarIncidActivity extends AppCompatActivity {
    int verificacion=0;
    Persona persona = null;
    private boolean estado=false; // para validar el servicio
    private SpinnerUrbanizacionAdapter spinnerUrbanizacionAdapter;

    private List<Urbanizacion> urbanizacionList = new ArrayList<>();
    //private ArrayList<TiposObstaculo> listTipoObs = new ArrayList<>();
    private ArrayList<NivelAgua> listNivelAgua = new ArrayList<>();
    private ArrayList<TiposObstaculo> listTipoObstaculo = new ArrayList<>();
    private ArrayList<Multimedia> listMultimedia = new ArrayList<>();

    private String formatosMultiMedia[][] = new String[5][2];

    private Urbanizacion urbanizacion = null;
    private WebServices webServices = null;
    NivelAguaAdapter mAdapter = null;
    TipoObstaculoAdapter mAdapterTO = null;
    //MultimediaAdapter mAdapterMM = null;
    CardDetalleIncidenciaM mAdapterMM = null;
    LinearLayoutManager horizontalLayoutManagaer;


    @BindView(R.id.txtDirecElegida)
    TextView txtDirecElegida;

    @BindView(R.id.radioGroup2)
    RadioGroup radioGroup2;

    @BindView(R.id.rdInundacion)
    RadioButton rdInundacion;

    @BindView(R.id.rdObstaculizacion)
    RadioButton rdObstaculizacion;

    @BindView(R.id.txtTituloOpt)
    TextView txtTituloOpt;

    @BindView(R.id.lblErrorMedia)
    TextView lblErrorMedia;

    @BindView(R.id.gvTiposObstaculo)
    GridView gvTiposObstaculo;

    /*@BindView(R.id.gvMultimedia)
    GridView gvMultimedia;*/

    @BindView(R.id.rvMedia)
    RecyclerView rvMedia;

    @BindView(R.id.cntInundacion)
    LinearLayout cntInundacion;
    @BindView(R.id.cntObstaculizacion)
    LinearLayout cntObstaculizacion;


    @BindView(R.id.imgPersInundada)
    ImageView imgPersInundada;

    @BindView(R.id.sbNivelInund)
    SeekBar sbNivelInund;


    @BindView(R.id.spUrbanizacion)
    CustomSearchableSpinner spUrbanizacion;

    @BindView(R.id.txtDescripcion)
    EditText txtDescripcion;


    @BindView(R.id.btnMasArchivos)
    ImageView btnMasArchivos;

    /*@BindView(R.id.imageView)
    ImageView imageView;*/

    @BindView(R.id.btnRegresar)
    Button btnRegresar;

    @BindView(R.id.btnEnviar)
    Button btnEnviar;

    /*@BindView(R.id.txtImg)
    EditText txtImg;*/

    @BindView(R.id.rvNivelAgua)
    ListView rvNivelAgua;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txtDescNroCantEnvMultimedia)
    TextView lblMsjeMultimedia;




    int nivelAguaSeleccionado = 0;
    /**
     * INUNDACION = 1
     * OBSTACULIZACION = 2
     */
    int idTipoIncidencSelec = 0;

    String direcSelec = "";
    double latitudSelec = 0;
    double longitudSelec = 0;

    int cantArchivPerm = 0;
    private ImageLoader imageLoader;
    String msjPesoCantPerm = "";

    //private SeekbarWithIntervals SeekbarWithIntervals = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regincidencia);
        ButterKnife.bind(this);
        webServices=new WebServices(RegistrarIncidActivity.this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("NUEVA INCIDENCIA");
        }
        setSupportActionBar(toolbar);

        horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        Bundle bundle = getIntent().getExtras();
        direcSelec  = bundle.getString("direccion");
        latitudSelec = Double.parseDouble(bundle.getString("latitude"));
        longitudSelec = Double.parseDouble(bundle.getString("longitude"));

        persona = Select.from(Persona.class).first();

        txtDirecElegida.setText(direcSelec);
        spUrbanizacion.setTitle("Urbanización");
        spUrbanizacion.setPositiveButton("CERRAR");

        // inicializamos el incidente en INUNDACION
        idTipoIncidencSelec = 1;

        rdInundacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTipoIncidencSelec = 1;
                cntInundacion.setVisibility(View.VISIBLE);
                cntObstaculizacion.setVisibility(View.GONE);
                txtTituloOpt.setText("PROFUNDIDAD DEL AGUA");
            }
        });

        rdObstaculizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTipoIncidencSelec = 2;
                cntInundacion.setVisibility(View.GONE);
                cntObstaculizacion.setVisibility(View.VISIBLE);
                txtTituloOpt.setText("TIPOS DE OBSTACULACIÓN");
            }
        });

        spUrbanizacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spUrbanizacion.isSpinnerDialogOpen = false;
                // este obj es la urb seleccionada
                urbanizacion = spinnerUrbanizacionAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sbNivelInund.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                /*if( progress == 0 ) {
                    seekBar.setProgress(1);
                    nivelAguaSeleccionado = listNivelAgua.get(0).getIdNivel();
                }*/
                if( progress > 0 ) {
                    int index = buscarNivelAgua(progress);
                    int maxNivel = 0;
                    if (index >= 0) {
                        String prog[] = listNivelAgua.get(index).getProgreso().split(",");
                        maxNivel = Integer.parseInt(prog[prog.length - 1]);
                        //aumento = listNivelAgua.get(index).getAlturaItem()/listNivelAgua.get(index).getAlturaPorNivel() - 1;
                        //if( maxNivel > 0 ){
                        if (progress != maxNivel) {
                            seekBar.setProgress(maxNivel);
                        }
                    }

                    //txtTituloOpt.setText("" + progress + "%" + "/aumento/"+maxNivel + "/INDEX/" + index);
                    nivelAguaSeleccionado = listNivelAgua.get(index).getIdNivel();
                    //imgPersInundada.setImageResource(listNivelAgua.get(index).getImagenBg());

                    Metodos.GlideImagenLogoDefault(RegistrarIncidActivity.this, listNivelAgua.get(index).getImg_url(), imgPersInundada);

                    /*imageLoader = CustomVolleyRequest.getInstance(RegistrarIncidActivity.this)
                            .getImageLoader();
                    imageLoader.get(listNivelAgua.get(index).getImg_url(), ImageLoader.getImageListener(imgPersInundada,
                            R.drawable.agregar, android.R.drawable.ic_dialog_alert));
                    imgPersInundada.setImageUrl(listNivelAgua.get(index).getImg_url(), imageLoader);*/
                }
                else{
                    nivelAguaSeleccionado = 0;
                    imgPersInundada.setImageResource(R.drawable.nivel_cero);
                }

                /*if( progress == 4 ){
                    seekBar.setProgress(5);
                }
                if( progress == 6 ){
                    seekBar.setProgress(7);
                }*/

                //nivelAguaSeleccionado = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( nivelAguaSeleccionado > 0 ) {
                    for( int i=0; i<listNivelAgua.size(); i++ ){
                        if ( listNivelAgua.get(i).getIdNivel() == nivelAguaSeleccionado ){
                            Toast.makeText(RegistrarIncidActivity.this, listNivelAgua.get(i).getDescripcion(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        });

        //Requesting storage permission
        requestStoragePermission();

        //txtImg.setText("");
        webservicesListarUrbanizacion();
        wsListarNivelesAgua();
        wsListarTipoObstaculo();
        wsGetCantArchivosPermitidos();
        cargarFormatosPermitidos();

        /*mAdapterMM = new MultimediaAdapter(listMultimedia,RegistrarIncidActivity.this);
        gvMultimedia.setAdapter(mAdapterMM);
        mAdapterMM.notifyDataSetChanged();*/
        /////////

        rvMedia.setHasFixedSize(true);
        RecyclerView.LayoutManager llm = new LinearLayoutManager(this);
        rvMedia.setLayoutManager(llm);

        mAdapterMM = new CardDetalleIncidenciaM(listMultimedia,RegistrarIncidActivity.this,1);
        rvMedia.setAdapter(mAdapterMM);
        mAdapterMM.notifyDataSetChanged();
        rvMedia.setLayoutManager(horizontalLayoutManagaer);

       // List<String> seekbarIntervals = getIntervals();
       // getSeekbarWithIntervals().setIntervals(seekbarIntervals);
        // Seteamos mensaje de archivos y peso de los mismos

    }

/**
    private List<String> getIntervals() {
        return new ArrayList<String>() {{
            add("Pies");
            add("Rodillas");
            add("Cintura");
            add("Torax");
            add("Cabez");
        }};
    }

    private SeekbarWithIntervals getSeekbarWithIntervals() {
        if (SeekbarWithIntervals == null) {
            SeekbarWithIntervals = (SeekbarWithIntervals) findViewById(R.id.seekbarWithIntervals);
        }

        return SeekbarWithIntervals;
    }*/



    /**
     * Busca la info del nivel de agua,
     * segun el progreso seleccionado del seekbar
     * @param progress - progreso del seekbar
     * @return devuelve index de la lista de niveles de agua
     */
    private int buscarNivelAgua( int progress ){
        int index = -1;
        boolean found = false;
        try{
            for( int i=0; i<listNivelAgua.size(); i++ ){
                String prog[] = listNivelAgua.get(i).getProgreso().split(",");

                for( int j=0; j<prog.length; j++ ){
                    if( Integer.parseInt(prog[j]) == progress ){
                        // listNivelAgua.get(i).getAlturaItem()/listNivelAgua.get(i).getAlturaPorNivel() - 1;
                        index = i;
                        found = true;
                        break;
                    }
                }

                if( found ){
                    break;
                }
            }
        }
        catch (Exception e){
            index = -1;
        }

        return index;
    }


    /**
     * Carga arrglo de formato de archivos multimedia permitidos
     */
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



    @OnClick(R.id.btnMasArchivos)
    public void onBtnMasArchivos(View view) {
        if( listMultimedia.size() < cantArchivPerm ) {
           shoDialogOpcion(RegistrarIncidActivity.this);
        }
        else{
            Toast.makeText(RegistrarIncidActivity.this, "Llegó al límite de archivos permitidos", Toast.LENGTH_LONG).show();
        }
    }


    @OnClick(R.id.btnRegresar)
    public void onBtnRegresar(View view) {
        NuevoIncidenteFragment.veces = 0;
        this.finish();

        /*for( int i=0; i<listMultimedia.size(); i++ ){
            Log.i("getFile_name::",listMultimedia.get(i).getFile_name());
            Log.i("getStrBase64::",listMultimedia.get(i).getStrBase64());
        }*/
    }


    @OnClick(R.id.btnEnviar)
    public void onBtnEnviar(View view) {
        verificacion=0;
        if( validarForm() ){
            /*Calendar fecha = Calendar.getInstance();
            int anio = fecha.get(Calendar.YEAR);
            int mes = fecha.get(Calendar.MONTH) + 1;
            int dia = fecha.get(Calendar.DAY_OF_MONTH);
            String fecAct = String.valueOf(anio) + "-" + mes + "-" + dia;*/

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fecAct = sdf.format(new Date());


            Incidente incidente = new Incidente();
            incidente.setFecha(fecAct);
            incidente.setDescripcion(txtDescripcion.getText().toString().trim());
            incidente.setDireccion(direcSelec);
            incidente.setLatitud(latitudSelec);
            incidente.setAltitud(longitudSelec);
            incidente.setIdUrb(urbanizacion.getIdUrbanizacion());
            incidente.setCreador(String.valueOf(persona.getIdPersona()));
            incidente.setTipo(String.valueOf(idTipoIncidencSelec));
            incidente.setIdNivelAgua(nivelAguaSeleccionado);
            incidente.setTipoObstaculo(mAdapterTO.getSelectedItemSelected());
            incidente.setListMultimedia(this.listMultimedia);
            wsRegistrarIncidente(incidente);

        }else{
            switch(verificacion){
                case  1:
                    Toast.makeText(RegistrarIncidActivity.this,"Seleccione la profundidad del agua",Toast.LENGTH_SHORT).show();
                    break;
                case  2:
                    Toast.makeText(RegistrarIncidActivity.this,"Seleccione el tipo de obstaculización",Toast.LENGTH_SHORT).show();
                    break;
                case  3:
                    Toast.makeText(RegistrarIncidActivity.this,"Ingrese imágenes o videos",Toast.LENGTH_SHORT).show();
                    break;
            }
            verificacion=0;
        }
    }





    /**
     * valida formulario
     * @return - devuelve true si ha ingresado los datos obligatorios
     * sino false
     */
    public  boolean validarForm(){
        boolean retorno = false;

        if ( txtDirecElegida.getText().toString().trim().equals("") ){
            txtDirecElegida.setError("Seleccione una Dirección");
            txtDirecElegida.requestFocus();
            return false;
        }
        else{
            txtDirecElegida.setError(null);
            txtDirecElegida.setVisibility(View.VISIBLE);
            retorno=true;
        }

        // si es inundacion
        if( idTipoIncidencSelec == 1 ){
            // verificar  que minimo sea pies
            if (nivelAguaSeleccionado>0){
                retorno=true;
            }else{
                verificacion=1;
               return false;
            }
        }

        // si es obstaculizacion
        if( idTipoIncidencSelec == 2 ){
            if (mAdapterTO.getSelectedItemIndex() == -1) {
                verificacion=2;
                //gvTiposObstaculo.setError("Seleccione un Tipo de Obstaculización");
                gvTiposObstaculo.requestFocus();
                return false;
            } else {
                //gvTiposObstaculo.setError(null);
                gvTiposObstaculo.setVisibility(View.VISIBLE);
                retorno = true;
            }
        }

        /*if ( spUrbanizacion.getText().toString().trim().equals("") ){
            spUrbanizacion.setError("Seleccione una dirección");
            spUrbanizacion.requestFocus();
            return false;
        }
        else{
            spUrbanizacion.setError(null);
            spUrbanizacion.setVisibility(View.VISIBLE);
            retorno=true;
        }*/


        if ( txtDescripcion.getText().toString().trim().equals("") ){
            txtDescripcion.setError("Ingrese una descripción");
            txtDescripcion.requestFocus();
            return false;
        }
        else{
            txtDescripcion.setError(null);
            txtDescripcion.setVisibility(View.VISIBLE);
            retorno=true;
        }


        if ( listMultimedia.size() == 0 ){
            verificacion=3;
            lblErrorMedia.setError("Ingrese Imágenes o videos");
            lblErrorMedia.setText("Ingrese Imágenes o videos");
            lblErrorMedia.requestFocus();
            lblErrorMedia.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            lblErrorMedia.setError(null);
            lblErrorMedia.setVisibility(View.GONE);
            retorno=true;
        }


        return  retorno;
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
            Toast.makeText(RegistrarIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
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
                Toast.makeText(RegistrarIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError  ex) {
            Log.i("PESOOOOOOOO:::: ", ""+ex.getMessage());
            Toast.makeText(RegistrarIncidActivity.this, "Memoria insuficiente para cargar dicho archivo", Toast.LENGTH_LONG).show();
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




    /** Read the given binary file, and return its contents as a byte array.*/
    byte[] read(String aInputFileName){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            Log.i("","Reading in binary file named : " + aInputFileName);
            File file = new File(aInputFileName);
            Log.i("File size:::","File size: " + file.length());

            byte[] result;
            result = new byte[(int) file.length()];

            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length){
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0){
                        totalBytesRead = totalBytesRead + bytesRead;

                        output.write(result, 0, bytesRead);
                    }
                }
                    /*
                     the above style is a bit tricky: it places bytes into the 'result' array;
                     'result' is an output parameter;
                     the while loop usually has a single iteration only.
                    */
                Log.i("","Num bytes read: " + totalBytesRead);
            }
            finally {
                Log.i("","Closing input stream.");
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            Log.i("","File not found.");
        }
        catch (IOException ex) {
            Log.i("", ""+ex);
        }
        catch (OutOfMemoryError  ex) {
            Log.i("PESOOOOOOOO:::: ", ""+ex.getMessage());
        }
        return output.toByteArray();
    }




    // INICIOOOOOOOOOOOOOO
    //Image request code
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

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        startActivityForResult(pickIntent, PICK_IMAGE_REQUEST);
    }

    @SuppressLint("NewApi")
    private void showCamera(){
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED&& checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_APN_SETTINGS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_PERMISSION_CODE);
            }
            else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
        else {
            // Pre-Marshmallow
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(RegistrarIncidActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(RegistrarIncidActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(RegistrarIncidActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrarIncidActivity.this, Manifest.permission.CAMERA)&&
        ActivityCompat.shouldShowRequestPermissionRationale(RegistrarIncidActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
        ActivityCompat.shouldShowRequestPermissionRationale(RegistrarIncidActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(RegistrarIncidActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RegistrarIncidActivity.this.RESULT_OK && data != null && data.getData() != null) {
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
                    bitmap = MediaStore.Images.Media.getBitmap(RegistrarIncidActivity.this.getContentResolver(), filePath);
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
                long tamanio=Metodos.byteSizeVideo(RegistrarIncidActivity.this,filePath);
                if (tamanio<3145728){
                    peso=true;
                }
                encodedImageData = getEncoded64VideoStringFromBitmap(path);
                /*byte[] fileContents  = read(path);
                encodedImageData = Base64.encodeToString(fileContents, 0);*/
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
                    objMM.setEliminar(1);

                    // Log.i("name===", name);
                    //Log.i("tipo===", tipo);

                    listMultimedia.add(objMM);

                    mAdapterMM.notifyDataSetChanged();
                    rvMedia.setLayoutManager(horizontalLayoutManagaer);

                    lblErrorMedia.setError(null);
                    lblErrorMedia.setVisibility(View.GONE);
                    lblErrorMedia.setText("");
                }else{
                    Toast.makeText(RegistrarIncidActivity.this,"Peso máximo de cada archivo multimedia es: 3mb",Toast.LENGTH_SHORT).show();
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
        Uri filePath= getImageContentUri(RegistrarIncidActivity.this,file);
        String encodedImageData = "";
        String path = "";
        try {
            bitmap = MediaStore.Images.Media.getBitmap(RegistrarIncidActivity.this.getContentResolver(), filePath);
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
            listMultimedia.add(objMM);

            mAdapterMM.notifyDataSetChanged();
            rvMedia.setLayoutManager(horizontalLayoutManagaer);

            lblErrorMedia.setError(null);
            lblErrorMedia.setVisibility(View.GONE);
            lblErrorMedia.setText("");
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
                Toast.makeText(RegistrarIncidActivity.this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(RegistrarIncidActivity.this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

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
     * method to get the file path from uri - images
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        String path = "";
        try {
            Cursor cursor = RegistrarIncidActivity.this.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = RegistrarIncidActivity.this.getContentResolver().query(
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
    public String getRealVideoPathFromURI(ContentResolver contentResolver,  Uri contentURI) {
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



    /**
     * Servicio para registrar un incidente
     * @param incidente - obj incidente con los detalles de la incidencia
     */
    private void wsRegistrarIncidente(final Incidente incidente) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarIncidActivity.this,"Registrando...");
        progressDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.param_servc_fecha), incidente.getFecha());
            object.put(getString(R.string.param_servc_descripcion), incidente.getDescripcion());
            object.put(getString(R.string.param_servc_direccion), incidente.getDireccion());
            object.put(getString(R.string.param_servc_latitud), incidente.getLatitud());
            object.put(getString(R.string.param_servc_longitud), incidente.getAltitud());
            object.put(getString(R.string.param_servc_imagen_file), "");
            object.put(getString(R.string.param_servc_urb), incidente.getIdUrb());
            object.put(getString(R.string.param_servc_persona), incidente.getCreador());
            object.put(getString(R.string.param_servc_estado_incidente), "1");
            object.put(getString(R.string.param_servc_tipo_incidente), incidente.getTipo());
            object.put(getString(R.string.param_servc_tipousuario), persona.getIdTipo());

            JSONObject objectNA = new JSONObject();
            objectNA.put(getString(R.string.param_servc_nivel_agua), incidente.getIdNivelAgua());
            //object.put(getString(R.string.param_servc_tipo_inundacion), objectNA);
            object.accumulate(getString(R.string.param_servc_tipo_inundacion), objectNA);


            JSONObject objectTO = new JSONObject();
            objectTO.put(getString(R.string.param_servc_tipo_obstaculo), incidente.getTipoObstaculo());
            object.accumulate(getString(R.string.param_servc_calle_obstaculo), objectTO);


            JSONArray jsonArrayMM = new JSONArray();
            for( int i=0; i<listMultimedia.size(); i++ ) {
                JSONObject objectMM = new JSONObject();
                objectMM.put(getString(R.string.param_servc_tipo_media), incidente.getListMultimedia().get(i).getTipo());
                objectMM.put(getString(R.string.param_servc_media_file), incidente.getListMultimedia().get(i).getStrBase64());
                objectMM.put(getString(R.string.param_servc_incid_media_name), incidente.getListMultimedia().get(i).getFile_name());
                //object.accumulate(getString(R.string.param_servc_media), objectMM);
                jsonArrayMM.put(objectMM);
            }
            object.put(getString(R.string.param_servc_media), jsonArrayMM);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(103), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("success");
                    Toast.makeText(RegistrarIncidActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();

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
                        // limpiar form
                        txtDirecElegida.setText("");
                        idTipoIncidencSelec = 1;

                        sbNivelInund.setProgress(1); // nivel 1 - pies
                        nivelAguaSeleccionado = listNivelAgua.get(0).getIdNivel(); // codigo 1 - pies

                        txtDescripcion.setText("");
                        listMultimedia = null;
                        listMultimedia = new ArrayList<>();

                        NuevoIncidenteFragment.veces = 0;
                        MenuActivity.accion = "MostrarPincipal";
                        finish();
                    }
                }
            }


        });
    }




    /**
     * Servicio para obtener los tipos de obstaculo desde bd
     */
    private void wsListarTipoObstaculo() {
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarIncidActivity.this,"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(102), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes =response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            TiposObstaculo objTO = new TiposObstaculo();
                            objTO.setIdTipo(jsonObject.getInt("id"));
                            objTO.setNombre(jsonObject.getString("descripcion"));
                            objTO.setImagen(jsonObject.getString("src_imagen"));

                            listTipoObstaculo.add(objTO);
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
                if (Metodos.VerificarLista(listTipoObstaculo)){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();

                        mAdapterTO = new TipoObstaculoAdapter(listTipoObstaculo,RegistrarIncidActivity.this);
                        gvTiposObstaculo.setAdapter(mAdapterTO);
                    }
                }
            }


        });
    }



    private int alturaListaNA = 0;
    private int alturaPorNivel = 0;

    /**
     * Servicio para obtener los niveles de agua desde bd
     */
    private void wsListarNivelesAgua() {
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarIncidActivity.this,"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(101), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes = response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){

                        String progreso = "";
                        int ultimoProg = 0;
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            NivelAgua objNA = new NivelAgua();
                            objNA.setIdNivel(jsonObject.getInt("id"));
                            objNA.setDescripcion(jsonObject.getString("descripcion"));
                            objNA.setImg_url(jsonObject.getString("src_imagen"));

                            alturaPorNivel = objNA.getAlturaPorNivel();
                            switch ( objNA.getIdNivel() ){
                                case 1:
                                    objNA.setAlturaItem(50); // altura normal
                                    objNA.setImagenBg(R.drawable.nivel_pies);
                                    break;
                                case 2:
                                    objNA.setAlturaItem(50); // altura normal
                                    objNA.setImagenBg(R.drawable.nivel_rodillas);
                                    break;
                                case 3:
                                    objNA.setAlturaItem(50); // altura normal
                                    objNA.setImagenBg(R.drawable.nivel_cintura);
                                    break;
                                case 4:
                                    objNA.setAlturaItem(100); // doble altura
                                    objNA.setImagenBg(R.drawable.nivel_torax);
                                    break;
                                case 5:
                                    objNA.setAlturaItem(100); // doble altura
                                    objNA.setImagenBg(R.drawable.nivel_cabeza);
                                    break;
                                default:
                                    objNA.setAlturaItem(50); // altura normal
                                    break;
                            }

                            progreso = "";
                            for( int p=1; p<=objNA.getAlturaItem()/objNA.getAlturaPorNivel(); p++ ){
                                ultimoProg ++;
                                progreso = progreso + String.valueOf(ultimoProg) + ",";
                            }
                            // "1" - "2" - "3" - "4,5" - "6,7"
                            objNA.setProgreso( progreso.substring(0, progreso.length()-1) );
                            //objNA.setProgreso( i + (int)(objNA.getAlturaItem()/objNA.getAlturaPorNivel()) );

                            alturaListaNA = alturaListaNA + objNA.getAlturaItem();
                            listNivelAgua.add(objNA);
                        }

                        if (Metodos.VerificarLista(listNivelAgua)){
                            // para ordenarlo descendentemente
                            ArrayList<NivelAgua> listClon = (ArrayList<NivelAgua>)listNivelAgua.clone();
                            listNivelAgua  = new ArrayList<>();

                            for( int j=listClon.size()-1; j>=0; j-- ){
                                listNivelAgua.add(listClon.get(j));
                            }
                        }

                        Log.i("alturaListaNA::::", ""+alturaListaNA);
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
                if (Metodos.VerificarLista(listNivelAgua)){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        //spinnerUrbanizacionAdapter = new SpinnerUrbanizacionAdapter(RegistrarIncidActivity.this, android.R.layout.simple_spinner_dropdown_item, listNivelAgua);
                        //spUrbanizacion.setAdapter(spinnerUrbanizacionAdapter);

                        mAdapter = new NivelAguaAdapter(listNivelAgua,RegistrarIncidActivity.this);
                        rvNivelAgua.setAdapter(mAdapter);

                        Log.i("alturaListaNA::====::", ""+alturaListaNA);
                        ViewGroup.LayoutParams par = rvNivelAgua.getLayoutParams();
                        par.height = alturaListaNA + (rvNivelAgua.getDividerHeight() * (mAdapter.getCount() - 1));
                        rvNivelAgua.setLayoutParams(par);
                        rvNivelAgua.requestLayout();

                        Log.i("setMax::====::", ""+alturaListaNA / alturaPorNivel);
                        sbNivelInund.setMax(alturaListaNA / alturaPorNivel);
                        // 27.1 layout_width
                    }
                }
            }


        });
    }


    int idx = 0;
    String msg = "";
    /**
     * Servicio para obtener las urbanizaciones desde bd
     */
    private void webservicesListarUrbanizacion() {
        idx = 0;
        msg = "La urbazación a la que perteneces no tiene un Alcalde vecinal";
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarIncidActivity.this,"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(20), null
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

                            //Log.i("verificandoooooo====", ">>>>>" + urbanizacion.getIdUrbanizacion() + " == " + persona.getIdUrbanizacion());
                            if( urbanizacion.getIdUrbanizacion() == persona.getIdUrbanizacion() ){
                                idx = i;
                                msg = "";
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
                        spinnerUrbanizacionAdapter = new SpinnerUrbanizacionAdapter(RegistrarIncidActivity.this, android.R.layout.simple_spinner_dropdown_item, urbanizacionList);
                        spUrbanizacion.setAdapter(spinnerUrbanizacionAdapter);
                        spUrbanizacion.setSelection(idx);
                        if( !msg.equals("") ){
                            Toast.makeText(RegistrarIncidActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }


        });
    }


    /**
     * Servicio para obtener las CONFIGURACIONES
     */
    private void wsGetCantArchivosPermitidos() {
        RequestQueue requestQueue = Volley.newRequestQueue(RegistrarIncidActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(RegistrarIncidActivity.this,"Cargando datos");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest_incidencia =  new JsonArrayRequest(Request.Method.GET, webServices.Url(105), null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    JSONArray jsonarrayIncidentes =response;
                    if (Metodos.ValidarJsonArray(jsonarrayIncidentes)){
                        for (int i = 0; i <jsonarrayIncidentes.length() ; i++) {
                            JSONObject jsonObject= jsonarrayIncidentes.getJSONObject(i);

                            if( jsonObject.getString("nombre").trim().equals("numero_carga_archivos") ){
                                cantArchivPerm = jsonObject.getInt("valor");
                                lblMsjeMultimedia.setText("La cantidad máxima de arcivos multimedia es: " + Integer.toString(cantArchivPerm));
                                //Log.i("cantArchivPerm:::",""+cantArchivPerm);
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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                /*if (Metodos.VerificarLista(urbanizacionList)){
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        spinnerUrbanizacionAdapter = new SpinnerUrbanizacionAdapter(RegistrarIncidActivity.this, android.R.layout.simple_spinner_dropdown_item, urbanizacionList);
                        spUrbanizacion.setAdapter(spinnerUrbanizacionAdapter);
                    }
                }*/
            }


        });
    }




    @Override
    public void onBackPressed() {
        NuevoIncidenteFragment.veces = 0;
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NuevoIncidenteFragment.veces = 0;
        finish();
        return super.onSupportNavigateUp();
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

}
