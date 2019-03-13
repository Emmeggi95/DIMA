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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.security.SecureRandom;
import java.util.Locale;


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

    // Layout elements
    private ImageView newAlbumPicture;
    private EditText newAlbumNameInput;
    private EditText newAlbumReleaseDateInput;
    private EditText newAlbumArtistInput;
    private EditText newAlbumGenreInput1;
    private EditText newAlbumGenreInput2;
    private EditText newAlbumGenreInput3;

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
        final String parsedCoverName= selectedImageUri.getLastPathSegment()+randomIdGenerator();



        // Create object Album according to the number of genres specified
        Album album;
        if (isNotEmpty(newAlbumGenreInput2))
            if(isNotEmpty(newAlbumGenreInput3))
                album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                        0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(),
                        newAlbumGenreInput2.getText().toString(), newAlbumGenreInput3.getText().toString(), parsedCoverName);
            else
                album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                        0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(),
                        newAlbumGenreInput2.getText().toString(), parsedCoverName);

        else
            album = new Album(parsedAlbumName, newAlbumReleaseDateInput.getText().toString(),
                    0.0, parsedArtistName, newAlbumGenreInput1.getText().toString(), parsedCoverName);



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
                    Snackbar.make(getActivity().getCurrentFocus(),R.string.album_existing_error,Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(getActivity().getCurrentFocus(),R.string.album_unknown_error,Snackbar.LENGTH_LONG).show();
            }

        });


    }

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
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_genre_error, Snackbar.LENGTH_LONG).show();
        }
        else if (!newAlbumPicture.isActivated())
            Snackbar.make(getActivity().getCurrentFocus(),R.string.album_picture_error, Snackbar.LENGTH_LONG).show();
    }


    /**
     * Generates a @buf digits random alphanumerical string
     * @return random string
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

/*

if (existsArtist(newAlbumArtistInput.getText().toString()))
else
                        Snackbar.make(getActivity().getCurrentFocus(),R.string.album_noartist_error,Snackbar.LENGTH_LONG).show();

  private boolean existsArtist(String artist) {

        tempArtist.setName("default");
        Query result = database.getReference("artists").orderByKey().equalTo(artist).limitToFirst(1);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> list = dataSnapshot.getChildren();

                for (DataSnapshot el: list) {
                    Artist fetched = el.getValue(Artist.class);
                    tempArtist.setName(fetched.getName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getActivity().getCurrentFocus(), "ERROR: could not check Artist validity", Snackbar.LENGTH_LONG).show();
            }
        };
        result.addValueEventListener(listener);
        System.out.println(tempArtist);
        return (tempArtist.getName().equals(artist));

    }

 */


}
