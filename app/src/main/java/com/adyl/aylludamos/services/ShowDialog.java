package com.adyl.aylludamos.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.DetalleIncidActivity;
import com.adyl.aylludamos.beans.Direccion;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.utils.TouchImageViewHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by LENOVO on 23/06/2018.
 */

public class ShowDialog {


    @SuppressLint("RestrictedApi")
    public static ProgressDialog createProgressDialog(Activity activity, String Mensaje) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(
                new ContextThemeWrapper(activity, R.style.custom_dialog));
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage(Mensaje);
        progressDialog.setProgress(0);
        progressDialog.setTitle(activity.getString(R.string.app_name));
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }
    /**@param valor 1 cuando viene del principal y solo para visualizar
    **@param valor 1 cuando viene del incidente y  solo para visualizar
    **@param valor 3 cuando viene del incidente y  yestado atendida para que pueda seguir agregando
     *
     * */
    public static   void showDialogIncidente(final Activity activity, final Incidente incidente, final int valor){
        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_incidencia);
        TextView tvTitulo=(TextView)dialog.findViewById(R.id.tvTitulo);
        TextView tvFecha=(TextView)dialog.findViewById(R.id.tvFecha);
        TextView tvUrbanizacion=(TextView)dialog.findViewById(R.id.tvUrbanizacion);
        TextView tvTerritorio=(TextView)dialog.findViewById(R.id.tvTerritorio);
        TextView tvDescripcion=(TextView)dialog.findViewById(R.id.tvDescripcion);
        TextView tvDireccion=(TextView)dialog.findViewById(R.id.tvDireccion);
        Button btnSolicitar=(Button)dialog.findViewById(R.id.btnSolicitar);

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, DetalleIncidActivity.class);
                intent.putExtra("incidente",incidente);
                intent.putExtra("valor",valor);
                activity.startActivity(intent);
            }
        });

        tvFecha.setText(Metodos.formatoFecha(incidente.getFecha()));
        tvTitulo.setText(incidente.getTipo().toUpperCase());

        tvUrbanizacion.setText(incidente.getUrbanizacion());
        tvTerritorio.setText(incidente.getTerritorio());
        tvDescripcion.setText(incidente.getDescripcion());
        tvDireccion.setText(incidente.getDireccion());

        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        Window window=dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void shoDialogValidarGPS(final Activity activity, final LocationManager lm ){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mensaje);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView msj = (TextView) dialog.findViewById(R.id.tvDialogMensaje);
        msj.setText(R.string.msj_activar_gps);

        TextView btnDialogCancelar = (TextView) dialog.findViewById(R.id.btnDialogCancelar);
        btnDialogCancelar.setVisibility(View.GONE);

        TextView btnDialogAceptar = (TextView) dialog.findViewById(R.id.btnDialogAceptar);
        btnDialogAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
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

    //tipo 1 imagen , tipo 2 video
    public static void shoDialogMedia(final Activity activity, Bitmap bit, final  String url, final int tipo,  int imagen){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_media);

        TouchImageViewHelper ivFoto=(TouchImageViewHelper) dialog.findViewById(R.id.ivFoto);
        VideoView vvVideo=(VideoView) dialog.findViewById(R.id.vvVideo);
        RelativeLayout rlVideo=(RelativeLayout) dialog.findViewById(R.id.rlVideo);
        if (tipo==1){
            rlVideo.setVisibility(View.GONE);
            if( imagen > 0 ) { // si viene de bd
                Metodos.GlideImagen(activity, url, ivFoto);
            }
            else if( imagen== 0 ){ // si viene de local y es nuevo
                ivFoto.setImageBitmap(bit);
            }


        }else{
            MediaController mediaController = new MediaController(activity);
            ivFoto.setVisibility(View.GONE);
            rlVideo.setVisibility(View.VISIBLE);
            vvVideo.setVideoPath(url);
            vvVideo.start();
            //vvVideo.requestFocus();
            //vvVideo.setMediaController(mediaController);

        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }


}
