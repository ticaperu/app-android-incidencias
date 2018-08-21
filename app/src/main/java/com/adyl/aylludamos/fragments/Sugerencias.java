package com.adyl.aylludamos.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.services.ShowDialog;

/**
 * Created by LENOVO on 25/06/2018.
 */

public class Sugerencias extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;

            view = inflater.inflate(R.layout.fragment_sugerencias, container, false);

            final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                ShowDialog.shoDialogValidarGPS(getActivity(),lm);
            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 225);

            }


        return view;
    }



/*
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

    */
}
