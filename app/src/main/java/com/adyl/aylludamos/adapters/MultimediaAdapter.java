package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Multimedia;

import java.util.ArrayList;

public class MultimediaAdapter extends BaseAdapter {
    private ArrayList<Multimedia> listaMedia;
    private Activity activity;

    public MultimediaAdapter(ArrayList<Multimedia> lista, Activity activity){
        this.listaMedia = lista;
        this.activity=activity;
        //notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listaMedia.size();
    }

    @Override
    public Object getItem(int position) {
        return listaMedia.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaMedia.get(position).getIdmedia();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_multimedia, container, false);
        }

        ((ImageView) convertView.findViewById(R.id.imageView)).setImageBitmap(listaMedia.get(position).getBitmap());


        return convertView;
    }


}
