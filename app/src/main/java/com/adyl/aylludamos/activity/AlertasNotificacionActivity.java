package com.adyl.aylludamos.activity;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.beans.Notificaciones;
import com.adyl.aylludamos.services.Metodos;
import com.adyl.aylludamos.services.WebServices;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertasNotificacionActivity extends AppCompatActivity {

    Notificaciones notificaciones=null;
    WebServices webServices;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvFecha) TextView tvFecha;
    @BindView(R.id.tvTitulo)  TextView tvTitulo;
    @BindView(R.id.tvDescripcion)  TextView tvDescripcion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        ButterKnife.bind(this);
        webServices=new WebServices(AlertasNotificacionActivity.this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notificaciones");
        }

        if (getIntent().getExtras()!=null){
            notificaciones=(Notificaciones) getIntent().getExtras().getSerializable("incidente");
        }

        tvFecha.setText(Metodos.formatoFechas(notificaciones.getFecha()));
        tvTitulo.setText(notificaciones.getTitulo());
        tvDescripcion.setText(notificaciones.getDescripcion());
        setSupportActionBar(toolbar);
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
