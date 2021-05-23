package com.example.chateasy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitHelper {
    public static ChatEasyApi chatEasyApi;
    public static String cookie;

    static class MyCookieStore extends SQLiteOpenHelper {
        public static MyCookieStore store;
        SQLiteDatabase db;

        public MyCookieStore(@Nullable Context context) {
            super(context, "cookiebase", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            this.db = db;
            db.execSQL("create table if not exists cookietable (" +
//                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "cookie varchar" +
                            ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void set(String cookie) {
            this.reset();
            this.getWritableDatabase().execSQL("INSERT INTO cookietable (cookie) values(\'" +
                    cookie +
                    "\');");
        }

        public void reset() {
            this.getWritableDatabase().execSQL("DELETE FROM cookietable;");
        }

        public String get() {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM cookietable;", null);
            if (cursor.getCount()==0)
                return null;

            cursor.moveToFirst();
            return cursor.getString(0);
        }

    }

    public static void init(String BASEURL) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(new OkHttpClient().newBuilder().cookieJar(new CookieJar() {
                    private List<Cookie> cookies;

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        if (url.encodedPath().endsWith("signin") || url.encodedPath().endsWith("register")) {
                            this.cookies = new ArrayList<>(cookies);
                            RetrofitHelper.cookie = "";
                            for (Cookie cookie : this.cookies) {
                                RetrofitHelper.cookie += cookie.name() + "=" + cookie.value() + "; ";
                            }
                            RetrofitHelper.save();
                            Log.d("Recvd cookies:", RetrofitHelper.cookie);
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        if (this.cookies != null) {
                            String s = "";
                            for (Cookie cookie : this.cookies) {
                                s += cookie.name() + "\n";
                            }
                            Log.d("Sent cookies:", s);
                            return this.cookies;
                        }
                        return Collections.emptyList();
                    }
                }).build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        chatEasyApi = retrofit.create(ChatEasyApi.class);
    }

    public static void clear() {
        RetrofitHelper.cookie = null;
        MyCookieStore.store.reset();
    }

    public static void save() {
        MyCookieStore.store.set(RetrofitHelper.cookie);
    }

}
