package com.adyl.aylludamos.activity;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.fragments.DatosPersonalesFragment;
import com.adyl.aylludamos.fragments.DirectorioFragment;
import com.adyl.aylludamos.fragments.IncidentesFragment;
import com.adyl.aylludamos.fragments.MiFamiliaFragment;
import com.adyl.aylludamos.fragments.MisIncidentesFragment;
import com.adyl.aylludamos.fragments.MisIncidentesValidadosFragment;
import com.adyl.aylludamos.fragments.NuevoIncidenteFragment;
import com.adyl.aylludamos.fragments.RegistarUbicacion;
import com.adyl.aylludamos.fragments.Sugerencias;
import com.adyl.aylludamos.fragments.ValidaIncidenteFragment;
import com.adyl.aylludamos.services.ConexionInterna;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LENOVO on 24/06/2018.
 */

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Integer> idFragment;
    private List<String> nombreFragment;
    private Persona persona=null;
    private TipoUsuario tipoUsuario=null;
    public static boolean mMapIsTouched = false;
    WebServices webServices;

    public static String accion = "";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.flPrincipal)
    FrameLayout flPrincipal;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
   // @BindView(R.id.txtUsuario) TextView txtUsuario;
    //@BindView(R.id.cvFoto) CircleImageView cvFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        ButterKnife.bind(this);
        webServices=new WebServices(MenuActivity.this);
       if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("MENU");
        }

        persona= ConexionInterna.ultimoIngreso();
        interfaz();

       if (getIntent().getExtras()!=null){
            String palabra = (String) getIntent().getExtras()
                    .getSerializable(getString(R.string.parametro_evento));
            if (palabra != null) {
                int id = SacarIdMenu(palabra);
                Selecccionar(id, palabra, 1);
            } else {
                Selecccionar(R.id.Item_Incidentes, getString(R.string.itemIncidentes), 1);
            }
        }else{
            Selecccionar(R.id.Item_Incidentes, getString(R.string.itemIncidentes), 1);
        }


        MenuActivity.accion = "";
    }


    @Override
    protected void onResume() {
        super.onResume();

        if( MenuActivity.accion.equals("MostrarPincipal") ){
            Log.i("ACCCIONNNN=====","MostrarPincipal");
            Selecccionar(R.id.Item_Incidentes, getString(R.string.itemIncidentes), 1);
            MenuActivity.accion = "";
        }
    }

    private void interfaz(){
        flPrincipal.removeAllViews();
        idFragment = new ArrayList<>();
        nombreFragment = new ArrayList<>();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.activity_menu_usuario);
        TextView txtUser = (TextView) headerView.findViewById(R.id.txtUsuario);
        TextView txtCorreo = (TextView) headerView.findViewById(R.id.txtCorreo);
        CircleImageView fotoPerfil = (CircleImageView) headerView.findViewById(R.id.cvFoto);
        navigationView.setNavigationItemSelectedListener(this);
        if (persona!=null){
            txtUser.setText(persona.getNombre());
            txtCorreo.setText(persona.getEmail());
            tipoUsuario= ConexionInterna.tipo();
            if (tipoUsuario!=null){
                Metodos.GlideImagen(MenuActivity.this,tipoUsuario.getImagen(),fotoPerfil);

                /*switch (tipoUsuario.getImagen()){
                    case "bronce":
                        //fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.bronce));
                        fotoPerfil.setBackgroundResource(R.drawable.bronce);
                        break;
                    case "plata":
                        //fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.plata));
                        fotoPerfil.setBackgroundResource(R.drawable.plata);
                        break;
                    case "oro":
                        //fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.oro));
                        fotoPerfil.setBackgroundResource(R.drawable.oro);
                        break;
                    case "platiniun":
                        //fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.platino));
                        fotoPerfil.setBackgroundResource(R.drawable.platino);
                        break;
                    default:
                        //fotoPerfil.setImageDrawable(getResources().getDrawable(R.drawable.bronce));
                        fotoPerfil.setBackgroundResource(R.drawable.sinfoto_eliminar);
                        break;
                }*/


            }
            navigationView.getMenu().findItem(R.id.Item_ValidarIncidente).setVisible((persona.getIdTipo()!=2) ?true:false);
            navigationView.getMenu().findItem(R.id.Item_RegistrarUbicacion).setVisible((persona.getIdTipo()!=2) ?false:true);
            //navigationView.getMenu().findItem(R.id.Item_NuevoIncidente).setVisible((persona.getIdTipo()!=2) ?false:true);
            navigationView.getMenu().findItem(R.id.Item_NuevoIncidente).setVisible( usuPermitidosRegistIncidenc(persona.getIdTipo()) );
        }
    }


    public boolean usuPermitidosRegistIncidenc(int tipopers){
        boolean permitido = false;

        switch (tipopers){
            case 2: // ciudadano
            case 4: // alcalde vecinal
            case 5: // Personal Plandet
                permitido = true;
                break;
        }

        return permitido;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Item_Salir) {
            Salir();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            Selecccionar(item.getItemId(), item.getTitle().toString(), 1);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //validamos el arreglo del IdFragment si tiene mas de 1 dato o sino manda la opcion salir.
            if (idFragment.size() > 1) {
                idFragment.remove(idFragment.size() - 1);
                nombreFragment.remove(nombreFragment.size() - 1);
                Selecccionar(idFragment.get(idFragment.size() - 1),
                        nombreFragment.get(nombreFragment.size() - 1), 2);
            } else {
                finish();
            }
        }
    }

    private void Salir(){
        Persona.deleteAll(Persona.class);
        TipoUsuario.deleteAll(TipoUsuario.class);
        Intent intent=new Intent(MenuActivity.this,PrincipalActivity.class);
        startActivity(intent);
        MenuActivity.this.finish();
    }

    private void Selecccionar(int IdMenuItem, String NombreMenuItem, int MetodoImpresion) {
        Metodos.closeSoftKeyBoard(MenuActivity.this);
        switch (IdMenuItem) {

            case R.id.Item_Incidentes:
                Fragment incidentes = new IncidentesFragment();
                ImprimirFragment(incidentes, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;


            case R.id.Item_Perfil:
                Fragment perfilUsuario = new DatosPersonalesFragment();
                ImprimirFragment(perfilUsuario, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

            case R.id.Item_NuevoIncidente:
                if (tipoUsuario.getEstado().equalsIgnoreCase("Activo")){
                    NuevoIncidenteFragment.veces = 0;
                    Fragment nuevoIncidente = new NuevoIncidenteFragment();
                    ImprimirFragment(nuevoIncidente, IdMenuItem, NombreMenuItem, MetodoImpresion);
                }else{
                    //Toast.makeText(MenuActivity.this,"Funcionalidad inhabilitada",Toast.LENGTH_SHORT).show();
                    webservicesValidar(persona,IdMenuItem,NombreMenuItem,MetodoImpresion);
                }

                break;

            case R.id.Item_MisIncidentes:
                Fragment misIncidentes = null;
                if (persona!=null){
                        if (persona.getIdTipo()!=2){
                            misIncidentes = new MisIncidentesValidadosFragment();
                        }else{
                            misIncidentes = new MisIncidentesFragment();
                        }
                }else{
                    misIncidentes = new MisIncidentesFragment();
                }
                ImprimirFragment(misIncidentes, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

            case R.id.Item_MiFamilia:
                Fragment miFamilia = new MiFamiliaFragment();
                ImprimirFragment(miFamilia, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

            case R.id.Item_Directorio:
                Fragment directorio= new DirectorioFragment();
                ImprimirFragment(directorio, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

            case R.id.Item_ValidarIncidente:
                Fragment validarIncidente = new ValidaIncidenteFragment()   ;
                //Imprimir en el Fragment
                ImprimirFragment(validarIncidente, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;
            case R.id.Item_RegistrarUbicacion:
                Fragment registrarUbicacion = new RegistarUbicacion()   ;
                //Imprimir en el Fragment
                ImprimirFragment(registrarUbicacion, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

            case R.id.Item_Sugerencias:
                Fragment sugerencias = new Sugerencias()   ;
                //Imprimir en el Fragment
                ImprimirFragment(sugerencias, IdMenuItem, NombreMenuItem, MetodoImpresion);
                break;

        }
    }

    private void ImprimirFragment(Fragment fragment, int idMenuItem, String nombreMenuItem,
                                  int Reset) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nombreMenuItem);
        }
        flPrincipal.removeAllViews();
        if (getApplication() != null) {
            switch (Reset) {
                case 1:
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,  android.R.animator.fade_in, android.R.animator.fade_out)
                                .add(R.id.flPrincipal, fragment)
                                //.addToBackStack(nombreMenuItem)
                                .commit();
                        idFragment.add(idMenuItem);
                        nombreFragment.add(nombreMenuItem);
                    }
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out,
                                    android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.flPrincipal, fragment)
                            .commit();
                    break;

            }
        }
    }

    private int SacarIdMenu(String palabra) {
        int resp = 0;
        if (palabra.compareTo(getString(R.string.itemPerfil)) == 0) {
            resp = R.id.Item_Perfil;
        } else if (palabra.compareTo(getString(R.string.itemNuevoIncidente)) == 0) {
            resp = R.id.Item_NuevoIncidente;
        } else if (palabra.compareTo(getString(R.string.itemIncidentes)) == 0) {
            resp = R.id.Item_MisIncidentes;
        } else if (palabra.compareTo(getString(R.string.itemFamilia)) == 0) {
            resp = R.id.Item_MiFamilia;
        } else if (palabra.compareTo(getString(R.string.itemDirectorio)) == 0) {
            resp = R.id.Item_Directorio;
        } else if (palabra.compareTo(getString(R.string.itemValidarIncidente)) == 0) {
            resp = R.id.Item_ValidarIncidente;
        }else if (palabra.compareTo(getString(R.string.itemIncidentes)) == 0) {
            resp = R.id.Item_Incidentes;
        }
        return resp;
    }


    private void webservicesValidar(final Persona persona,final int IdMenuItem,final String NombreMenuItem,final int MetodoImpresion) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(MenuActivity.this, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(MenuActivity.this,"Verificando estado");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_idpersona),persona.getIdPersona());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(8), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //estado =response.getBoolean("status");
                    //  Toast.makeText(SplashScreenActivity.this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                    //idUsuario, int idPersona, String nombre, String ape_paterno, String ape_materno,
                    // int dni, String email, String direccion, String telefono, String password, int idUrbanizacion, String urbanizacion, int idTipo, String tipo) {

                    TipoUsuario tipo=Select.from(TipoUsuario.class).first();
                    TipoUsuario.deleteAll(TipoUsuario.class);
                    TipoUsuario tipoUsu=new TipoUsuario(
                            response.getInt("user_persona_id"),
                            response.getString("user_state"),
                            tipo.getTipoPersona(),
                            tipo.getTipo(),
                            response.getString("user_nivel_ciudadano_name"),
                            response.getString("user_nivel_ciudadano_icono_src"),
                            response.getString("user_puntuacion_persona_puntos"),
                            response.getString("user_incidencias_registradas"),
                            response.getString("user_incidencias_atendidas"),
                            response.getString("user_incidencias_no_atendidas")
                    );
                    tipoUsu.save();
                    tipoUsuario=tipoUsu;



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
                    if (tipoUsuario.getEstado().equalsIgnoreCase("Activo")){
                        NuevoIncidenteFragment.veces = 0;
                        Fragment nuevoIncidente = new NuevoIncidenteFragment();
                        ImprimirFragment(nuevoIncidente, IdMenuItem, NombreMenuItem, MetodoImpresion);
                    }else{
                        Toast.makeText(MenuActivity.this,"Funcionalidad inhabilitada",Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
    }

}
