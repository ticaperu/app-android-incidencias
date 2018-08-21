package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.beans.Media;
import com.adyl.aylludamos.beans.Multimedia;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardDetalleIncidenciaM  extends RecyclerView.Adapter<CardDetalleIncidenciaM.DetalleIncidenciaViewHolder> {
    private final List<Multimedia> mediaList;
    private final Activity activity;
    int tipo=0;

    public CardDetalleIncidenciaM(List<Multimedia> mediaList, Activity activity,int tipo) {
        this.mediaList = mediaList;
        this.activity = activity;
        this.tipo=tipo;
    }

    @Override
    public DetalleIncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_incidencia, parent, false);
        return new DetalleIncidenciaViewHolder(v);
    }



    @Override
    public void onBindViewHolder(DetalleIncidenciaViewHolder holder,final int position) {
        final Multimedia media = mediaList.get(position);

        if (media.getTipo().equalsIgnoreCase("Imagen")){

            if( media.getIdmedia() > 0 ) { // si viene de bd
                Metodos.GlideImagen(activity, media.getPath_file(), holder.ivFoto);
            }
            else if( media.getIdmedia() == 0 ){ // si viene de local y es nuevo
                holder.ivFoto.setImageBitmap(media.getBitmap());
            }
            holder.rlVideo.setVisibility(View.GONE);

            holder.ivFoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (media.getEliminar()==1){
                        mediaList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mediaList.size());
                    }
                    if (tipo==1){
                        RecyclerView rv=(RecyclerView)activity.findViewById(R.id.rvMedia);
                            LinearLayout.LayoutParams params = new
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        rv.setLayoutParams(params);
                        rv.setHasFixedSize(true);
                    }

                    return true;
                }
            });

            holder.ivFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( media.getIdmedia() > 0 ) {
                    ShowDialog.shoDialogMedia(activity,media.getBitmap(),media.getPath_file(),1, 1);
                    }else {
                        ShowDialog.shoDialogMedia(activity,media.getBitmap(),media.getPath_file(),1, 0);
                    }
                }
            });
        }
        else{
            //Log.i("anthonyyyy", "videooooo");
            //Log.i("anthonyyyy", "pathhh:::"+media.getPath_file());

            holder.vvVideo.setVideoPath(media.getPath_file());
            holder.vvVideo.start();
            holder.vvVideo.pause();
            //holder.vvVideo.requestFocus();

            holder.ivFoto.setVisibility(View.GONE);

            holder.ivVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (media.getEliminar()==1){
                        mediaList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mediaList.size());
                    }
                    return true;
                }
            });

            holder.ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowDialog.shoDialogMedia(activity,null,media.getPath_file(),2, 0);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    class DetalleIncidenciaViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ivVideo) ImageView ivVideo;
        @BindView(R.id.ivFoto) ImageView ivFoto;
        @BindView(R.id.vvVideo) VideoView vvVideo;
        @BindView(R.id.rlVideo)
        RelativeLayout rlVideo;
        DetalleIncidenciaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }





}