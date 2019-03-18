package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Iterator;

import static com.dima.emmeggi95.jaycaves.me.MainActivity.PHOTO_PICKER;

/**
 * This activity is where the user can insert into the catalogue new albums
 */
public class CreateNewAlbumActivity extends AppCompatActivity {

    // Backend references
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;

    // Layout elements
    private ImageView newAlbumPicture;
    private EditText newAlbumNameInput;
    private EditText newAlbumReleaseDateInput;
    private EditText newAlbumArtistInput;
    private EditText newAlbumGenreInput1;
    private EditText newAlbumGenreInput2;
    private EditText newAlbumGenreInput3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Storage setup
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("Album_covers");

        // Database setup
        database= FirebaseDatabase.getInstance();
        dbReference= database.getReference("albums");


        // Activity View Setup
        setContentView(R.layout.activity_create_new_album);
        newAlbumNameInput = findViewById(R.id.newAlbumNameInput);
        newAlbumReleaseDateInput = findViewById(R.id.newAlbumReleaseDateInput);
        newAlbumArtistInput = findViewById(R.id.newAlbumArtistInput);
        newAlbumGenreInput1 = findViewById(R.id.newAlbumGenreInput1);
        newAlbumGenreInput2 = findViewById(R.id.newAlbumGenreInput2);
        newAlbumGenreInput3 = findViewById(R.id.newAlbumGenreInput3);
        newAlbumPicture = findViewById(R.id.newAlbumPicture);
        newAlbumPicture.setActivated(false);


        // Button for image selection
        ImageButton button = findViewById(R.id.photoPickerButton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PHOTO_PICKER);
            }
        });


        // Button to send data about the new album
        Button createNewAlbumButton = findViewById(R.id.createNewAlbumButton);
        createNewAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputCheckOk()) {
                    newAlbumUpload();
                    finish();
                } else
                    missingElementMessage();
            }
        });

    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // used to capture the picture-to-be-uploaded selection activity result
        if (requestCode == PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            newAlbumPicture.setImageURI(selectedImageUri);
            newAlbumPicture.setActivated(true);

        }


    }


    /**
     * Checks if one text field in the form contains user input
     * @param etText
     * @return true if there is some content
     */
    private boolean isNotEmpty(EditText etText) {
        return etText.getText().toString().trim().length()> 0;
    }

    /**
     * Checks if the form is completely and correctly filled by the user
     * @return
     */
    private boolean inputCheckOk() {

        return (isNotEmpty(newAlbumNameInput) && isNotEmpty(newAlbumReleaseDateInput) && isNotEmpty(newAlbumArtistInput)
                && isNotEmpty(newAlbumGenreInput1) && newAlbumPicture.isActivated());
    }

    /**
     * Procedure to store the new album in the real-time database and to upload the album cover in the storage
     */
    private void newAlbumUpload() {

        // Temp variables to parse user inputs and store query results
        String tempAlbumName = newAlbumNameInput.getText().toString();
        String parsedAlbumName = tempAlbumName.substring(0, 1).toUpperCase() + tempAlbumName.substring(1);
        String tempArtistName = newAlbumArtistInput.getText().toString();
        String parsedArtistName= tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1);
        final ArrayList<String> tempAlbums= new ArrayList<>();
        final ArrayList<String> tempArtists= new ArrayList<>();
        Album album;



        // Check if album is already present in the db
        Query existingAlbum = dbReference.orderByChild("title").equalTo(parsedAlbumName);
        existingAlbum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                        tempAlbums.add(next.getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // Check if artist is already present in the db
        tempArtists.add("artist");

        if (tempAlbums.isEmpty()) {

            if (!tempArtists.isEmpty()) {

                // Create object Album according to the number of genres specified
                if (isNotEmpty(newAlbumGenreInput2))
                    if(isNotEmpty(newAlbumGenreInput3))
                        album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                            0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(),
                                newAlbumGenreInput2.getText().toString(), newAlbumGenreInput3.getText().toString(), selectedImageUri.getLastPathSegment());
                    else
                        album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                                0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(),
                                newAlbumGenreInput2.getText().toString(), selectedImageUri.getLastPathSegment());

                 else
                    album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                            0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(), selectedImageUri.getLastPathSegment());

                // Store new album in the real-time db
                dbReference.child(newAlbumNameInput.getText().toString()).setValue(album);

                StorageReference tempStorage = storageReference.child(selectedImageUri.getLastPathSegment());
                tempStorage.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }});
            } else{
                Snackbar.make(this.getCurrentFocus(),R.string.album_noartist_error, Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(this.getCurrentFocus(),R.string.album_existing_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void missingElementMessage(){

        if (!isNotEmpty(newAlbumNameInput)){
            Snackbar.make(this.getCurrentFocus(),R.string.album_name_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumReleaseDateInput)){
            Snackbar.make(this.getCurrentFocus(),R.string.album_date_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumArtistInput)){
            Snackbar.make(this.getCurrentFocus(),R.string.album_artist_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumGenreInput1)){
            Snackbar.make(this.getCurrentFocus(),R.string.genre_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!newAlbumPicture.isActivated())
            Snackbar.make(this.getCurrentFocus(),R.string.album_picture_error, Snackbar.LENGTH_LONG).show();
    }


}
