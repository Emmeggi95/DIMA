package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private Button page;
    private Uri selectedImageUri;
    private DatabaseReference prefReference;
    private DatabaseReference reviewReference;
    private StorageReference storageReference;
    private File localFile;
    private boolean usernameTaken = false;
    private boolean checkedUsername;

    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;



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
        if(User.username!=null)
            usernameText.setText(User.username);// default value
        usernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

               usernameTaken = false;
                checkedUsername = true;
                // Parse user input text
                String tempUsername = s.toString();
                if (s.toString().length()>0)
                    if (tempUsername.endsWith(" "))
                        tempUsername= tempUsername.substring(0, tempUsername.length()-1);


                FirebaseDatabase.getInstance().getReference("preferences").orderByChild("username").equalTo(tempUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                        for(DataSnapshot d: data){
                            usernameTaken = true;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        checkedUsername = false;
                    }
                });

            }

        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // avoid keyboard opening at activity start

        submit= findViewById(R.id.account_submit_button);
        logout= findViewById(R.id.logout_button);
        page= findViewById(R.id.account_peronalpage_button);

        prefReference = FirebaseDatabase.getInstance().getReference("preferences").child(User.uid);
        reviewReference= FirebaseDatabase.getInstance().getReference("reviews");
        storageReference= FirebaseStorage.getInstance().getReference().child("User_covers");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputOk())
                    submit();
                else if (usernameTaken)
                    Snackbar.make(getCurrentFocus(), R.string.username_taken, Snackbar.LENGTH_LONG).show();
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

        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("username", User.username);
                intent.putExtra("email", User.email );
               // intent.putExtra("reviews", (Serializable) User.reviews);
                startActivity(intent);
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


    private boolean isInputOk(){
        return checkedUsername && !usernameTaken;
    }



    private void submit() {

        if (selectedImageUri != null) {
            final String coverId = selectedImageUri.getLastPathSegment() + CustomRandomId.randomIdGenerator();
            storageReference.child(coverId).putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Snackbar.make(getCurrentFocus(), R.string.changed_username_image, Snackbar.LENGTH_LONG).show();
                            AccountPreference newPref = new AccountPreference(usernameText.getText().toString(), coverId);
                            prefReference.setValue(newPref).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    User.setUsername(usernameText.getText().toString());
                                    updateReviewsWithNewUsername();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(getCurrentFocus(), R.string.username_taken, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(getCurrentFocus(), R.string.upload_failed, Snackbar.LENGTH_LONG).show();

                        }
                    });
        }
        else
            if (!usernameText.getText().toString().equalsIgnoreCase(User.username)) {
                prefReference.child("username").setValue(usernameText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        User.setUsername(usernameText.getText().toString());
                        updateReviewsWithNewUsername();
                    }
                });
                Snackbar.make(getCurrentFocus(), R.string.changed_username, Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(getCurrentFocus(), R.string.nothing_changed, Snackbar.LENGTH_LONG).show();
    }


    private void updateReviewsWithNewUsername(){
        reviewReference.orderByChild("userEmail").equalTo(User.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d: data){
                    System.out.println("REVIEW: " + d.getKey());
                    reviewReference.child(d.getKey()).child("author").setValue(User.username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an IntentFilter instance.
        intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        networkChangeReceiver = new NetworkChangeReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(networkChangeReceiver, intentFilter);

    }
}
