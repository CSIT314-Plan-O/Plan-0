package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for floating button
    private static final int TASKS = 0;
    private static final int REMINDERS = 1;

    int index;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationMenu;
    FloatingActionButton fab;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;

    ToDoListFragment tasksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab =(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (index == TASKS){
                    intent = new Intent(Menu.this, AddReminder.class);
                    startActivity(intent);
                }
                else if (index == REMINDERS){
                    intent = new Intent(Menu.this, AddTask.class);
                    startActivity(intent);
                }

            }
        });

        //bottom navigation view
        bottomNavigationMenu = findViewById(R.id.bottom_navigation);
        bottomNavigationMenu.clearAnimation();
        bottomNavigationMenu.setOnNavigationItemSelectedListener(navListener);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //set instance of firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mFirebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(Menu.this, Login.class));
                }
            }
        };



        //set default fragment
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ExamFragment()).commit();
            navigationView.setCheckedItem(R.id.action_exam);
        }

    }

    //bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.action_exam:
                            selectedFragment = new ExamFragment();
                            break;
                        case R.id.action_calendar:
                            selectedFragment = new CalendarFragment();
                            break;
                        case R.id.action_task:
                            selectedFragment = new TaskFragment();
                            //tasksFragment = new ToDoListFragment();
                            index=0;
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    //navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_reminder:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ReminderFragment()).commit();
                index=1;
                break;
            case R.id.nav_task:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TaskFragment()).commit();
                index=0;
                break;
            case R.id.nav_pomodoro:
                startActivity(new Intent(this, Pomodoro.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                mGoogleSignInClient.signOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    public void goToProfile(View view){
        startActivity(new Intent (Menu.this, Profile.class));
    }

}
