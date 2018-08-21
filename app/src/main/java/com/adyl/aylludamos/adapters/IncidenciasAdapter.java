package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.services.WebServices;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IncidenciasAdapter extends RecyclerView.Adapter<IncidenciasAdapter.IncidenciaViewHolder> {
    private final List<Incidente> listaInc;
    private final Activity activity;
    private WebServices webServices;
    private boolean estado=false;

    public IncidenciasAdapter(List<Incidente> lista, Activity activity) {
        this.listaInc = lista;
        this.activity = activity;
        webServices=new WebServices(activity);
    }

    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
        return new IncidenciaViewHolder(v);
    }


    @Override
    public void onBindViewHolder(IncidenciaViewHolder holder, final int position) {
        final Incidente incidente = listaInc.get(position);

    }

    @Override
    public int getItemCount() {
        return listaInc.size();
    }

    class IncidenciaViewHolder extends RecyclerView.ViewHolder {



        IncidenciaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
