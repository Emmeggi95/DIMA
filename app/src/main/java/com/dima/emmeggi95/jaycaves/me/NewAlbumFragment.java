package com.dima.emmeggi95.jaycaves.me;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import static android.app.Activity.RESULT_OK;
import static com.dima.emmeggi95.jaycaves.me.MainActivity.PHOTO_PICKER;


/**
 * This fragment is where the user can insert into the catalogue new albums
 */
public class NewAlbumFragment extends Fragment {

    // Backend references
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private ArrayList<String> genreList;
    private ArrayList<String> tempArtist;
    private boolean checkedArtist;

    // Layout elements
    private ImageView newAlbumPicture;
    private EditText newAlbumNameInput;
    private EditText newAlbumReleaseDateInput;
    private EditText newAlbumArtistInput;
    private AutoCompleteTextView newAlbumGenreInput1;
    private AutoCompleteTextView newAlbumGenreInput2;
    private AutoCompleteTextView newAlbumGenreInput3;

    public NewAlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);
        // Storage setup
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("Album_covers");

        // Database setup
        database= FirebaseDatabase.getInstance();
        dbReference= database.getReference("albums");

        // Activity View Setup
        newAlbumNameInput = view.findViewById(R.id.newAlbumNameInput);
        newAlbumReleaseDateInput = view.findViewById(R.id.newAlbumReleaseDateInput);
        newAlbumArtistInput = view.findViewById(R.id.newAlbumArtistInput);
        newAlbumGenreInput1 = view.findViewById(R.id.newAlbumGenreInput1);
        newAlbumGenreInput2 = view.findViewById(R.id.newAlbumGenreInput2);
        newAlbumGenreInput3 = view.findViewById(R.id.newAlbumGenreInput3);
        newAlbumPicture = view.findViewById(R.id.newAlbumPicture);
        newAlbumPicture.setActivated(false);

        // Fetch all genres for autocompletion
        genreList= new ArrayList<>();
        database.getReference("genres").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                    for (DataSnapshot d : data)
                        genreList.add(d.getKey());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, genreList);
                    newAlbumGenreInput1.setAdapter(adapter);
                    newAlbumGenreInput2.setAdapter(adapter);
                    newAlbumGenreInput3.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getActivity().getCurrentFocus(), R.string.internal_error, Snackbar.LENGTH_LONG).show();
            }
        });



        // Text Listener for Artist input check
        tempArtist= new ArrayList<>();
        checkedArtist=false;
        newAlbumArtistInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                // Parse user input text
                String tempArtistName = s.toString();
                String parsedArtistName = "";
                if (s.toString().length()>0)
                    if (tempArtistName.endsWith(" "))
                        parsedArtistName= tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1, tempArtistName.length()-1);
                    else
                        parsedArtistName= tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1);

                // Check existence of typed artist in real time db
                database.getReference("artists").orderByKey().equalTo(parsedArtistName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                tempArtist.clear(); // flush previous temporary values
                                Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                                for (DataSnapshot d: data) {
                                    tempArtist.add(d.getValue(Artist.class).getName());

                                }
                                checkedArtist=true;

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Snackbar.make(getActivity().getCurrentFocus(),R.string.album_unknown_error, Snackbar.LENGTH_LONG).show();
                            }
                        });

            }

        });

        // Button for image selection
        ImageButton button = view.findViewById(R.id.photoPickerButton);
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
        Button createNewAlbumButton = view.findViewById(R.id.createNewAlbumButton);
        createNewAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (inputCheckOk()) {
                    newAlbumUpload();
                }
                else
                    missingElementMessage();
            }
        });


        return view;
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
            newAlbumPicture.setImageURI(selectedImageUri);
            newAlbumPicture.setActivated(true);

        }


    }

    /**
     * Checks if the form is completely and correctly filled by the user
     * @return
     */
    private boolean inputCheckOk() {

        return (isNotEmpty(newAlbumNameInput) && isNotEmpty(newAlbumReleaseDateInput) && isNotEmpty(newAlbumArtistInput)
                && isNotEmpty(newAlbumGenreInput1) && newAlbumPicture.isActivated() && checkedArtist && !tempArtist.isEmpty()
                && isGenreValid(newAlbumGenreInput1) && isYearValid(newAlbumReleaseDateInput) && (!isNotEmpty(newAlbumGenreInput2) || isGenreValid(newAlbumGenreInput2))
                && ((!isNotEmpty(newAlbumGenreInput3) || isGenreValid(newAlbumGenreInput3))));
    }

    /**
     * Checks if one text field in the form contains user input
     * @param etText
     * @return true if there is some content
     */
    private boolean isNotEmpty(EditText etText) {
        return (etText.getText().toString().trim().length()> 0);
    }

    private boolean isGenreValid(AutoCompleteTextView textView){
        boolean found= false;
        for (String el: genreList){
            if (el.toLowerCase().equals(textView.getText().toString().toLowerCase()))
                found=true;
        }

        return found;
    }

    private boolean isYearValid(EditText editText){

        int selectedYear= Integer.parseInt(editText.getText().toString());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        return ((selectedYear<=currentYear) && (selectedYear>0));
    }


    /**
     * Procedure to store the new album in the real-time database and to upload the album cover in the storage
     */
    private void newAlbumUpload() {

        // Temp variables to parse user inputs
        String tempAlbumName = newAlbumNameInput.getText().toString();
        String parsedAlbumName;
        if(tempAlbumName.endsWith(" ")) //remove last space input if present
            parsedAlbumName= tempAlbumName.substring(0, 1).toUpperCase() + tempAlbumName.substring(1,tempAlbumName.length()-1);
        else
            parsedAlbumName = tempAlbumName.substring(0, 1).toUpperCase() + tempAlbumName.substring(1);
        final String finalAlbumName= parsedAlbumName;
        String tempArtistName = newAlbumArtistInput.getText().toString();
        String parsedArtistName;
        if(tempArtistName.endsWith(" ")) //remove last space input if present
            parsedArtistName= tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1,tempArtistName.length()-1);
        else
            parsedArtistName = tempArtistName.substring(0, 1).toUpperCase() + tempArtistName.substring(1);
        String tempGenre1= newAlbumGenreInput1.getText().toString();
        String parsedGenre1= tempGenre1.substring(0,1).toUpperCase()+ tempGenre1.substring(1);
        String tempGenre2= newAlbumGenreInput1.getText().toString();
        String parsedGenre2= tempGenre2.substring(0,1).toUpperCase()+ tempGenre2.substring(1);
        String tempGenre3= newAlbumGenreInput1.getText().toString();
        String parsedGenre3= tempGenre3.substring(0,1).toUpperCase()+ tempGenre3.substring(1);
        final String parsedCoverName= selectedImageUri.getLastPathSegment()+CustomRandomId.randomIdGenerator();


        // Create object Album according to the number of genres specified
        Album album;
        if (isNotEmpty(newAlbumGenreInput2))
            if(isNotEmpty(newAlbumGenreInput3))
                album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                        parsedArtistName, parsedGenre1, parsedGenre2, parsedGenre3, parsedCoverName);
            else
                album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                        parsedArtistName, parsedGenre1, parsedGenre2, parsedCoverName);

        else
            album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                        parsedArtistName, parsedGenre1, parsedCoverName);



        // Try to perform the addition to database
        dbReference.child(parsedAlbumName+"@"+parsedArtistName).setValue(album)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Add the cover in the storage
                StorageReference tempStorage = storageReference.child(parsedCoverName);
                tempStorage.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Snackbar.make(getActivity().getCurrentFocus(),R.string.album_success, Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(getActivity().getCurrentFocus(),R.string.album_cover_error,Snackbar.LENGTH_LONG).show();
                                dbReference.child(finalAlbumName).removeValue();

                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String code= e.getMessage();
                if(code.contains("Permission denied"))
                    Snackbar.make(getActivity().getCurrentFocus(),R.string.album_existing_error,Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(getActivity().getCurrentFocus(),R.string.album_unknown_error,Snackbar.LENGTH_LONG).show();
            }

        });


    }

    /**
     *
     */
    private void missingElementMessage(){

        if (!isNotEmpty(newAlbumNameInput)){
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_name_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumReleaseDateInput)){
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_date_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumArtistInput)){
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_artist_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!isNotEmpty(newAlbumGenreInput1)){
            Snackbar.make(getActivity().getCurrentFocus(),R.string.genre_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!newAlbumPicture.isActivated()){
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_picture_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!checkedArtist){
            Snackbar.make(getActivity().getCurrentFocus(), R.string.album_unknown_error, Snackbar.LENGTH_LONG).show();
        }
        else if (tempArtist.isEmpty()) {
            Snackbar.make(getActivity().getCurrentFocus(), R.string.album_noartist_error, Snackbar.LENGTH_LONG).show();
            //((AddContentActivity) getActivity()).getViewPager().setCurrentItem(1);
        }
        else if(!isGenreValid(newAlbumGenreInput1)) {
            Snackbar.make(getActivity().getCurrentFocus(), R.string.genre1_error, Snackbar.LENGTH_LONG).show();
        }
        else if(!isGenreValid(newAlbumGenreInput2)) {
            Snackbar.make(getActivity().getCurrentFocus(), R.string.genre2_error, Snackbar.LENGTH_LONG).show();
        }
        else if(!isGenreValid(newAlbumGenreInput3)) {
            Snackbar.make(getActivity().getCurrentFocus(), R.string.genre3_error, Snackbar.LENGTH_LONG).show();
        }
        else if(!isYearValid(newAlbumReleaseDateInput)){
            Snackbar.make(getActivity().getCurrentFocus(), "Please insert a valid date!", Snackbar.LENGTH_LONG).show();
        }
    }







}

