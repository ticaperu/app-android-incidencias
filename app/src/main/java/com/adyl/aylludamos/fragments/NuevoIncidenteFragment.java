package com.adyl.aylludamos.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.RegistrarIncidActivity;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.utils.Maps.MapStateListener;
import com.adyl.aylludamos.utils.Maps.TouchableMapFragment;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by LENOVO on 25/06/2018.
 */

public class NuevoIncidenteFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private TextView TextViewApp;
    private TextView PaginaWeb;
    private TextView TextViewCorreo;
    Button btnContinuarMap = null;
    EditText et = null;
    ImageView mark = null;
    TouchableMapFragment mapFragment;

    public static int veces = 0;
    int ok;
    int point;
    Boolean flag;
    List<Address> list;
    LatLng position;
    int codDistrito;
    String direccion;
    private GoogleApiClient client;
    GoogleMap mGoogleMap = null;
    GoogleApiClient mGoogleApiClient;
    public static boolean mMapIsTouched = false;
    CargandoUbicacion mAuthTask=null;
    String codelocality = "";
    ProgressWheel Igif;
    LocationRequest mlocationRequest;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ok = 0;
        //daoPosition=new DaoPosition(getApplication());
        point = 0;
        list = new ArrayList<>();
        position = new LatLng(0, 0);
        codDistrito = -1;
        flag = false;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        //ButterKnife.bind(this,view);
        boolean asd = googleServicesAvailable();
        //Log.i(" vRRRRRRv00000v===", "=="+asd);
        if (asd) {
            //setContentView(R.layout.activity_maps);
            view = inflater.inflate(R.layout.fragment_nuevainciadencia, container, false);
            //tvDireccion = (TextView) view.findViewById(R.id.tvDireccion);
            //Igif = (ProgressWheel) view.findViewById(R.id.progress_wheel);
            et = (EditText) view.findViewById(R.id.editText);
            btnContinuarMap = (Button) view.findViewById(R.id.btnContinuarMap);
            btnContinuarMap.setEnabled(false);
            mark = (ImageView) view.findViewById(R.id.mark);

            final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ShowDialog.shoDialogValidarGPS(getActivity(),lm);
            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 225);
            }
            intMap();

        } // else NO GOOGLE MAPS LAYOUT

        client = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();


        btnContinuarMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(direccion!="") {
                        NuevoIncidenteFragment.veces++;
                        //Log.i("JAJAJAJ=","YA PUESSSSS::"+NuevoIncidenteFragment.veces);
                        geoLocate();
                    }
                    else{
                        Toast.makeText(getActivity(), "Ingrese una Dirección",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }



    private void intMap() {
        /*mapFragment = (TouchableMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        mapFragment = (TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
        //Log.i("okokokok000==", ""+ok);
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        View mapView = mapFragment.getView();


        //Log.i("okokokok000==", ""+ok);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            //Log.i("mapView==", ""+ok);
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

                //Log.i("okokokokPPPPP==", ""+ok);
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
        mlocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mlocationRequest, this);
        LatLng center = mGoogleMap.getCameraPosition().target; //estos e agrgego
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.i("Google", "Location Changed::::"+ok);
        if (location == null) {
            Toast.makeText(getActivity(), "No se puede obtener la ubicación actual", Toast.LENGTH_LONG).show();
        } else {

            //Log.i("Google", "elseeeeee::::"+ok);
            if (ok == 0) {

                //Log.i("Google", "iffffff ::::"+location.getLatitude()+"///"+location.getLongitude());
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                ubicacion(ll);
                position = new LatLng(ll.latitude, ll.longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 17);
                mGoogleMap.animateCamera(update);
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
            //tvDireccion.setText("");
            //Igif.setVisibility(View.VISIBLE);
            btnContinuarMap.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (ok == 0) {
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        direccion = address.getAddressLine(0);
                        codelocality = address.getAddressLine(1);
                        if (codelocality==null){
                            codelocality=list.get(0).getLocality();
                        }
                        //Toast.makeText(MainActivity.this, locality, Toast.LENGTH_SHORT).show();
                        //tvDireccion.setText(direccion);
                        et.setText(direccion);

                        point = 1;
                        btnContinuarMap.setEnabled(true);
                        //Igif.setVisibility(View.GONE);
                    }

                    ok = -1;
                } else if (position != ll) {
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        direccion = address.getAddressLine(0);
                        codelocality = address.getAddressLine(1);
                        if (codelocality==null){
                            codelocality=list.get(0).getLocality();
                        }
                        //Toast.makeText(MainActivity.this, locality, Toast.LENGTH_SHORT).show();
                        position = new LatLng(ll.latitude, ll.longitude);
                        //tvDireccion.setText(direccion);
                        et.setText(direccion);

                        point = 1;

                        btnContinuarMap.setEnabled(true);
                        //Igif.setVisibility(View.GONE);
                    }
                }else{
                    //Igif.setVisibility(View.GONE);
                }


                if( NuevoIncidenteFragment.veces >= 1 ) {
                    NuevoIncidenteFragment.veces = 0;
                    //Log.i("vecesssss::::","ENTROOOO: "+NuevoIncidenteFragment.veces);
                    //startActivity(new Intent(getActivity(), RegistrarIncidActivity.class));
                    Intent intent = new Intent(getActivity(), RegistrarIncidActivity.class);
                    intent.putExtra("direccion", direccion);
                    intent.putExtra("latitude", String.valueOf(position.latitude));
                    intent.putExtra("longitude", String.valueOf(position.longitude));
                    startActivity(intent);
                }


            }
        }

        @Override
        protected void onCancelled() {
        }
    }




    //onclick este metodo se ejcuta cuando se presiona el boton ir
    public void geoLocate() throws IOException {
    //public void geoLocate(View view) throws IOException {
        //EditText et = (EditText) findViewById(R.id.editText);
        //String location = et.getText().toString() + ",Peru";

        if (et.getText().toString().isEmpty() || et.getText().toString().trim().equals("")) {
            et.setError("Ingrese dirección");
        }else{
            et.setError(null);
            String location = et.getText().toString() + ",Peru";

            if (!location.equals("")) {
                //ok = 1;
                Geocoder gc = new Geocoder(getActivity());
                List<Address> list = gc.getFromLocationName(location, 1);
                if (list.size() > 0) {
                    Address address = list.get(0);
                    String locality = address.getAddressLine(0);
                    String codelocality = address.getAddressLine(1);
                    //Toast.makeText(getActivity(), locality, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getActivity(), "latitude=="+String.valueOf(position.latitude)+"//"+position.longitude, Toast.LENGTH_LONG).show();

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    mGoogleMap.animateCamera(update);
                    ubicacion(latLng);
                    mAuthTask = null;
                    ////ok = -1;
                }
                else {
                    Toast.makeText(getActivity(), "Dirección no encontrada.\nInténtelo de nuevo o busque manualmente", Toast.LENGTH_SHORT).show();
                    //ok = -1;
                }
                et.setText("");
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

    /*
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }*/





}
