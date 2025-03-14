package com.polnareff.izyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;


public class Home extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView dbgText;

    private static final String servUrl = Constants.SERV_URL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = findViewById(R.id.textNameInput);
        password = findViewById(R.id.textMDPInput);

        dbgText = findViewById(R.id.dbgText);


        Intent intent = getIntent();
        String message = intent.getStringExtra("error_message");
        if (message != null) {
            dbgText.setText(message);
        }

        //Checking if client has previously logged in using shared preferences
        SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);



        String savedUsername = preferences.getString("username", "");
        String savedPassword = preferences.getString("password", "");

        //Debugging, you can see the clear password lol, pretty secure
        Log.d("DBG", "savedUsername: "+savedUsername);
        Log.d("DBG", "savedPassword: "+savedPassword);

        //If the client has previously logged in, we log him in automatically
        if (!savedUsername.equals("") && !savedPassword.equals("")) {
            username.setText(savedUsername);
            password.setText(savedPassword);
            register(null);
        }


    }

    public void register (View v) {
        OkHttpClient client = Constants.getHttpClient();



        RequestBody body = new FormBody.Builder()
                .add("username", username.getText().toString())
                .add("password", password.getText().toString())
                .add("action", "login")
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "index.php")
                .post(body)
                .build();

        Log.d("DBG", "fetchPost: "+request);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Home.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Connection au serveur impossible", Toast.LENGTH_SHORT).show());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("dbg", "onResponse: "+response);
                    assert response.body() != null;
                    final String myResponse = response.body().string();
                    Log.d("DBG", "onResponse: "+myResponse);
                    Home.this.runOnUiThread(() -> {
                        try {
                            JSONObject obj = new JSONObject(myResponse);

                            switch (obj.getString("success")) {
                                case "0":
                                    Log.d("DBG", "LoginFailed");
                                    dbgText.setText("Nom d'utilisateur ou mot de passe incorrect");
                                    break;
                                case "1":
                                    //If login is successful, we save the username and password in shared preferences
                                    SharedPreferences preferences = getSharedPreferences("com.example.izyinsta.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("username", username.getText().toString());
                                    editor.putString("password", password.getText().toString());
                                    editor.apply();

                                    //Redirect to main activity
                                    Intent intent = new Intent(Home.this, Tendency.class);
                                    startActivity(intent);
                                    break;
                                case "2":
                                    Log.d("DBG", "Banned");
                                    dbgText.setText("Vous avez été banni");
                                    Toast.makeText(getApplicationContext(), "Vous avez été banni", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Log.d("DBG", "LoginFailed");
                                    break;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        //dbgText.setText(myResponse);
                        Log.d("TAG", "Login Response: "+myResponse);
                    });
                }
                else {
                    Home.this.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Connection au serveur impossible", Toast.LENGTH_SHORT).show();
                        dbgText.setText("Connection au serveur impossible");
                    });
                    Log.d("fetchPost", "onResponse: "+response);

                }

            }
        });
    }

    public void createAccount(View v) {
        Intent intent = new Intent(this, Create_Account.class);
        startActivity(intent);
    }
}