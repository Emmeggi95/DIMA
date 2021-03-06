package com.dima.emmeggi95.jaycaves.me.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.db.AccountPreference;
import com.dima.emmeggi95.jaycaves.me.entities.CustomRandomId;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.User;
import com.dima.emmeggi95.jaycaves.me.fragments.ChartsFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.FreshFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.HomeFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.LoadingFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.PlaylistsFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase
    protected FirebaseAuth authentication;
    protected static FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private DatabaseReference adminReference;
    private DatabaseReference prefReference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public static final String ANONYMOUS = "anonymous";
    public static final int SIGN_IN = 123;
    public static final int PHOTO_PICKER = 2;
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

        // Login
        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        adminReference = database.getReference("admins");
        prefReference= database.getReference("preferences");
        setContentView(R.layout.activity_main);

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
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.mipmap.music_explorer_round)
                                    //.setTheme(R.style.MySuperAppTheme)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                                    .setTheme(R.style.AuthTheme)
                                    .build(),
                            SIGN_IN);
                }
            }
        };

        authentication.addAuthStateListener(authStateListener);


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
                Intent intent = new Intent(MainActivity.this, AddContentActivity.class);
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

        // If you click on a notification open the correct page
        if (getIntent().getStringExtra("type") != null){
            Intent new_intent = new Intent(this, SocialActivity.class);
            new_intent.putExtra("type", getIntent().getStringExtra("type"));
            startActivity(new_intent);
        }



    }

    // OVERRIDE ON ACTIVITY METHODS

    @Override
    protected void onPause() {
        super.onPause();
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        // Open the correct page if you click on a notification while the app is open
        setIntent(intent);
        if (getIntent().getStringExtra("type") != null){
            Intent new_intent = new Intent(this, SocialActivity.class);
            new_intent.putExtra("type", getIntent().getStringExtra("type"));
            startActivity(new_intent);
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


    @Override
    protected void onStart() {
        super.onStart();
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Explorer Channel";
            String description = "Music Explorer channel for messages and notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Notify the correct login
        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }

        }
    }


    // FIREBASE AUTH METHODS

    private void onSignOutCleanUp() {
        User.username = ANONYMOUS;
        User.uid = ANONYMOUS;
        User.email = ANONYMOUS;
        User.reviews.clear();
        User.playlists.clear();
        User.likes.clear();
    }

    private void onSignInInitialize(final FirebaseUser firebaseUser) {

        User.setUid(firebaseUser.getUid());
        User.setEmail(firebaseUser.getEmail());

        // Token init
        Task<InstanceIdResult> tokenTask = FirebaseInstanceId.getInstance().getInstanceId();
        tokenTask.addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                User.setToken_id(instanceIdResult.getToken());
                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("uid", User.uid);
                user.put("username", User.username);
                user.put("token", User.token_id);
                firestore.collection("Users").document(User.uid).set(user);

            }
        });




        prefReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // USER JUST REGISTERED
                    if(firebaseUser.getProviderId().contains("password"))
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                    System.out.println("SENT");
                            }
                        });
                    String newUsername= "User "+ CustomRandomId.randomNumberGenerator(4);
                    User.setUsername(newUsername);
                    AccountPreference newPref= new AccountPreference(newUsername, "image:209839Ry8C5wbAn6");
                    prefReference.child(firebaseUser.getUid()).setValue(newPref).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            User.initPreferences(custom_image);
                        }
                    });
                }
                else
                    User.initPreferences(custom_image);

                User.initLikes();
                User.initReviews();
                User.initPlaylists();
                User.initChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(firebaseUser.getProviderId().contains("password") && !firebaseUser.isEmailVerified()) {
            findViewById(R.id.drawer_layout).setVisibility(View.VISIBLE);
            new AlertDialog.Builder(this)
                    .setTitle("Verify your email address")
                    .setMessage("We sent you an email when you first registered. Please confirm your email and restart the app")
                    .setCancelable(false)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton("Send it again!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseUser.sendEmailVerification();
                            Snackbar.make(getCurrentFocus(), "Confermation mail resent! App will shut down soon...", Snackbar.LENGTH_LONG).show();
                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    System.exit(0);
                                }
                            }, 7000);

                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setNeutralButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                        }
                    })
                    .show();

        }
    }

   // GRAPHICAL ELEMENT SETUP METHODS

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the navigation history when you leave the activity
        outState.putIntegerArrayList(NAV_HISTORY, (ArrayList<Integer>) navigationHistory);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        // Proceed backwards in the navigation history
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

            if (id != R.id.nav_account && id != R.id.nav_social) {
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
            } else if (id == R.id.nav_social) {
                goToActivity(SocialActivity.class);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Virtually click on a navigation drawer item
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

    // Set fragment with an animation
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

        // Set the app bar icons to gray
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorGrayText), PorterDuff.Mode.SRC_ATOP);
            }
        }

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

            // Get center of search icon
            View searchIcon = findViewById(R.id.action_search);
            int coordinates[] = new int[2];
            searchIcon.getLocationOnScreen(coordinates);
            int cx = coordinates[0];
            int cy = coordinates[1];
            Intent intent = new Intent(this, SearchableActivity.class);

            // Pass the coordinates to the Searchable Activity
            intent.putExtra(getString(R.string.x_axis), cx);
            intent.putExtra(getString(R.string.y_axis), cy);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
