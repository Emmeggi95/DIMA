package com.dima.emmeggi95.jaycaves.me.entities.adapters;

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

import com.dima.emmeggi95.jaycaves.me.activities.ChatActivity;
import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.CoverCache;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.db.ChatPreview;
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
    private DatabaseReference prefReference = FirebaseDatabase.getInstance().getReference("preferences");
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
        final String mail;

        String userId = "";
            if (chat.getUser_1().equals(User.uid))
                userId= chat.getUser_2();
            if (chat.getUser_2().equals(User.uid))
                userId = chat.getUser_1();

        // Get user name and image from DB

        if(userId!=""){
            prefReference.orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                    for(DataSnapshot d: data){
                        final AccountPreference pref = d.getValue(AccountPreference.class);
                        cover_id = pref.getCoverphoto();
                        h.card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("username", pref.getUsername());
                                context.startActivity(intent);
                            }
                        });

                    }
                    CoverCache.retrieveCover(cover_id, h.icon, h.loading,
                            context.getDir(CoverCache.INTERNAL_DIRECTORY_USERS,MODE_PRIVATE));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(chat.getUnreadMessages_1()>0){
            h.description.setText(chat.getUnreadMessages_1() + " unread messages");
        } else {
          //  h.description.setText("Last message: " + chat.getLastMessageFormatted());
        }


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
