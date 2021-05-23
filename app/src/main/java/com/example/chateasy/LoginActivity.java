package com.example.chateasy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.chateasy.RetrofitHelper.chatEasyApi;

public class LoginActivity extends AppCompatActivity {
    private Button signin_button, signup_button;
    private EditText username_field, password_field;
    Call<SimpleResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RetrofitHelper.MyCookieStore.store = new RetrofitHelper.MyCookieStore(this);
        checkForLogin();


        username_field = findViewById(R.id.username);
        password_field = findViewById(R.id.password);
        signin_button = findViewById(R.id.signin_button);
        signup_button = findViewById(R.id.signup_button);

        RetrofitHelper.init("https://chateasychat.herokuapp.com");

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call = chatEasyApi.createUser(new User(username_field.getText().toString(), password_field.getText().toString()));
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.isSuccessful()) {
                            Log.i("Response: Okay: ", response.body().response);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i("Response: Problem: ", response.message());
                            if(response.code()==409)
                                Toast.makeText(LoginActivity.this,"Username Exists",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this,response.message(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i("Response", t.getMessage());
                    }
                });
            }

        });

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call = chatEasyApi.login(new User(username_field.getText().toString(), password_field.getText().toString()));
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.isSuccessful()) {
                            Log.i("Response: Okay: ", response.body().response);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.i("Response: Problem: ", response.message());
                            if(response.code()==404)
                                Toast.makeText(LoginActivity.this,"No Such user",Toast.LENGTH_LONG).show();
                            else if(response.code()==403)
                                Toast.makeText(LoginActivity.this,"Wrong Password",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this,response.message(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i("Response", t.getMessage());
                    }
                });
            }
        });
    }

    private void checkForLogin() {
        String cookie = RetrofitHelper.MyCookieStore.store.get();
        if (cookie == null)
            return;

        RetrofitHelper.cookie = cookie;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}