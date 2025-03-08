package com.polnareff.izyinsta;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import okhttp3.OkHttpClient;

public class Constants {
    public static final String SERV_HOST = "android.chocolatine-rt.fr";
    public static final String SERV_URL = "https://"+SERV_HOST +"/androidServ/";

    public static final String PREFERENCE_FILE_KEY = "com.polnareff.izyinsta.PREFERENCE_FILE_KEY";

    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .hostnameVerifier((hostname, session) -> hostname.equals(SERV_HOST) || hostname.endsWith(".eu.ngrok.io"))
                    .build();
        }
        return client;
    }

    public static void verifySavedUsername(String savedUsername, Activity activity) {
        if(savedUsername.equals("")) {
            Log.e("DBG", "uploadImageToServer: No username found in shared preferences");
            Intent intent = new Intent(activity, Home.class);
            intent.putExtra("error_message", "Une erreur s'est produite, veuillez vous reconnecter");

            activity.startActivity(intent);
            return;
        }
    }
}
