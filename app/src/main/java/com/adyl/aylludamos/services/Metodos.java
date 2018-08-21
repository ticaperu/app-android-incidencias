package com.adyl.aylludamos.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.adyl.aylludamos.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.adyl.aylludamos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LENOVO on 23/06/2018.
 */

public class Metodos {


    public Metodos() {
        Policity();
    }

    public static boolean VerificarLista(List<?> personaList) {
        return (personaList != null) && (personaList.size() > 0);
    }

    public static boolean ValidarJson(JSONObject jsonObject1) {
        return (jsonObject1 != null);
    }

    public static boolean ValidarJsonArray(JSONArray jsonObject1) {
        return jsonObject1 != null && jsonObject1.length() > 0;
    }
    public String FechaActual() {
        Calendar c1 = Calendar.getInstance();
        return c1.get(Calendar.YEAR) + "-" +
                ((c1.get(Calendar.MONTH) < 10) ? "0" + (c1.get(Calendar.MONTH) + 1)
                        : (c1.get(Calendar.MONTH) + 1))
                + "-" + ((c1.get(Calendar.DATE) < 10) ? "0" + c1.get(Calendar.DATE)
                : c1.get(Calendar.DATE));
    }

    public String HoraaActual() {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);
        int segundo = cal.get(Calendar.SECOND);
        return ((hora < 10) ? "0" + hora : hora) + ":" + ((minuto < 10) ? "0" + minuto : minuto) + ":"
                + ((segundo < 10) ? "0" + segundo : segundo);
    }


    private void Policity() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static boolean estadoConexionWifioDatos(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        boolean resulta;
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getTypeName().equalsIgnoreCase("WIFI")) {
                    if (activeNetwork.isConnected()) {
                        haveConnectedWifi = true;
                    }
                }
                if (activeNetwork.getTypeName().equalsIgnoreCase("MOBILE")) {
                    if (activeNetwork.isConnected()) {
                        haveConnectedMobile = true;
                    }
                }
                resulta = haveConnectedWifi || haveConnectedMobile;
            } else {
                resulta = false;
            }
        } else {
            resulta = false;
        }
        return resulta;
    }

    public String VersionAndroid() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        return versionCode + " - " + versionName;
    }

    public static String TokenFirebase() {
        String a= FirebaseInstanceId.getInstance().getToken();
        return FirebaseInstanceId.getInstance().getToken();
    }

    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public boolean ValidarEmail(String email) {
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public RetryPolicy Parametros() {
        return new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static  void closeSoftKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    @SuppressLint("MissingPermission")
    public static void onCall(Activity activity, String Telefono, String mensaje) {
        if (Telefono.trim().length() > 0 && Telefono.trim().length() <= 9) {
            try {
                int permissionCheck = ContextCompat
                        .checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + Telefono));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity,mensaje,Toast.LENGTH_SHORT).show();
        }
    }

    public static String formatoFecha(String dia) {
        /*String fecha[] = dia.split("-");
        fecha[1] = nombreMes(Integer.parseInt(fecha[1]));

        return fecha[2] + " de " + fecha[1] + " " + fecha[0] ;*/
        return  dia;
    }

    public static String formatoFechas(String dia) {
        String dias[] = dia.split(" ");
        String fecha[] = dias[0].split("-");
        fecha[1] = nombreMes(Integer.parseInt(fecha[1]));

        return fecha[2] + " de " + fecha[1] + " " + fecha[0] ;
    }

    public static String nombreMes(int mes) {
        String nombreMes = "";
        switch (mes) {
            case 1:
                nombreMes = "enero";
                break;
            case 2:
                nombreMes = "febrero";
                break;
            case 3:
                nombreMes = "marzo";
                break;
            case 4:
                nombreMes = "abril";
                break;
            case 5:
                nombreMes = "mayo";
                break;
            case 6:
                nombreMes = "junio";
                break;
            case 7:
                nombreMes = "julio";
                break;
            case 8:
                nombreMes = "agosto";
                break;
            case 9:
                nombreMes = "septiembre";
                break;
            case 10:
                nombreMes = "octubre";
                break;
            case 11:
                nombreMes = "noviembre";
                break;
            case 12:
                nombreMes = "diciembre";
                break;
            default:
                break;
        }
        return nombreMes;
    }

    public static  void GlideImagen(Activity activity, String urlfoto, ImageView imageViewFotoDocente) {
        Glide.with(activity).load( urlfoto)
                .asBitmap()
                .thumbnail(0.5f)
                 //.centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.sinfoto_eliminar)
                .dontAnimate()
                // .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.sinfoto_eliminar)
                .into(imageViewFotoDocente);
    }

    public static  void GlideImagenLogoDefault(Activity activity, String urlfoto, ImageView imageViewFotoDocente) {
        Glide.with(activity).load( urlfoto)
                .asBitmap()
                .thumbnail(0.5f)
                // .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.logo)
                .dontAnimate()
                // .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.logo)
                .into(imageViewFotoDocente);
    }


    public static boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int byteSizeImage(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static long byteSizeVideo(Activity activity,Uri uri){
        Cursor returnCursor =activity.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String nombre=returnCursor.getString(nameIndex);
        long tama=returnCursor.getLong(sizeIndex);
        return  tama;
    }
}
