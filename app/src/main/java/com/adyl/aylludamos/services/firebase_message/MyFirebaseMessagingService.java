package com.adyl.aylludamos.services.firebase_message;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adyl.aylludamos.R;
import com.adyl.aylludamos.activity.AlertasNotificacionActivity;
import com.adyl.aylludamos.activity.DetalleIncidNotifiActivity;
import com.adyl.aylludamos.activity.MenuActivity;
import com.adyl.aylludamos.beans.Notificaciones;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            JSONObject json;
            try {
                //Log.e("json", remoteMessage.toString());
                json = new JSONObject(remoteMessage.getData().toString());
                Log.e("json", json.toString());
                handleDataMessage(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }





    private void handleDataMessage(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            String timestamp = data.getString("timestamp");
            String plataforma = data.getString("plataforma");
            int incidencia = data.getInt("incidencia");

            Notificaciones notificaciones = new Notificaciones(
                        title,
                        message,
                        plataforma,
                        timestamp,incidencia
                );
                //notificaciones.save();
            Intent intent=null;
            if (incidencia>0){
                intent = new Intent(this, DetalleIncidNotifiActivity.class);
                intent.putExtra("incidente", notificaciones);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else{
                intent = new Intent(this, AlertasNotificacionActivity.class);
                intent.putExtra("incidente", notificaciones);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            showNotificationMessage(getApplicationContext(), title, message, timestamp, intent);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }


}

