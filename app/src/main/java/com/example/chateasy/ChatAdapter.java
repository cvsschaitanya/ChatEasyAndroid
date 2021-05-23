package com.example.chateasy;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class Chat{
    public String _FROM,_TO,_MESSAGE;

    public Chat(String _FROM, String _TO, String _MESSAGE) {
        this._FROM = _FROM;
        this._TO = _TO;
        this._MESSAGE = _MESSAGE;
    }
}

class ChatAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Chat> chatList;

    public ChatAdapter(Context context) {
        this.context = context;
        this.chatList = new ArrayList<>();
    }

    public List<Chat> getChatList() {
        return chatList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.textView = itemView.findViewById(R.id.chat_box);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        int position = this.getItemCount()-1-pos;
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(this.chatList.get(position)._MESSAGE);
        if(this.chatList.get(position)._FROM.equals(SocketIOHelper.name)){
            viewHolder.view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//            viewHolder.textView.setGravity(Gravity.RIGHT);
            viewHolder.textView.setBackground(context.getResources().getDrawable(R.drawable.my_msg));
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorOnDark));
        }
        else{
            viewHolder.view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            viewHolder.textView.setGravity(Gravity.LEFT);
            viewHolder.textView.setBackground(context.getResources().getDrawable(R.drawable.peer_msg));
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.colorOnLight));
        }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
