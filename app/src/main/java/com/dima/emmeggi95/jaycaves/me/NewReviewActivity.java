package com.dima.emmeggi95.jaycaves.me;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.entities.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class NewReviewActivity extends AppCompatActivity {

    private TextView title;
    private TextView artist;
    private Album album;
    private RatingBar ratingBar;
    private MultiAutoCompleteTextView essay;
    private Button sendButton;
    private String albumKey;
    private int votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        album = (Album) getIntent().getExtras().get("album");
        albumKey = album.getTitle()+"@"+album.getArtist();
        title = findViewById(R.id.new_review_album_title);
        title.setText(album.getTitle());
        artist = findViewById(R.id.new_review_artist);
        artist.setText("By "+ album.getArtist());
        ratingBar= findViewById(R.id.new_review_ratingbar);
        essay= findViewById(R.id.new_review_text);
        sendButton= findViewById(R.id.createNewReviewButton);
        votes=-1;

        // Keep Votes consistent
        FirebaseDatabase.getInstance().getReference("reviews").orderByChild("title").equalTo(albumKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                votes=0;
                for (DataSnapshot d : data)
                   votes++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ERROR GETTING VOTES");
            }
        });

        // Keeps Score consistent
        FirebaseDatabase.getInstance().getReference("albums").child(albumKey).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d : data)
                    album.setScore((double) d.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ERROR GETTING SCORES");
            }
        });


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


        // need to retrieve user credentials
        Review review = new Review("pietro@grotti", albumKey,
                essay.getText().toString(), ratingBar.getRating(),formatter.format(date) ,0);

        if (votes != -1) {
            FirebaseDatabase.getInstance().getReference("reviews").child(CustomRandomId.randomIdGenerator()).setValue(review)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            votes++;
                            if (album.getScore()!= 0)
                                album.setScore((album.getScore() + ratingBar.getRating()) / votes);
                            else
                                album.setScore(ratingBar.getRating());
                            FirebaseDatabase.getInstance().getReference("albums").child(albumKey).child("score").setValue(album.getScore()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(getCurrentFocus(), "Review successfully sent!", Snackbar.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
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
        } else
            Snackbar.make(getCurrentFocus(), "Error! refresh the page and try again", Snackbar.LENGTH_LONG).show();

    }


    private void showMissingContentMessage(){
            if (essay.getText().toString().length() == 0)
                Snackbar.make(getCurrentFocus(), "Please insert a valid text!", Snackbar.LENGTH_LONG).show();
            if (ratingBar.getRating() == 0.0)
                Snackbar.make(getCurrentFocus(), "Please rate before submitting!", Snackbar.LENGTH_LONG).show();
    }




}
