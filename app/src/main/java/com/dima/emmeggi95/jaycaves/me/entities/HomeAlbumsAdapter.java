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
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.AlbumActivity;
import com.dima.emmeggi95.jaycaves.me.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to inflate the content in the RecyclerView in the Home Page.
 */
public class HomeAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<HomeAlbum> albumList;
    private static HomeAlbum header;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * @param mContext
     * @param albumList
     */
    public HomeAlbumsAdapter(Context mContext, List<HomeAlbum> albumList) {
        this.context = mContext;
        if(header==null){
            header = new HomeAlbum(context.getResources().getString(R.string.home_header_title));
        }
        if(albumList.get(0)!=header){
            albumList.add(0, header);
        }
        this.albumList = albumList;
    }

    /**
     * Holder to manage a single item in the RecyclerView
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, genre, rating;
        public ImageView cover;
        public List<ImageView> stars;
        public CardView card;

        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_album_title);
            author = (TextView) view.findViewById(R.id.text_author);
            genre = (TextView) view.findViewById(R.id.text_genre);
            cover = (ImageView) view.findViewById(R.id.image_cover);
            rating = (TextView) view.findViewById(R.id.text_rating);
            stars = new ArrayList<ImageView>();
            stars.add((ImageView) view.findViewById(R.id.star_1));
            stars.add((ImageView) view.findViewById(R.id.star_2));
            stars.add((ImageView) view.findViewById(R.id.star_3));
            stars.add((ImageView) view.findViewById(R.id.star_4));
            stars.add((ImageView) view.findViewById(R.id.star_5));
            card = (CardView) view.findViewById(R.id.card);
        }
    }

    /**
     * Holder to manage the header of the RecyclerView
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        
        public HeaderViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.home_header_text);
        }
    }

    /**
     * Choose the layout to inflate based on the viewType (item or header)
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_home, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_album_card, parent, false);
            return new ItemViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    /**
     * Set the parameters of each item from the corresponding Album in albumList
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        HomeAlbum album = albumList.get(position);
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).text.setText(album.getTitle());
        }else if(holder instanceof ItemViewHolder){
            ((ItemViewHolder) holder).title.setText(album.getTitle());
            ((ItemViewHolder) holder).author.setText(album.getAuthor());
            ((ItemViewHolder) holder).genre.setText(album.getGenre());
            ((ItemViewHolder) holder).rating.setText(String.format("%.2f", album.getScore()));
            ((ItemViewHolder) holder).cover.setImageResource(album.getCover());

            //Set rating stars
            int integerScore = (int) album.getScore();
            int i;
            for(i=0; i<integerScore; i++){
                ((ItemViewHolder) holder).stars.get(i).setImageResource(R.drawable.ic_star_24dp);
            }
            float decimalPart = (float) album.getScore() - integerScore;
            if(decimalPart >= 0.5){
                ((ItemViewHolder) holder).stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
            }

            //Set listener to open AlbumActivity
            ((ItemViewHolder) holder).card.setOnClickListener(new CardView.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("album", albumList.get(position));
                    context.startActivity(intent);
                }
            });
        }

    }

    private HomeAlbum getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    /**
     * This method is used "behind the scenes" in the onCreateViewHolder to set the header in first position and the items in all the following positions in the RecyclerView
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}