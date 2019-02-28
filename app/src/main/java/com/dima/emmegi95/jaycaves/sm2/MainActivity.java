package com.dima.emmegi95.jaycaves.sm2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private DatabaseReference rtb;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 123;
    public static final int RC_PHOTO_PICKER =  2;
    private String username;
    private String email;


    // Fragments
    HomeFragment homeFragment;
    ChartsFragment chartsFragment;
    ForumFragment forumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        rtb= database.getReference().child("admins");
        storageReference= storage.getReference();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user signed in
                    onSignInInitialize(user);

                } else {
                    //user signed out
                    onSignOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    //.setIsSmartLockEnabled(false)
                                    //.setLogo(R.drawable.my_great_logo)
                                    //.setTheme(R.style.MySuperAppTheme)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        setContentView(R.layout.activity_main);

        // Set App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        // Fragments init
        homeFragment = new HomeFragment();
        chartsFragment = new ChartsFragment();
        forumFragment = new ForumFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.home_fragment, homeFragment).commit();
        setTitle(R.string.app_name);
    }

    private void onSignOutCleanUp() {
        username = ANONYMOUS;
        //signout procedure, database detach
    }

    private void onSignInInitialize(FirebaseUser user) {
        username = user.getDisplayName();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // for now do nothing
                    }
                });
        //signin procedure, database attach
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent goToAccount = new Intent(this, AccountActivity.class);
            startActivity(goToAccount);
        } else if (id == R.id.nav_home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (chartsFragment.isVisible())
                fragmentManager.beginTransaction().remove(chartsFragment).commit();
            if (forumFragment.isVisible())
                fragmentManager.beginTransaction().remove(forumFragment).commit();
            if (!homeFragment.isVisible()) {
                fragmentManager.beginTransaction().add(R.id.home_fragment, homeFragment).commit();
                setTitle(R.string.app_name);
            }
        } else if (id == R.id.nav_charts) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (homeFragment.isVisible())
                fragmentManager.beginTransaction().remove(homeFragment).commit();
            if (forumFragment.isVisible())
                fragmentManager.beginTransaction().remove(forumFragment).commit();
            if (!chartsFragment.isVisible()) {
                fragmentManager.beginTransaction().add(R.id.charts_fragment, chartsFragment).commit();
                setTitle(R.string.title_charts);
            }
        } else if (id == R.id.nav_forum) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (chartsFragment.isVisible())
                fragmentManager.beginTransaction().remove(chartsFragment).commit();
            if (homeFragment.isVisible())
                fragmentManager.beginTransaction().remove(homeFragment).commit();
            if (!forumFragment.isVisible()) {
                fragmentManager.beginTransaction().add(R.id.forum_fragment, forumFragment).commit();
                setTitle(R.string.title_forum);
            }
        } else if (id == R.id.nav_settings) {
            Intent goToSettings = new Intent(this, SettingsActivity.class);
            startActivity(goToSettings);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (requestCode == RC_PHOTO_PICKER && requestCode == RESULT_OK ){
                
            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        authentication.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //authentication.addAuthStateListener(authStateListener);
    }

}
