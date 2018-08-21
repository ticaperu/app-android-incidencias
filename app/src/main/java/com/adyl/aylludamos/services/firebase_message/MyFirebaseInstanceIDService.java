package com.adyl.aylludamos.services.firebase_message;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.adyl.aylludamos.R;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.e(TAG, "Got token: " + refreshedToken);
        setFirebaseInstanceId(this, refreshedToken);
    }

    public static void setFirebaseInstanceId(Context context, String InstanceId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_firebase_instance_id_key), InstanceId);
        editor.apply();
    }

    public static String getFirebaseInstanceId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_firebase_instance_id_key);
        String default_value = context.getString(R.string.pref_firebase_instance_id_default_key);
        return sharedPreferences.getString(key, default_value);
    }
}
