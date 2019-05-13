package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewReviewActivity extends AppCompatActivity {

    private TextView title;
    private TextView artist;
    private Album album;
    private Review oldReview;
    private RatingBar ratingBar;
    private MultiAutoCompleteTextView essay;
    private Button sendButton;
    private String albumKey;
    private AutoCompleteTextView headliner;
    private DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
    private DatabaseReference albumsRef = FirebaseDatabase.getInstance().getReference("albums");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);


        album = (Album) getIntent().getExtras().get("album");

        albumKey = album.getTitle() + "@" + album.getArtist();
        title = findViewById(R.id.new_review_album_title);
        title.setText(album.getTitle());
        artist = findViewById(R.id.new_review_artist);
        artist.setText("By " + album.getArtist());

        ratingBar= findViewById(R.id.new_review_ratingbar);
        essay= findViewById(R.id.new_review_text);
        sendButton= findViewById(R.id.createNewReviewButton);
        headliner = findViewById(R.id.new_review_headline);


        oldReview = (Review) getIntent().getExtras().get("review");
        if (oldReview!=null){
            ratingBar.setRating((float) oldReview.getRating());
            essay.setText(oldReview.getBody());
            headliner.setText(oldReview.getHeadline());
        }



        // Button listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCheckOk())
                    registerReview();
                else
                    showMissingContentMessage();
            }
        });

    }

    private boolean inputCheckOk(){
        return ((essay.getText().toString().length()>0) && (ratingBar.getRating() != 0.0));
    }

    private void registerReview(){


        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());



        final Review review = new Review(User.getUsername(), albumKey, headliner.getText().toString(),
                essay.getText().toString(), ratingBar.getRating(),formatter.format(date));


            String id;

            if(oldReview!=null)
                id= oldReview.getId(); // keep old id
            else
                id= CustomRandomId.randomIdGenerator(); // generate random id

            review.setId(id);
            reviewsRef.child(id).setValue(review)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        reviewsRef.orderByChild("title").equalTo(albumKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                                int count=0;
                                double sum=0;
                                for (DataSnapshot d : data){
                                    count++;
                                    sum+=d.getValue(Review.class).getRating();
                                }
                                FirebaseDatabase.getInstance().getReference("albums").child(albumKey).child("score").setValue(sum/count).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        User.getReviews().add(review);
                                        Snackbar.make(getCurrentFocus(), "Review successfully sent!", Snackbar.LENGTH_LONG).show();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(getCurrentFocus(), "Oops, try again!", Snackbar.LENGTH_LONG).show();
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Snackbar.make(getCurrentFocus(), "Oops, try again!", Snackbar.LENGTH_LONG).show();
                            }
                        });




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(getCurrentFocus(), "Oops, try again!", Snackbar.LENGTH_LONG).show();
                        }
                    });


    }


    private void showMissingContentMessage(){
            if (essay.getText().toString().length() == 0)
                Snackbar.make(getCurrentFocus(), "Please insert a valid text!", Snackbar.LENGTH_LONG).show();
            if (ratingBar.getRating() == 0.0)
                Snackbar.make(getCurrentFocus(), "Please rate before submitting!", Snackbar.LENGTH_LONG).show();
    }



}
