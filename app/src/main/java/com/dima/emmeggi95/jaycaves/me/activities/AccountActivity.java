package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.CustomRandomId;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.User;
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

import static com.dima.emmeggi95.jaycaves.me.activities.MainActivity.PHOTO_PICKER;

/**
 * Space where user can change profile pic and username
 */
public class AccountActivity extends AppCompatActivity {

    private ImageView cover;
    private ImageView upload;
    private EditText usernameInput;
    private TextView usernameText;
    private ImageView editSubmitIcon;
    private TextView editSubmitText;
    private LinearLayout editSubmit;
    private Button logout;
    private LinearLayout page;
    private Uri selectedImageUri;
    private DatabaseReference prefReference;
    private DatabaseReference reviewReference;
    private DatabaseReference notificationReference;
    private StorageReference storageReference;
    private File localFile;
    private boolean usernameTaken = false;
    private boolean uncheckedUsername = false;
    private ConstraintLayout content;

    private boolean editMode;

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
        cover.setEnabled(false);
        upload = findViewById(R.id.account_edit_image);
        content = findViewById(R.id.account_content);
        if(User.cover_image!=null)
            cover.setImageBitmap(User.cover_image);

        usernameInput = findViewById(R.id.account_username_input);
        usernameText = findViewById(R.id.username_text);
        if(User.username!=null) {
            usernameInput.setText(User.username);// default value
            usernameText.setText(User.username);
        }
        uncheckedUsername = false;
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

               usernameTaken = false;

                // Parse user input text
                String tempUsername = s.toString();
                if (s.toString().length()>0)
                    if (tempUsername.endsWith(" "))
                        tempUsername= tempUsername.substring(0, tempUsername.length()-1);

                    tempUsername.replaceAll("[^a-zA-Z0-9]", "");


                FirebaseDatabase.getInstance().getReference("preferences").orderByChild("username").equalTo(tempUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data= dataSnapshot.getChildren();
                        for(DataSnapshot d: data){
                            if (!d.getValue(AccountPreference.class).getUsername().equalsIgnoreCase(User.username))
                                usernameTaken = true;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        uncheckedUsername = false;
                    }
                });

            }

        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // avoid keyboard opening at activity start

        editSubmit = findViewById(R.id.edit_submit_container);
        editSubmitIcon = findViewById(R.id.edit_submit_icon);
        editSubmitText = findViewById(R.id.edit_submit_text);
        logout= findViewById(R.id.logout_button);
        page= findViewById(R.id.go_to_user_page_container);

        prefReference = FirebaseDatabase.getInstance().getReference("preferences").child(User.uid);
        reviewReference= FirebaseDatabase.getInstance().getReference("reviews");
        notificationReference = FirebaseDatabase.getInstance().getReference("notifications");
        storageReference= FirebaseStorage.getInstance().getReference().child("User_covers");


        editSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editMode) {
                    editMode = true;
                    editSubmitIcon.setImageResource(R.drawable.ic_check_black_24dp);
                    editSubmitText.setText(getString(R.string.submit_profile));
                    usernameInput.setVisibility(View.VISIBLE);
                    usernameText.setVisibility(View.GONE);
                    upload.setVisibility(View.VISIBLE);
                    cover.setImageAlpha(10);
                    cover.setEnabled(true);

                } else {
                    editMode = false;
                    editSubmitIcon.setImageResource(R.drawable.ic_baseline_create_24px);
                    editSubmitText.setText(getString(R.string.edit_profile));
                    usernameInput.setVisibility(View.GONE);
                    usernameText.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.GONE);
                    cover.setImageAlpha(255);
                    submit(v);
                    cover.setEnabled(false);

                }
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
                startActivity(intent);
            }
        });

        editMode = false;
    }


    /**
     * Handles choice of picture to be uploaded
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

    /**
     * Uploads new pictures to storage
     * @param v
     */
    private void submit(final View v) {

        if (selectedImageUri != null) {
            final String coverId = selectedImageUri.getLastPathSegment() + CustomRandomId.randomIdGenerator();
            storageReference.child(coverId).putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            usernameCheckNChange(v, coverId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(v, R.string.upload_failed, Snackbar.LENGTH_LONG).show();

                        }
                    });
        }
        else
            usernameCheckNChange(v, null);
    }


    /**
     * Checks if username in valid and then saves new preferences on realtime db
     * @param v
     * @param coverId
     */
    private void usernameCheckNChange(final View v, String coverId){
        if(uncheckedUsername){
            Snackbar.make(v, R.string.unchecked_username, Snackbar.LENGTH_LONG).show();
        }
        else if(usernameTaken){
            Snackbar.make(v, R.string.username_taken, Snackbar.LENGTH_LONG).show();
        }
        else {
            String parsedInput = usernameInput.getText().toString();

            if (parsedInput.endsWith(" "))
                parsedInput= parsedInput.substring(0, parsedInput.length()-1);

            parsedInput = parsedInput.replaceAll("[^a-zA-Z0-9]", ""); // eliminate special chars
            final String finalIput = parsedInput;
            final String oldUsername = User.username;
            System.out.println(oldUsername);
            AccountPreference newPref = new AccountPreference(parsedInput, (coverId != null) ? coverId : User.cover_photo_id);
            prefReference.setValue(newPref).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Snackbar.make(v, R.string.changed_preferences, Snackbar.LENGTH_LONG).show();
                    updateServerWithNewUsername(oldUsername);
                    User.setUsername(finalIput);
                    usernameText.setText(finalIput);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(v, R.string.failed_username_upload, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Corrects all reviews done by user with their most recent username
     */
    private void updateServerWithNewUsername(String oldUsername){
        // Review Section
        reviewReference.orderByChild("userEmail").equalTo(User.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d: data){
                    reviewReference.child(d.getKey()).child("author").setValue(User.username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // FUTURE NOTE
        notificationReference.orderByChild("liker").equalTo(oldUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot d: data){
                    System.out.println(d.getKey());
                    //notificationReference.child(d.getKey()).child("liker").setValue(User.username);
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
