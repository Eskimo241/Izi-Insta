package com.example.izyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Create_Account extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText email;
    private TextView statusText;

    public String servUrl = "http://android.chocolatine-rt.fr:7217/androidServ/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        username = findViewById(R.id.textNameInput);
        password = findViewById(R.id.textMDPInput);
        email = findViewById(R.id.textEmailInput);
        statusText = findViewById(R.id.statusText);
    }

    public void register (View v) {
        OkHttpClient client = new OkHttpClient();


        RequestBody body = new FormBody.Builder()
                .add("username", username.getText().toString())
                .add("password", password.getText().toString())
                .add("email", email.getText().toString())
                .add("action", "register")
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "index.php")
                .post(body)
                .build();

        Log.d("fetchPost", "fetchPost: "+request);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("fetchPost", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final String myResponse = response.body().string();
                    Create_Account.this.runOnUiThread(() -> {
                        try {
                            JSONObject obj = new JSONObject(myResponse);
                            if (obj.getString("success").equals("1")) {

                                //If login is successful, we save the username and password in shared preferences
                                SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", username.getText().toString());
                                editor.putString("password", password.getText().toString());
                                editor.apply();

                                //Redirect to main activity
                                Intent intent = new Intent(Create_Account.this, Tendency.class);
                                startActivity(intent);
                            }
                            else {
                                Log.d("DBG", "CreateAccountFailed");
                                Log.d("DBG", "onResponse: "+myResponse);
                                switch (obj.getString("success")) {
                                    case "2":
                                        statusText.setText("Email invalide");
                                        break;
                                    case "3":
                                        statusText.setText("Email déjà utilisé");
                                        break;
                                    case "4":
                                        statusText.setText("Nom d'utilisateur invalide.");
                                        break;
                                    case "5":
                                        statusText.setText("Nom d'utilisateur déjà utilisé");
                                        break;
                                    default:
                                        statusText.setText("Unknown error");
                                }

                        }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                else {
                    Log.d("DBG", "onResponse: "+ response);
                }

            }
        });
    }

    public void login(View v) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}