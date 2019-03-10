package com.dima.emmeggi95.jaycaves.me;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 *
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
        return inflater.inflate(R.layout.fragment_new_album, container, false);
    }



}
