package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.DireccionesFamiliaActivity;
import com.adyl.aylludamos.activity.LogeoActivity;
import com.adyl.aylludamos.activity.SplashScreenActivity;
import com.adyl.aylludamos.beans.Directorio;
import com.adyl.aylludamos.beans.Familia;
import com.adyl.aylludamos.beans.Persona;
import com.adyl.aylludamos.beans.TipoUsuario;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.ShowDialog;
import com.adyl.aylludamos.services.WebServices;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardFamilia extends RecyclerView.Adapter<CardFamilia.FamiliaViewHolder> {
    private final List<Familia> familiaList;
    private final Activity activity;
    private WebServices webServices;
    private boolean estado=false;

    public CardFamilia(List<Familia> familiaList, Activity activity) {
        this.familiaList = familiaList;
        this.activity = activity;
        webServices=new WebServices(activity);
    }

    @Override
    public FamiliaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_familia, parent, false);
        return new FamiliaViewHolder(v);
    }


    @Override
    public void onBindViewHolder(FamiliaViewHolder holder, final int position) {
        final Familia familia=familiaList.get(position);
        holder.tvNombre.setText(familia.getNombre());
        holder.tvTelefono.setText(familia.getTelefono());

        holder.ivEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webservicesEliminar(familia,position);
            }
        });

        holder.ivUbicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, DireccionesFamiliaActivity.class);
                intent.putExtra("familia",familia);
                activity.startActivity(intent);
            }
        });
        holder.ivLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.onCall(activity,familia.getTelefono(),"Número no valido");
            }
        });
    }

    @Override
    public int getItemCount() {
        return familiaList.size();
    }

    class FamiliaViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvNombre) TextView tvNombre;
        @BindView(R.id.tvTelefono) TextView tvTelefono;
        @BindView(R.id.ivEliminar) ImageView ivEliminar;
        @BindView(R.id.ivUbicar) ImageView ivUbicar;
        @BindView(R.id.ivLlamar) ImageView ivLlamar;
        FamiliaViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void webservicesEliminar(final Familia familia,final int position) {
        //final boolean[] estado = new boolean[0];
        //estado[0]=false;
        RequestQueue requestQueue = Volley.newRequestQueue(activity, new HurlStack());
        final ProgressDialog progressDialog = ShowDialog.createProgressDialog(activity,"Eliminando ");
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put(activity.getString(R.string.parametro_service_idfamilia),familia.getIdPersona());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest_logeo=  new JsonObjectRequest(Request.Method.POST, webServices.Url(10), object
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    estado =response.getBoolean("status");
                    Toast.makeText(activity,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
                } catch (JSONException error) {
                    error.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        jsonObjectRequest_logeo.setRetryPolicy(webServices.Parametros());
        jsonObjectRequest_logeo.setShouldCache(false);
        requestQueue.add(jsonObjectRequest_logeo);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    if (estado==true){
                        familiaList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, familiaList.size());
                    }
                }
            }


        });
    }

}
