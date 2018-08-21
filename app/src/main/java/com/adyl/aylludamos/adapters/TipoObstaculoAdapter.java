package com.adyl.aylludamos.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.TiposObstaculo;
import com.adyl.aylludamos.utils.VolleyRequest.CustomVolleyRequest;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;


public class TipoObstaculoAdapter extends BaseAdapter {
    private ArrayList<TiposObstaculo> listTipoObs;
    private Activity activity;
    private RadioButton[] rb = null;
    private int selectedPosition = -1;
    //private RadioGroup grupo = null;
    private ImageLoader imageLoader;

    public TipoObstaculoAdapter(ArrayList<TiposObstaculo> lista, Activity activity){
        this.listTipoObs = lista;
        this.activity=activity;
        rb = new RadioButton[this.listTipoObs.size()];
        //grupo = g;

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return listTipoObs.size();
    }

    @Override
    public Object getItem(int position) {
        return listTipoObs.get(position);
    }


    @Override
    public long getItemId(int position) {
        return listTipoObs.get(position).getIdTipo();
    }


    @Override
    public View getView(int i, View view, ViewGroup container) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean

            view = LayoutInflater.from(activity).inflate(R.layout.item_tipo_obstaculo, container, false);

            viewHolder.label = (TextView) view.findViewById(R.id.txtTipoObs);
            viewHolder.radioButton = (RadioButton) view.findViewById(R.id.rdTipoObs);
            viewHolder.imgTipoObs = (NetworkImageView) view.findViewById(R.id.imgTipoObs);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();


        viewHolder.imgTipoObs.setDefaultImageResId(R.drawable.logo);
        //String url = "https://5.imimg.com/data5/AY/WH/MY-4326708/lenovo-desktop-computer-500x500.jpg";
        String url = "";
        if( listTipoObs.get(i).getImagen() == null ){
            viewHolder.imgTipoObs.setDefaultImageResId(R.drawable.logo);
        }
        else{
            url = listTipoObs.get(i).getImagen();

            imageLoader = CustomVolleyRequest.getInstance(activity)
                    .getImageLoader();
            imageLoader.get(url, ImageLoader.getImageListener(viewHolder.imgTipoObs,
                    R.drawable.agregar, android.R.drawable.ic_dialog_alert));
            viewHolder.imgTipoObs.setImageUrl(url, imageLoader);
        }

        viewHolder.label.setText(listTipoObs.get(i).getNombre());

        //check the radio button if both position and selectedPosition matches
        viewHolder.radioButton.setChecked(i == selectedPosition);

        //Set the position tag to both radio button and label
        viewHolder.radioButton.setTag(i);
        viewHolder.label.setTag(i);

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
            }
        });

        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
            }


        });

        //((TextView) convertView.findViewById(R.id.txtTipoObs)).setText(listTipoObs.get(position).getNombre());

        return view;
    }

    //On selecting any view set the current position to selectedPositon and notify adapter
    private void itemCheckChanged(View v) {
        selectedPosition = (Integer) v.getTag();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView label;
        private RadioButton radioButton;
        private NetworkImageView imgTipoObs;
    }


    public int getSelectedItemSelected() {
        if (selectedPosition != -1) {
            return listTipoObs.get(selectedPosition).getIdTipo();
        }
        return 0;
    }


    public int getSelectedItemIndex() {
        return selectedPosition;
    }

    public String getSelectedItemName() {
        if (selectedPosition != -1) {
            //Toast.makeText(activity, "Selected Item : " + listTipoObs.get(selectedPosition).getNombre(), Toast.LENGTH_SHORT).show();
            return listTipoObs.get(selectedPosition).getNombre();
        }
        return "";
    }

}
