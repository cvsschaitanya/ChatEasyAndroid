package com.example.chateasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String peername;
    private Toolbar toolbar;
    private TextView peernameView;
    private RecyclerView recyclerView;
    private EditText message_field;
    private FloatingActionButton send_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        peernameView=findViewById(R.id.peername);
        SocketIOHelper.presentActivity = this;
        peername = getIntent().getStringExtra("peername");
        peernameView.setText(peername);
        JSONArray couple;
        try {
            couple = new JSONArray(new String[]{peername, SocketIOHelper.name});
            SocketIOHelper.mSocket.emit("extract-chats-between", couple);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SocketIOHelper.chatAdapter = new ChatAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(SocketIOHelper.chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);

        message_field = findViewById(R.id.message_box);
        send_button = findViewById(R.id.send_button);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map = new HashMap<>();
                map.put("_FROM",SocketIOHelper.name);
                map.put("_TO",peername);
                map.put("_MESSAGE",message_field.getText().toString());

                JSONObject chatObj = new JSONObject(map);
                SocketIOHelper.mSocket.emit("new-chat",chatObj);
                message_field.setText("");
            }
        });
    }
}