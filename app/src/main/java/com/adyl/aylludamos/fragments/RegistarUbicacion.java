package com.adyl.aylludamos.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.LogeoActivity;
import com.adyl.aylludamos.activity.PrincipalActivity;
import com.adyl.aylludamos.activity.RegistrarActivity;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.adyl.aylludamos.utils.Maps.MapStateListener;
import com.adyl.aylludamos.utils.Maps.TouchableMapFragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.orm.query.Select;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by LENOVO on 25/06/2018.
 */

public class RegistarUbicacion extends Fragment
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    public static boolean mMapIsTouched = false;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    CargandoUbicacion mAuthTask=null;
    int ok;
    int point;
    LatLng position;
    List<Address> list;
    private GoogleApiClient client;
    TouchableMapFragment mapFragment;
    String direccion;
    String codelocality;
    LocationRequest mlocationRequest;
    int estado=0;
    Persona persona=null;
    WebServices webServices;
    boolean registro=false;

    @BindView(R.id.etBuscar) EditText etBuscar;
    @BindView(R.id.btnBuscar) Button btnBuscar;
    @BindView(R.id.tvDireccion) TextView tvDireccion;
    @BindView(R.id.progress_wheel)ProgressWheel progress_wheel;
    @BindView(R.id.mark)ImageView mark;
    @BindView(R.id.btnRegistrar) Button btnRegistrar;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ok = 0;
        point = 0;
        position = new LatLng(0, 0);
        webServices=new WebServices(getActivity());
        persona= Select.from(Persona.class).first();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        if (googleServicesAvailable()) {
            view = inflater.inflate(R.layout.fragment_registrar_ubicacion, container, false);

            final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ShowDialog.shoDialogValidarGPS(getActivity(),lm);
            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 225);

            }
            intMap();
        }
        ButterKnife.bind(this,view);
        client = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();

        return view;
    }



    private void intMap() {
       mapFragment = (TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment = (TouchableMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cant connect is play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        View mapView = mapFragment.getView();

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 20, 20);
        }

        new MapStateListener(mGoogleMap, mapFragment, getActivity()) {
            @Override
            public void onMapTouched() {
            }

            @Override
            public void onMapReleased() {
            }

            @Override
            public void onMapUnsettled() {
            }

            @Override
            public void onMapSettled() {
                LatLng center = mGoogleMap.getCameraPosition().target;

                if (ok == -1) {
                    if (position.latitude != 0 && position.longitude != 0) {
                        ubicacion(center);
                        mAuthTask = null;

                        System.out.println("Posicion nueva");
                    } else {
                        System.out.println("Posicion inicial");
                    }
                }
            }
        };

        /*mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //Log.e("mapDrag", "DragStart : " + marker.getPosition());
                LatLng ll = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                // ubicacion(ll);
                //position = new LatLng(ll.latitude, ll.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //Log.e("mapDrag", "Drag : " + marker.getPosition());
                LatLng ll = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                // ubicacion(ll);
                //position = new LatLng(ll.latitude, ll.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng ll = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
               // ubicacion(ll);
                //position = new LatLng(ll.latitude, ll.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
            }
        });*/

       /* mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (estado==0){
                    try {
                        geoLocate();
                        estado=1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    LatLng ll = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    position = new LatLng(ll.latitude, ll.longitude);
                    ubicacion(ll);
                    mAuthTask = null;
                }
                //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                //mGoogleMap.animateCamera(update);
            }
        });*/
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(225);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 225);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);
        LatLng center = mGoogleMap.getCameraPosition().target;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.i("Google", "Location Changed");
        if (location == null) {
            Toast.makeText(getActivity(), "No se puede obtener la ubicación actual", Toast.LENGTH_LONG).show();
        } else {
            if (ok == 0) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                ubicacion(ll);
                mAuthTask = null;
                position = new LatLng(ll.latitude, ll.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
            }
        }
    }




    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @OnClick({R.id.btnBuscar,R.id.btnRegistrar})
    void  onclick(Button button){
        switch(button.getId()) {
            case R.id.btnBuscar:
                try {
                    geoLocates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnRegistrar:
                //showDialogRegistrar(getActivity());

                webservicesRegistarUbicación( "Registro de mi ubicacion actual");

                break;
        }
    }

    public void geoLocates() throws IOException {

        //String location = et.getText().toString() + ",Peru";

        if (etBuscar.getText().toString().isEmpty() || etBuscar.getText().toString().trim().equals("")) {
            etBuscar.setError("Ingrese la dirección donde desee recibir su pedido");
        }else{
            etBuscar.setError(null);
            String location = etBuscar.getText().toString() + ",Peru";

            if (!location.equals("")) {
                //ok = 1;
                Geocoder gc = new Geocoder(getActivity());
                List<Address> list = gc.getFromLocationName(location, 1);
                if (list.size() > 0) {
                    Address address = list.get(0);
                    String locality = address.getAddressLine(0);

                    Toast.makeText(getActivity(), locality, Toast.LENGTH_LONG).show();

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    mGoogleMap.animateCamera(update);
                    ubicacion(latLng);
                    mAuthTask = null;
                    ////ok = -1;
                } else {
                    Toast.makeText(getActivity(), "Dirección no encontrada.\nInténtelo de nuevo o busque manualmente", Toast.LENGTH_SHORT).show();
                    //ok = -1;
                }
                etBuscar.setText("");
            }
        }

    }

    public void geoLocate() throws IOException {
        String location =  "Trujillo,Peru";

        if (!location.equals("")) {
            Geocoder gc = new Geocoder(getActivity());
            List<Address> list = gc.getFromLocationName(location, 1);

            if (list.size() > 0) {
                Address address = list.get(0);
                String locality = address.getAddressLine(0);

                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 11);
                mGoogleMap.animateCamera(update);
                ubicacion(latLng);
                mAuthTask = null;
            } else {
                Toast.makeText(getActivity(), "Dirección no encontrada.\nInténtelo de nuevo o busque manualmente", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void ubicacion(LatLng ll) {
        if (mAuthTask != null) {
            System.out.println("RETURN");
            return;
        }

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mAuthTask = new CargandoUbicacion(ll);
            mAuthTask.execute((Void) null);
        } else {
            Toast.makeText(getActivity(),"No hay conexión a internet",Toast.LENGTH_SHORT).show();
        }
    }

    private class CargandoUbicacion extends AsyncTask<Void, Void, Boolean> {

        LatLng ll;

        public CargandoUbicacion(LatLng ll) {
            this.ll = ll;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            point = -1;
            if (ok == 0) {
                Geocoder gc = new Geocoder(getActivity());
                list = new ArrayList<>();
                try {
                    list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ok = -1;
            } else if (position != ll) {
                Geocoder gc = new Geocoder(getActivity());
                list = new ArrayList<>();
                try {
                    list = gc.getFromLocation(ll.latitude, ll.longitude, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            tvDireccion.setText("");
            progress_wheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (ok == 0) {
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        direccion = address.getAddressLine(0);
                        codelocality = address.getAddressLine(1);

                        //Toast.makeText(MainActivity.this, locality, Toast.LENGTH_SHORT).show();
                        tvDireccion.setText(direccion);

                        point = 1;
                        progress_wheel.setVisibility(View.GONE);
                    }

                    ok = -1;
                } else if (position != ll) {
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        direccion = address.getAddressLine(0);
                        codelocality = address.getAddressLine(1);

                        //Toast.makeText(MainActivity.this, locality, Toast.LENGTH_SHORT).show();
                        position = new LatLng(ll.latitude, ll.longitude);
                        tvDireccion.setText(direccion);

                        point = 1;

                        progress_wheel.setVisibility(View.GONE);
                    }
                }else{
                    progress_wheel.setVisibility(View.GONE);
                }
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    // se comento esta porcion de codigo debido a
    // que el cliente queria un camino mas directo
    ////////////////
    /*public void showDialogRegistrar(final Activity activity){
        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ubicacion);
        final EditText etDialogDireccion=(EditText)dialog.findViewById(R.id.etDialogDireccion);
        Button btnRegistrar=(Button) dialog.findViewById(R.id.btnRegistrar);



        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etDialogDireccion.getText().toString()) ){
                    webservicesRegistarUbicación(dialog,etDialogDireccion);
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
    }*/

    //private void webservicesRegistarUbicación(final Dialog dialog,final  EditText editText) {
    private void webservicesRegistarUbicación(final  String desc) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(getActivity(),"Espere un momento");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(getString(R.string.parametro_service_telefono),persona.getTelefono());
            //object.put(getString(R.string.parametro_service_telefono),"999999999");
            object.put(getString(R.string.parametro_service_latitud),position.latitude);
            object.put(getString(R.string.parametro_service_longitud),position.longitude);
            object.put(getString(R.string.parametro_service_descripcion), desc);
            //object.put(getString(R.string.parametro_service_descripcion),editText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(12), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    registro =response.getBoolean("success");
                    Toast.makeText(getActivity(),response.getString("data"),Toast.LENGTH_SHORT).show();
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
                    if (registro){
                        registro=false;
                        //editText.setText("");
                        //dialog.dismiss();
                    }
                }
            }


        });
    }

}
