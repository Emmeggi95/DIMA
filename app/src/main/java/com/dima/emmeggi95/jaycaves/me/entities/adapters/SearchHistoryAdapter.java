package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.activities.SearchableActivity;

import java.util.List;

/**
 * Displays query results in @SearchableActivity
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter {

    private SearchableActivity activity;
    private Context context;
    private List<String> history;

    public SearchHistoryAdapter(SearchableActivity activity, Context context, List<String> history) {
        this.activity = activity;
        this.context = context;
        this.history = history;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ConstraintLayout container;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
            container = itemView.findViewById(R.id.item_container);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_history_item, viewGroup, false);
        return new SearchHistoryAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemViewHolder h = (ItemViewHolder) viewHolder;
        final String item = history.get(i);
        h.text.setText(item);
        h.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickOnHistoryItem(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public void setItems(List<String> history){
        this.history = history;
    }
}
