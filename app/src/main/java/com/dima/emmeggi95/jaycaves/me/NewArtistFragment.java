package com.dima.emmeggi95.jaycaves.me;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static android.app.Activity.RESULT_OK;
import static com.dima.emmeggi95.jaycaves.me.MainActivity.PHOTO_PICKER;


/**
 * This fragment is where the user can insert into the catalogue new artists
 */
public class NewArtistFragment extends Fragment {

    public NewArtistFragment() {
        // Required empty public constructor
    }
        // Backend references
        private FirebaseStorage storage;
        private StorageReference storageReference;
        private Uri selectedImageUri;
        private FirebaseDatabase database;
        private DatabaseReference dbReference;

        // Layout elements
        private ImageView newArtistPicture;
        private EditText newArtistNameInput;
        private EditText newArtistReleaseDateInput;
        private EditText newArtistStory;
        private EditText newArtistGenreInput1;
        private EditText newArtistGenreInput2;
        private EditText newArtistGenreInput3;



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_new_artist, container, false);
            // Storage setup
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("Artist_covers");

            // Database setup
            database= FirebaseDatabase.getInstance();
            dbReference= database.getReference("artists");


            // Activity View Setup
            newArtistNameInput = view.findViewById(R.id.newArtistNameInput);
            newArtistReleaseDateInput = view.findViewById(R.id.newArtistReleaseDateInput);
            newArtistStory = view.findViewById(R.id.newArtistStory);
            newArtistGenreInput1 = view.findViewById(R.id.newArtistGenreInput1);
            newArtistGenreInput2 = view.findViewById(R.id.newArtistGenreInput2);
            newArtistGenreInput3 = view.findViewById(R.id.newArtistGenreInput3);
            newArtistPicture = view.findViewById(R.id.newArtistPicture);
            newArtistPicture.setActivated(false);


            // Button for image selection
            ImageButton button = view.findViewById(R.id.photoPickerButton2);
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
            Button createNewAlbumButton = view.findViewById(R.id.createNewArtistButton);
            createNewAlbumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (inputCheckOk()) {
                        newArtistUpload();
                    } else
                        missingElementMessage();
                }
            });
            return view;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

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
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        @Override
        public void onActivityResult(final int requestCode, final int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            // used to capture the picture-to-be-uploaded selection activity result
            if (requestCode == PHOTO_PICKER && resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                newArtistPicture.setImageURI(selectedImageUri);
                newArtistPicture.setActivated(true);

            }


        }

        /**
         * Checks if the form is completely and correctly filled by the user
         * @return
         */
        private boolean inputCheckOk() {

            return (isNotEmpty(newArtistNameInput) && isNotEmpty(newArtistReleaseDateInput) && isNotEmpty(newArtistStory)
                    && isNotEmpty(newArtistGenreInput1) && newArtistPicture.isActivated());
        }

        /**
         * Procedure to store the new album in the real-time database and to upload the album cover in the storage
         */
        private void newArtistUpload() {

            // Temp variables to parse user inputs and store query results
            String tempArtistName = newArtistNameInput.getText().toString();
            String parsedArtistName= tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1);
            final String parsedCoverName= selectedImageUri.getLastPathSegment()+randomIdGenerator();



            // Create object Album according to the number of genres specified
            Artist artist;
            if (isNotEmpty(newArtistGenreInput2))
                if(isNotEmpty(newArtistGenreInput3))
                    artist = new Artist(parsedArtistName, newArtistReleaseDateInput.getText().toString(), newArtistStory.getText().toString(), newArtistGenreInput1.getText().toString(),
                            newArtistGenreInput2.getText().toString(), newArtistGenreInput3.getText().toString(), parsedCoverName);
                else
                    artist = new Artist(parsedArtistName, newArtistReleaseDateInput.getText().toString(),
                            newArtistStory.getText().toString(), newArtistGenreInput1.getText().toString(),
                            newArtistGenreInput2.getText().toString(), parsedCoverName);

            else
                artist = new Artist(parsedArtistName, newArtistReleaseDateInput.getText().toString(),
                       newArtistStory.getText().toString() , newArtistGenreInput1.getText().toString(), parsedCoverName);



            // Try to perform the addition to database
            dbReference.child(parsedArtistName).setValue(artist)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Add the cover in the storage
                            StorageReference tempStorage = storageReference.child(parsedCoverName);
                            tempStorage.putFile(selectedImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_success, Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_cover_error,Snackbar.LENGTH_LONG).show();
                                            //dbReference.child(newAlbumNameInput.getText().toString()).removeValue();
                                            // HANDLE
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String code= e.getMessage();
                            if(code.contains("Permission denied"))
                                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_existing_error,Snackbar.LENGTH_LONG).show();
                            else
                                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_unknown_error,Snackbar.LENGTH_LONG).show();
                        }

                    });
        }


        private void missingElementMessage(){

            if (!isNotEmpty(newArtistNameInput)){
                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_name_error, Snackbar.LENGTH_LONG).show();
            }
            else if (!isNotEmpty(newArtistReleaseDateInput)){
                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_date_error, Snackbar.LENGTH_LONG).show();
            }
            else if (!isNotEmpty(newArtistStory)){
                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_story_error, Snackbar.LENGTH_LONG).show();
            }
            else if (!isNotEmpty(newArtistGenreInput1)){
                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_genre_error, Snackbar.LENGTH_LONG).show();
            }
            else if (!newArtistPicture.isActivated())
                Snackbar.make(getActivity().getCurrentFocus(),R.string.artist_picture_error, Snackbar.LENGTH_LONG).show();
        }


    /**
     * Generates a @buf digits random alphanumerical string
     * @return
     */
    private String randomIdGenerator(){

            String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lower = upper.toLowerCase(Locale.ROOT);
            String digits = "0123456789";
            String alphanum = upper + lower + digits;
            char[] symbols = alphanum.toCharArray();
            char[] buf = new char[10];

            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[new SecureRandom().nextInt(symbols.length)];

            return new String(buf);
        }


    }
