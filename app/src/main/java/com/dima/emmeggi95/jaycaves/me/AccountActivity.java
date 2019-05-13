package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static com.dima.emmeggi95.jaycaves.me.MainActivity.PHOTO_PICKER;

public class AccountActivity extends AppCompatActivity {

    private ImageView cover;
    private EditText usernameText;
    private Button submit;
    private Button logout;
    private Uri selectedImageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private File localFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Set App Bar
        Toolbar toolbar = findViewById(R.id.toolbar_account);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.title_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cover= findViewById(R.id.account_coverphoto);
        if(User.cover_image!=null)
            cover.setImageBitmap(User.cover_image);

        usernameText = findViewById(R.id.account_username_input);
        if(User.getUsername()!=null)
            usernameText.setText(User.getEmail());// default value

        submit= findViewById(R.id.account_submit_button);
        logout= findViewById(R.id.logout_button);

        databaseReference= FirebaseDatabase.getInstance().getReference("preferences").child(User.getEmail());
        storageReference= FirebaseStorage.getInstance().getReference().child("User_covers");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();

            }
        });
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PHOTO_PICKER);

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
    public void onActivityResult(final int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // used to capture the picture-to-be-uploaded selection activity result
        if (requestCode == PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            cover.setImageURI(selectedImageUri);


        }


    }
    private void submit(){
        final String coverId= selectedImageUri.getLastPathSegment()+CustomRandomId.randomIdGenerator();
        storageReference.child(coverId).putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(getCurrentFocus(),"UPLOAD SUCCESSFULL", Snackbar.LENGTH_LONG).show();
                        UserPreference newPref= new UserPreference(usernameText.getText().toString(), coverId);
                        databaseReference.setValue(newPref);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getCurrentFocus(),"UPLOAD FAILED, TRY AGAIN",Snackbar.LENGTH_LONG).show();

                    }
                });
    }


}
