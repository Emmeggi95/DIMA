package com.dima.emmegi95.jaycaves.sm2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;


public class HomeAlbumsAdapter extends RecyclerView.Adapter<HomeAlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<HomeAlbum> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, genre, rating;
        public ImageView cover;
        public List<ImageView> stars;
        public CardView card;

        public MyViewHolder(View view) {
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


    public HomeAlbumsAdapter(Context mContext, List<HomeAlbum> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HomeAlbum album = albumList.get(position);
        holder.title.setText(album.getTitle());
        holder.author.setText(album.getAuthor());
        holder.genre.setText(album.getGenre());
        holder.rating.setText(String.format("%.2f", album.getScore()));
        holder.cover.setImageResource(album.getCover());

        //Set rating stars
        int integerScore = (int) album.getScore();
        int i;
        for(i=0; i<integerScore; i++){
            holder.stars.get(i).setImageResource(R.drawable.ic_star_24dp);
        }
        float decimalPart = (float) album.getScore() - integerScore;
        if(decimalPart >= 0.5){
            holder.stars.get(i).setImageResource(R.drawable.ic_star_half_24dp);
        }

        //Set card background color based on the album cover
        Bitmap coverBitmap = BitmapFactory.decodeResource(mContext.getResources(), album.getCover());
        Palette p = Palette.from(coverBitmap).generate();
        holder.card.setCardBackgroundColor(p.getMutedSwatch().getRgb());
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}