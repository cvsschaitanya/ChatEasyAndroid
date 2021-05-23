package com.example.chateasy;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SocketIOHelper {
    public static Socket mSocket;
    public static String name;
    public static ContactAdapter contactAdapter;
    public static ChatAdapter chatAdapter;
    public static Activity presentActivity;

    public static TextView getGreetingCard() {
        return greetingCard;
    }

    public static void setGreetingCard(TextView greetingCard) {
        SocketIOHelper.greetingCard = greetingCard;
    }

    private static TextView greetingCard;

    public static void init() {
        contactAdapter = new ContactAdapter();

        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Cookie", RetrofitHelper.cookie)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        try {
            IO.Options options = new IO.Options();
            options.webSocketFactory = httpClient;
            options.callFactory = httpClient;
            mSocket = IO.socket("https://chateasychat.herokuapp.com", options);

            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("init");
                }
            });

            mSocket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("message", args[0].toString());
                }
            });

            mSocket.on("re-init", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("init");
                }
            });

            mSocket.on("init-reply", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    presentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                SocketIOHelper.name = data.get("name").toString();

                                JSONArray contactsArray = data.getJSONArray("contacts");
                                contactAdapter.getContactList().clear();
                                for (int i = 0; i < contactsArray.length(); ++i) {
                                    contactAdapter.getContactList().add(contactsArray.get(i).toString());
                                    contactAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                Log.e("JSON", e.getMessage());
                            }
                        }
                    });
                }
            });

            mSocket.on("new-chat-list", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    presentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray chatsArray = (JSONArray) args[0];
                            try {
                                for (int i = 0; i < chatsArray.length(); ++i) {
                                    JSONObject chatObj = (JSONObject) chatsArray.get(i);
                                    chatAdapter.getChatList().add(new Chat(
                                            chatObj.getString("_FROM"),
                                            chatObj.getString("_TO"),
                                            chatObj.getString("_MESSAGE")
                                    ));
                                    chatAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                Log.e("JSON", e.getMessage());
                            }
                        }
                    });
                }
            });

            mSocket.on("hard-resign", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Toast.makeText(presentActivity, "Another Login Detected", Toast.LENGTH_LONG).show();
                    signout();
                }
            });

        } catch (URISyntaxException e) {
            Log.i("Socket", e.getMessage());
        }
    }

    public static void signout() {
        mSocket.disconnect();
        SocketIOHelper.mSocket = null;
        SocketIOHelper.name = null;
        SocketIOHelper.contactAdapter = null;
        SocketIOHelper.greetingCard = null;

        RetrofitHelper.clear();
        Intent intent = new Intent(presentActivity, LoginActivity.class);
        presentActivity.startActivity(intent);
    }

}
