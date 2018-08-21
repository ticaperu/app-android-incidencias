package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.DetalleIncidActivity;
import com.adyl.aylludamos.activity.ValidarIncidenteActivity;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.beans.Incidente;
import com.adyl.aylludamos.services.Metodos;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardIncidencia extends RecyclerView.Adapter<CardIncidencia.IncidenciaViewHolder> {
    private final List<Incidente> incidenteList;
    private final Activity activity;
    private int tipo=1; //para verificar de donde vienen los datos (mis inicdente, incidentes validados, o para validar)

    public CardIncidencia(List<Incidente> incidenteList, Activity activity, int tipo) {
        this.incidenteList = incidenteList;
        this.activity = activity;
        this.tipo=tipo;
    }

    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
        return new IncidenciaViewHolder(v);
    }


    @Override
    public void onBindViewHolder(IncidenciaViewHolder holder, int position) {
        final Incidente incidente=incidenteList.get(position);

        holder.tvFecha.setText(Metodos.formatoFecha(incidente.getFecha()));//Formato de fecha
        holder.tvTipo.setText(incidente.getTipo());
        holder.tvDireccion.setText(incidente.getDireccion());
        holder.tvEstado.setText(incidente.getEstado());

        switch(tipo){
            case 1:
                holder.btnEstado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(activity, DetalleIncidActivity.class);
                        intent.putExtra("incidente",incidente);
                        intent.putExtra("valor",2);
                        activity.startActivity(intent);
                    }
                });
                break;
            case 2:
                holder.btnEstado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(activity, DetalleIncidActivity.class);
                        intent.putExtra("incidente",incidente);
                        intent.putExtra("valor",1);
                        activity.startActivity(intent);
                    }
                });
                break;
            case 3:
                holder.btnEstado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(activity, ValidarIncidenteActivity.class);
                        intent.putExtra("incidente",incidente);
                        activity.startActivity(intent);
                    }
                });
                break;
        }




    }

    @Override
    public int getItemCount() {
        return incidenteList.size();
    }

    class IncidenciaViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvFecha) TextView tvFecha;
        @BindView(R.id.tvTipo) TextView tvTipo;
        @BindView(R.id.tvDireccion) TextView tvDireccion;
        @BindView(R.id.tvEstado) TextView tvEstado;
        @BindView(R.id.btnEstado) Button btnEstado;
        IncidenciaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
