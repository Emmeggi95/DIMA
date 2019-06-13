package com.dima.emmeggi95.jaycaves.me.entities.adapters;

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
import com.dima.emmeggi95.jaycaves.me.entities.CustomNotification;
import com.dima.emmeggi95.jaycaves.me.entities.NotificationLike;
import com.dima.emmeggi95.jaycaves.me.entities.NotificationReader;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.entities.db.Album;
import com.dima.emmeggi95.jaycaves.me.view_models.NotificationsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NotificationReader {

    private Context context;
    private List<NotificationLike> notifications;
    private ItemViewHolder currentHolder;
    private HashMap<String,Album> albumHashMap;
    private DatabaseReference notificationReference= FirebaseDatabase.getInstance().getReference("notifications").child(User.uid);

    public NotificationsAdapter(Context context, List<NotificationLike> notifications) {
        this.context = context;
        this.notifications = notifications;

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView text, date;
        ImageView icon;
        CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.notification_text);
            date = itemView.findViewById(R.id.notification_date);
            icon = itemView.findViewById(R.id.notification_icon);
            card = itemView.findViewById(R.id.notification_card);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_card, viewGroup, false);
        return new NotificationsAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        CustomNotification notification = notifications.get(i);
        currentHolder = (ItemViewHolder) viewHolder;
        notification.read(this);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public void notify(final NotificationLike notification) {

        currentHolder.text.setText(notification.getMessage());
        currentHolder.date.setText(notification.getDate());
        setReadState(notification);
        currentHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NotificationsViewModel.albumList
                notification.setRead(true);
                NotificationsAdapter.this.notifyDataSetChanged();
                notificationReference.orderByChild("liker").equalTo(notification.getLiker()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        NotificationLike newVal = new NotificationLike();
                        String id ="";
                        for(DataSnapshot d: data){
                            newVal = d.getValue(NotificationLike.class);
                            id = d.getKey();
                        }
                        newVal.setRead(true);
                        notificationReference.child(id).setValue(newVal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void setReadState(CustomNotification notification){
        if(notification.isRead()){
           currentHolder.text.setTextColor(context.getResources().getColor(R.color.colorGrayText));
            currentHolder.card.setBackgroundColor(context.getResources().getColor(R.color.colorLightBackground));

        } else {
            currentHolder.card.setBackgroundColor(context.getResources().getColor(R.color.colorAccentMuted));
        }
    }
}
