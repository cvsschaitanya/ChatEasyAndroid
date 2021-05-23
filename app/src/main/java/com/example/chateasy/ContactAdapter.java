package com.example.chateasy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class ContactAdapter extends RecyclerView.Adapter {
    private List<String> contactList;

    public ContactAdapter() {
        this.contactList = new ArrayList<>();
    }

    public List<String> getContactList() {
        return contactList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.contact_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String peername = ((TextView) v.findViewById(R.id.contact_name)).getText().toString();

                    Intent intent = new Intent(SocketIOHelper.presentActivity, ChatActivity.class);
                    intent.putExtra("peername", peername);
                    SocketIOHelper.presentActivity.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(this.contactList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.contactList.size();
    }
}
