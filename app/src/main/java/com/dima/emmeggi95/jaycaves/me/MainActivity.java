package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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


    protected FirebaseAuth authentication;
    protected static FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private DatabaseReference rtb;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public static final String ANONYMOUS = "anonymous";
    public static final int SIGN_IN = 123;
    public static final int PHOTO_PICKER = 2;
    private String username;
    private String email;

    //Drawer
    private int clickedNavItem = 0;
    private boolean selectionChanged = false;

    //Custom fonts
    private Typeface floydFont;
    private Typeface nasaFont;

    // Fragments
    LoadingFragment loadingFragment;
    HomeFragment homeFragment;
    ChartsFragment chartsFragment;
    FreshFragment freshFragment;
    PlaylistsFragment playlistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        rtb = database.getReference().child("admins");
        storageReference = storage.getReference();

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
                            SIGN_IN);
                }
            }
        };

        setContentView(R.layout.activity_main);

        // Fragments init
        loadingFragment = new LoadingFragment();
        homeFragment = new HomeFragment();
        chartsFragment = new ChartsFragment();
        freshFragment = new FreshFragment();
        playlistsFragment = new PlaylistsFragment();

        // FOR NOW USER INIT IS DONE HERE
        User.initLikes();
        User.initReviews();
        User.initPlaylists();


        // Initialize custom fonts
        floydFont = Typeface.createFromAsset(getAssets(), "fonts/floyd.TTF");
        nasaFont = Typeface.createFromAsset(getAssets(), "fonts/nasalization-rg.ttf");

        // Set App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddContentActivity.class);
                startActivity(intent);
            }
        });

        // Set Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                /**
                 * Change fragments when the Drawer is closed to avoid lag
                 */
                if (selectionChanged) {
                    selectionChanged = false;
                    switch (clickedNavItem) {
                        case R.id.nav_home:
                            setFragment(homeFragment);
                            break;
                        case R.id.nav_charts:
                            setFragment(chartsFragment);
                            break;
                        case R.id.nav_fresh:
                            setFragment(freshFragment);
                            break;
                        case R.id.nav_playlists:
                            setFragment(playlistsFragment);
                            break;
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }

        });

        // Set Navigation Drawer listener and select Home as starting fragment
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        clickedNavItem = R.id.nav_home;
        setFragment(homeFragment);//init
        setToolbarTitle(R.string.app_name, 2);
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

    /**
     * Firebase methods
     */

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

    /**
     * Navigation Drawer methods
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (clickedNavItem != id) {
            if (id != R.id.nav_account && id != R.id.nav_settings) {
                clickedNavItem = id;
                selectionChanged = true;
            }

            // Set a blank fragment before loading the actual fragment to avoid lags
            if (id == R.id.nav_home) {
                setLoadingFragment();
                setToolbarTitle(R.string.app_name, 2);
            } else if (id == R.id.nav_charts) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_charts, 0);
            } else if (id == R.id.nav_fresh) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_fresh, 0);
            } else if (id == R.id.nav_playlists) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_playlists, 0);
            } else if (id == R.id.nav_account) {
                goToActivity(AccountActivity.class);
            } else if (id == R.id.nav_settings) {
                goToActivity(SettingsActivity.class);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void goToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void setFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }
    }

    public void setLoadingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, loadingFragment);
        ft.commit();
    }

    /**
     * App Bar methods
     *
     * @param menu
     * @return
     */

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
            Intent intent = new Intent(this, SearchableActivity.class);
            //ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    /**
     * Set the toolbar title
     *
     * @param title: the id of the string you want to put in the title
     * @param font:  these are the available fonts ->
     *               0. Roboto
     *               1. Floyd
     *               2. Nasa
     */
    private void setToolbarTitle(int title, int font) {
        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText(getResources().getString(title));
        switch (font) {
            case 2:
                titleTextView.setTypeface(nasaFont);
                break;
            case 1:
                titleTextView.setTypeface(floydFont);
                break;
            case 0:
            default:
                titleTextView.setTypeface(null);
        }
    }

}
