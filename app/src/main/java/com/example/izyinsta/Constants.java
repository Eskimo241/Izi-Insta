package com.example.izyinsta;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class Constants {
    public static final String SERV_HOST = "android.chocolatine-rt.fr";
    public static final String SERV_URL = "https://"+SERV_HOST +"/androidServ/";
    //public static final String servUrl = "https://android.chocolatine-rt.fr/androidServ/";

    public static final String PREFERENCE_FILE_KEY = "com.example.izyinsta.PREFERENCE_FILE_KEY";

    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return hostname.equals(SERV_HOST) || hostname.endsWith(".eu.ngrok.io");
                        }
                    })
                    .build();
        }
        return client;
    }
}
