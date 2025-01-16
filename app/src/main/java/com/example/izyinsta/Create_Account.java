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

public class Create_Account extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText email;
    private TextView dbgText;

    public String servUrl = "http://android.chocolatine-rt.fr:7217/androidServ/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        username = findViewById(R.id.textNameInput);
        password = findViewById(R.id.textMDPInput);

        // dbgText = findViewById(R.id.dbgText);
    }

    public void register (View v) {
        OkHttpClient client = new OkHttpClient();
        String url = servUrl + "index.php";

        RequestBody body = new FormBody.Builder()
                .add("username", username.getText().toString())
                .add("password", password.getText().toString())
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
                    Create_Account.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //dbgText.setText(myResponse);
                        }
                    });
                }
                else {
                    Log.d("fetchPost", "onResponse: "+response.toString());
                }

            }
        });
    }

    public void login(View v) {
        Intent intent = new Intent(this, Create_Account.class);
        startActivity(intent);
    }
}