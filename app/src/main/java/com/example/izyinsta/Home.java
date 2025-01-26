package com.example.izyinsta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
public class Home extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText email;
    private TextView dbgText;

    //public String servUrl = "http://android.chocolatine-rt.fr:7217/androidServ/";
    public String servUrl = "http://10.192.16.90/androidServ/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = findViewById(R.id.textNameInput);
        password = findViewById(R.id.textMDPInput);

        dbgText = findViewById(R.id.dbgText);
    }

    public void register (View v) {
        OkHttpClient client = new OkHttpClient();
        String url = servUrl + "index.php";

        RequestBody body = new FormBody.Builder()
                .add("username", username.getText().toString())
                .add("password", password.getText().toString())
                .add("action", "login")
                .build();

        Request request = new Request.Builder()
                .url(servUrl + "index.php")
                .post(body)
                .build();

        Log.d("fetchPost", "fetchPost: "+request.toString());
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("fetchPost", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Home.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(myResponse);
                                if (obj.getString("success").equals("1")) {
                                    Intent intent = new Intent(Home.this, devSend.class);
                                    startActivity(intent);
                                }
                                else {
                                    Log.d("DBG", "LoginFailed");
                                    dbgText.setText("Incorrect username or password");
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            //dbgText.setText(myResponse);
                            Log.d("TAG", "Login Response: "+myResponse);
                        }
                    });
                }
                else {
                    Log.d("fetchPost", "onResponse: "+response.toString());
                    dbgText.setText("Error connecting to the server");
                }

            }
        });
    }

    public void createAccount(View v) {
        Intent intent = new Intent(this, Create_Account.class);
        startActivity(intent);
    }
}