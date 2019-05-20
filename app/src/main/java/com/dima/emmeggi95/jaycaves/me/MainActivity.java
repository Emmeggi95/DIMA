package com.dima.emmeggi95.jaycaves.me;

import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private ImageView custom_image;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    //Drawer
    private List<Integer> navigationHistory;
    private String NAV_HISTORY = "navHistory";
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

    // Fab
    private boolean fabIsShown;

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


        // Set Navigation Drawer listener and select Home as starting fragment
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        custom_image= navigationView.getHeaderView(0).findViewById(R.id.nav_header_image);


        // Fragments init
        loadingFragment = new LoadingFragment();
        homeFragment = new HomeFragment();
        chartsFragment = new ChartsFragment();
        freshFragment = new FreshFragment();
        playlistsFragment = new PlaylistsFragment();

        // FOR NOW USER INIT IS DONE HERE
        User.initPreferences(custom_image);
        User.initLikes();
        User.initReviews();
        User.initPlaylists();


        // Initialize custom fonts
        floydFont = Typeface.createFromAsset(getAssets(), "fonts/floyd.TTF");
        nasaFont = Typeface.createFromAsset(getAssets(), "fonts/nasalization-rg.ttf");

        // Set App Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation bump = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bump);
                fab.startAnimation(bump);
                Intent intent = new Intent(view.getContext(), AddContentActivity.class);
                startActivity(intent);
            }
        });
        fabIsShown = true;

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



        // Retrieve navigation history from Instance state or init navigation history
        if (savedInstanceState != null) {
            navigationHistory = savedInstanceState.getIntegerArrayList(NAV_HISTORY);
            navigateToId(navigationHistory.get(navigationHistory.size()-1));
        } else {
            navigationHistory = new ArrayList<>();
            navigationHistory.add(R.id.nav_home);
            navigateToId(R.id.nav_home);
        }



    }

    // OVERRIDE ON ACTIVITY METHODS

    @Override
    protected void onPause() {
        super.onPause();
        //authentication.removeAuthStateListener(authStateListener);
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //authentication.addAuthStateListener(authStateListener);
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

    @Override
    protected void onStop(){
        super.onStop();
        /*if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        } */

    }

    @Override
    protected void onRestart(){
        super.onRestart();

        // Register the broadcast receiver with the intent filter object.
      //  registerReceiver(networkChangeReceiver, intentFilter);

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
     * Navigation methods
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(NAV_HISTORY, (ArrayList<Integer>) navigationHistory);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navigationHistory.size() > 1) {
            navigationHistory.remove(navigationHistory.size()-1);
            navigateToId(navigationHistory.get(navigationHistory.size()-1));
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Navigation Drawer methods
     */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (clickedNavItem != id) {

            if (id != R.id.nav_account && id != R.id.nav_settings) {
                clickedNavItem = id;
                selectionChanged = true;
                navigationHistory.add(id);
            }

            // Set a blank fragment before loading the actual fragment to avoid lags
            if (id == R.id.nav_home) {
                setLoadingFragment();
                setToolbarTitle(R.string.app_name, 2);
                showFab();
            } else if (id == R.id.nav_charts) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_charts, 0);
                hideFab();
            } else if (id == R.id.nav_fresh) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_fresh, 0);
                hideFab();
            } else if (id == R.id.nav_playlists) {
                setLoadingFragment();
                setToolbarTitle(R.string.title_playlists, 0);
                hideFab();
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

    private void navigateToId(int id) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(id);
        switch (id) {
            case R.id.nav_home:
                setFragment(homeFragment);
                setToolbarTitle(R.string.app_name, 2);
                showFab();
                break;
            case R.id.nav_charts:
                setFragment(chartsFragment);
                setToolbarTitle(R.string.title_charts, 0);
                hideFab();
                break;
            case R.id.nav_fresh:
                setFragment(freshFragment);
                setToolbarTitle(R.string.title_fresh, 0);
                hideFab();
                break;
            case R.id.nav_playlists:
                setFragment(playlistsFragment);
                setToolbarTitle(R.string.title_playlists, 0);
                hideFab();
                break;
        }
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

    private void hideFab() {
        if (fabIsShown) {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.animate().scaleX(0).setDuration(300).setStartDelay(300);
            fab.animate().scaleY(0).setDuration(300).setStartDelay(300);
        }
        fabIsShown = false;
    }

    private void showFab() {
        if(!fabIsShown){
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.animate().scaleX(1).setDuration(300).setStartDelay(300);
            fab.animate().scaleY(1).setDuration(300).setStartDelay(300);
        }
        fabIsShown = true;
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
