package com.adyl.aylludamos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adyl.aylludamos.beans.Urbanizacion;

import java.util.List;


/**
 * Created by LENOVO on 26/02/2018.
 */

public class SpinnerUrbanizacionAdapter extends ArrayAdapter<Urbanizacion> {
    private Context context;
    private List<Urbanizacion> urbanizacionList;


    public SpinnerUrbanizacionAdapter(@NonNull Context context, int resource, @NonNull List<Urbanizacion> urbanizacionList) {
        super(context, resource, urbanizacionList);
        this.context=context;
        this.urbanizacionList=urbanizacionList;
    }

    @Override
    public int getCount(){
        return urbanizacionList.size();
    }

    @Override
    public Urbanizacion getItem(int position){
        return urbanizacionList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setPadding(16, 16, 16, 16);
        label.setText(urbanizacionList.get(position).getDescripcion());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setPadding(16, 16, 16, 16);
        label.setText(urbanizacionList.get(position).getDescripcion());
        return label;
    }


}
