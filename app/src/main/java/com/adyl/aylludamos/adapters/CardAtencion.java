package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Atencion;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.services.Metodos;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardAtencion extends RecyclerView.Adapter<CardAtencion.AtencionViewHolder> {
    private final List<Atencion> atencionList;
    private final Activity activity;

    public CardAtencion(List<Atencion> atencionList, Activity activity) {
        this.atencionList = atencionList;
        this.activity = activity;
    }

    @Override
    public AtencionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atencion, parent, false);
        return new AtencionViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AtencionViewHolder holder, int position) {
        final Atencion atencion=atencionList.get(position);

        holder.tvAtencion.setText(atencion.getDescripcion());

    }

    @Override
    public int getItemCount() {
        return atencionList.size();
    }

    class AtencionViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvAtencion) TextView tvAtencion;
        AtencionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
