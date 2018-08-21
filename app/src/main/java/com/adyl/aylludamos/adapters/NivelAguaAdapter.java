package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.NivelAgua;

import java.util.ArrayList;

public class NivelAguaAdapter extends BaseAdapter {
    private ArrayList<NivelAgua> listNiveles;
    private Activity activity;

    public NivelAguaAdapter(ArrayList<NivelAgua> lista, Activity activity){
        this.listNiveles = lista;
        this.activity=activity;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listNiveles.size();
    }

    @Override
    public Object getItem(int position) {
        return listNiveles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listNiveles.get(position).getIdNivel();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_nivlesagua, container, false);
        }

        TextView txtNivelAgua = (TextView) convertView.findViewById(R.id.txtNivelAgua);
        txtNivelAgua.setText(listNiveles.get(position).getDescripcion());

        ViewGroup.LayoutParams par = txtNivelAgua.getLayoutParams();
        par.height = listNiveles.get(position).getAlturaItem();
        txtNivelAgua.setLayoutParams(par);
        txtNivelAgua.requestLayout();


        return convertView;
    }


}
