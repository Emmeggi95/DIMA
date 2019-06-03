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

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NotificationReader {

    private Context context;
    private List<Notification> notifications;
    private ItemViewHolder currentHolder;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
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
        Notification notification = notifications.get(i);
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
                /* Retrieve album and review IDs from notification and download data from DB
                Intent intent = new Intent(ALbumActivity.class);
                intent.putExtra("album", ...);
                intent.putExtra("review", ...);
                context.startActivity(intent);
                 */
                notification.setRead();
                NotificationsAdapter.this.notifyDataSetChanged();
            }
        });
    }

    private void setReadState(Notification notification){
        if(notification.isRead()){
           currentHolder.text.setTextColor(context.getResources().getColor(R.color.colorGrayText));
        } else {
            currentHolder.card.setBackgroundColor(context.getResources().getColor(R.color.colorAccentMuted));
        }
    }
}
