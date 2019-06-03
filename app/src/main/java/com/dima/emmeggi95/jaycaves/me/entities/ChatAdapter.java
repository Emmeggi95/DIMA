package com.dima.emmeggi95.jaycaves.me.entities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.User;
import com.dima.emmeggi95.jaycaves.me.view_models.MessagesViewModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> messages;
    private MessagesViewModel viewModel;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    public ChatAdapter(Context context, List<Message> messages, MessagesViewModel viewModel) {
        this.context = context;
        this.messages = messages;
        this.viewModel = viewModel;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView text, time;
        LinearLayout bubble;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
            time = itemView.findViewById(R.id.message_time);
            bubble = itemView.findViewById(R.id.message_container);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        CardView card;
        public LoadingViewHolder(@NonNull View itemView) {

            super(itemView);
            card = itemView.findViewById(R.id.load_card);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_card, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, viewGroup, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof ItemViewHolder){

            Message message = messages.get(i);
            ItemViewHolder h = (ItemViewHolder) viewHolder;
            h.text.setText(message.getText());
            h.time.setText(message.getTime());
            ViewGroup.LayoutParams params = h.bubble.getLayoutParams();
            if(message.getUserName().equals(User.username)){
                ((ViewGroup.MarginLayoutParams) params).leftMargin = (int) context.getResources().getDimension(R.dimen.side_bubble_margin);
                ((ViewGroup.MarginLayoutParams) params).rightMargin = (int) context.getResources().getDimension(R.dimen.layout_margin);
                h.bubble.setBackground(context.getDrawable(R.drawable.my_message_bubble));
                h.text.setTextColor(context.getResources().getColor(R.color.colorTextOnDarkBackground));
                h.time.setTextColor(context.getResources().getColor(R.color.colorTextOnDarkBackgroundSecondary));
            } else {
                ((ViewGroup.MarginLayoutParams) params).leftMargin = (int) context.getResources().getDimension(R.dimen.layout_margin);
                ((ViewGroup.MarginLayoutParams) params).rightMargin = (int) context.getResources().getDimension(R.dimen.side_bubble_margin);
                h.bubble.setBackground(context.getDrawable(R.drawable.their_message_bubble));
                h.text.setTextColor(context.getResources().getColor(R.color.colorTextOnPrimary));
                h.time.setTextColor(context.getResources().getColor(R.color.colorGrayText));
            }

        } else if (viewHolder instanceof LoadingViewHolder){
            final LoadingViewHolder h = (LoadingViewHolder) viewHolder;
            h.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.downloadMoreMessages();
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position)==null){
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
