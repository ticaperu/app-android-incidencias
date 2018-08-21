package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.beans.Media;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardDetalleIncidencia extends RecyclerView.Adapter<CardDetalleIncidencia.DetalleIncidenciaViewHolder> {
    private final List<Media> mediaList;
    private final Activity activity;

    public CardDetalleIncidencia(List<Media> mediaList, Activity activity) {
        this.mediaList = mediaList;
        this.activity = activity;
    }

    @Override
    public DetalleIncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_incidencia, parent, false);
        return new DetalleIncidenciaViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final DetalleIncidenciaViewHolder holder, int position) {
        final Media media=mediaList.get(position);

        if (media.getDescripcion().equalsIgnoreCase("Imagen")){
            Metodos.GlideImagen(activity,media.getUrl(),holder.ivFoto);
            holder.rlVideo.setVisibility(View.GONE);

            holder.ivFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowDialog.shoDialogMedia(activity,null,media.getUrl(),1, 1);

                }
            });
        }else{
            holder.ivFoto.setVisibility(View.GONE);
            holder.vvVideo.setVisibility(View.VISIBLE);
            final MediaController mediacontroller;
            final Uri uri;
            mediacontroller = new MediaController(activity);
            mediacontroller.setAnchorView(holder.vvVideo);
            uri = Uri.parse(media.getUrl());
            holder.vvVideo.setVideoPath(media.getUrl());

            holder.ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowDialog.shoDialogMedia(activity,null,media.getUrl(),2, 0);
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    class DetalleIncidenciaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rlVideo)
        RelativeLayout rlVideo;
        @BindView(R.id.ivVideo) ImageView ivVideo;
        @BindView(R.id.ivFoto) ImageView ivFoto;
        @BindView(R.id.vvVideo) VideoView vvVideo;
        DetalleIncidenciaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
