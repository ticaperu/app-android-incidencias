package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.services.Metodos;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.adyl.aylludamos.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardDirectorio extends RecyclerView.Adapter<CardDirectorio.DirectorioViewHolder> {
    private final List<Directorio> directorioList;
    private final Activity activity;

    public CardDirectorio(List<Directorio> directorioList, Activity activity) {
        this.directorioList = directorioList;
        this.activity = activity;
    }

    @Override
    public DirectorioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directorio, parent, false);
        return new DirectorioViewHolder(v);
    }


    @Override
    public void onBindViewHolder(DirectorioViewHolder holder, int position) {
        final Directorio directorio=directorioList.get(position);

        holder.tvNombre.setText(directorio.getNombre());
        holder.tvDireccion.setText(directorio.getDireccion());
        holder.ibTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.onCall(activity,directorio.getTelefono(),"Número no valido");
            }
        });

    }

    @Override
    public int getItemCount() {
        return directorioList.size();
    }

    class DirectorioViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvNombre) TextView tvNombre;
        @BindView(R.id.tvDireccion) TextView tvDireccion;
        @BindView(R.id.ibTelefono) ImageView ibTelefono;
        DirectorioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



}
