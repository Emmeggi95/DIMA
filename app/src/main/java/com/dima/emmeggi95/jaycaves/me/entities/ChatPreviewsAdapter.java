package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;

import java.util.List;

public class ChatPreviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<ChatPreview> chats;

    public ChatPreviewsAdapter(Context context, List<ChatPreview> chats) {
        this.context = context;
        this.chats = chats;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView user, description;
        ImageView icon;
        CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_name);
            description = itemView.findViewById(R.id.last_message);
            icon = itemView.findViewById(R.id.user_icon);
            card = itemView.findViewById(R.id.chat_card);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_preview_card, viewGroup, false);
        return new ChatPreviewsAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatPreview chat = chats.get(i);
        ItemViewHolder h = (ItemViewHolder) viewHolder;

        // Get user name and image from DB
        h.user.setText("username");
        //h.icon.set...

        if(chat.getUnreadMessages()>0){
            h.description.setText(chat.getUnreadMessages() + " unread messages");
        } else {
            h.description.setText("Last message: " + chat.getLastMessage());
        }

        h.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to chat
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
