package com.adyl.aylludamos.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.DetalleIncidNotifiActivity;
import com.adyl.aylludamos.activity.MapsActivity;
import com.adyl.aylludamos.beans.Direccion;
import com.adyl.aylludamos.services.Metodos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardDirecciones extends RecyclerView.Adapter<CardDirecciones.DireccionesViewHolder>  implements OnMapReadyCallback{
    private final List<Direccion> directorioList;
    private final Activity activity;
    GoogleMap mGoogleMap;
    Direccion direcciones=null;

    public CardDirecciones(List<Direccion> directorioList, Activity activity) {
        this.directorioList = directorioList;
        this.activity = activity;
    }

    @Override
    public DireccionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direccion, parent, false);
        return new DireccionesViewHolder(v);
    }


    @Override
    public void onBindViewHolder(DireccionesViewHolder holder, int position) {
        final Direccion direccion = directorioList.get(position);

        holder.tvFecha.setText(Metodos.formatoFecha(direccion.getFecha()));
        holder.tvDireccion.setText(direccion.getDescripcion());
        holder.ibGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity,MapsActivity.class);
                intent.putExtra("latitud",Double.parseDouble(direccion.getLatitud()));
                intent.putExtra("longitud",Double.parseDouble(direccion.getLongitud()));
                intent.putExtra("polyline","null");
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return directorioList.size();
    }


    class DireccionesViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvFecha)
        TextView tvFecha;
        @BindView(R.id.tvDireccion)
        TextView tvDireccion;
        @BindView(R.id.ibGps)
        ImageView ibGps;

        DireccionesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

}
