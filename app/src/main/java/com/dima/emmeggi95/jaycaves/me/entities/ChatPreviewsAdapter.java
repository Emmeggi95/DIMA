package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.ChatActivity;
import com.dima.emmeggi95.jaycaves.me.CoverCache;
import com.dima.emmeggi95.jaycaves.me.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ChatPreviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<ChatPreview> chats;

    // DB
    private DatabaseReference coverReference = FirebaseDatabase.getInstance().getReference("preferences");
    String cover_id;

    public ChatPreviewsAdapter(Context context, List<ChatPreview> chats) {
        this.context = context;
        this.chats = chats;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView user, description;
        ImageView icon;
        CardView card;
        ProgressBar loading;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_name);
            description = itemView.findViewById(R.id.last_message);
            icon = itemView.findViewById(R.id.user_icon);
            card = itemView.findViewById(R.id.chat_card);
            loading = itemView.findViewById(R.id.progressBar);

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
        final ChatPreview chat = chats.get(i);
        final ItemViewHolder h = (ItemViewHolder) viewHolder;

        // Get user name and image from DB
        h.user.setText(chat.getUsername());
        if(chat.getUsername()!=null){
            coverReference.orderByChild("username").equalTo(chat.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                    for(DataSnapshot d: data){
                        cover_id = d.getValue(AccountPreference.class).getCoverphoto();
                    }
                    CoverCache.retrieveCover(cover_id, h.icon, h.loading,
                            context.getDir(CoverCache.INTERNAL_DIRECTORY_USERS,MODE_PRIVATE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(chat.getUnreadMessages()>0){
            h.description.setText(chat.getUnreadMessages() + " unread messages");
        } else {
            h.description.setText("Last message: " + chat.getLastMessageFormatted());
        }

        h.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("username", chat.getUsername());
                intent.putExtra("email", chat.getEmail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
