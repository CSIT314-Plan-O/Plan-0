package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final int ADD_TODO_ITEM_REQUEST = 0;
    private static final String TAG = "-----------------------";
    private static final int TASKS = 0;
    private static final int EXAMS = 1;
    private static final int EVENTS = 2;
    private static final int REMINDERS = 3;

    // IDs for menu items
    private static final int MENU_DELETE = android.view.Menu.FIRST;
    private static final int MENU_DUMP = android.view.Menu.FIRST + 1;


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
    ToDoListFragment examsFragment;
    ToDoListFragment eventsFragment;
    ToDoListFragment remindersFragment;

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
                Intent intent = new Intent();
                if (index == REMINDERS)
                    intent = new Intent(Menu.this, AddReminder.class);
                else if (index == TASKS)
                    intent = new Intent(Menu.this, AddTask.class);
                else if (index == EXAMS)
                    intent = new Intent(Menu.this, AddExam.class);
                startActivityForResult(intent, ADD_TODO_ITEM_REQUEST);
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

        tasksFragment = new ToDoListFragment();
        examsFragment = new ToDoListFragment();
        eventsFragment = new ToDoListFragment();
        remindersFragment = new ToDoListFragment();

        index = EXAMS;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, examsFragment, "MY_FRAGMENT_EXAM");
        ft.commit();
        navigationView.setCheckedItem(R.id.action_exam);
        getSupportActionBar().setTitle("Exam");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.i(TAG, "Entered onActivityResult for Activity");

        ToDoListFragment myFragment = (ToDoListFragment) getFragmentManager().findFragmentByTag("MY_FRAGMENT_EXAM");
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = new CalendarFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    switch(menuItem.getItemId()){
                        case R.id.action_exam:
                            index = EXAMS;
                            getSupportActionBar().setTitle("Exam");
                            if(fm.findFragmentByTag("MY_FRAGMENT_EXAM") != null) {
                                //if the fragment exists, show it.
                                ft.show(fm.findFragmentByTag("MY_FRAGMENT_EXAM"));
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                ft.add(R.id.fragment_container, tasksFragment,"MY_FRAGMENT_EXAM");
                            }
                            if(fm.findFragmentByTag("MY_FRAGMENT_TASK") != null){
                                //if the other fragment is visible, hide it.
                                ft.hide(fm.findFragmentByTag("MY_FRAGMENT_TASK"));
                            }
                            if(getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT_CALENDAR") != null){
                                //if the other fragment is visible, hide it.
                                getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager()
                                        .findFragmentByTag("MY_FRAGMENT_CALENDAR")).commit();
                            }
                            ft.commit();
                            break;
                        case R.id.action_calendar:
                            getSupportActionBar().setTitle("Calendar");

                            if(getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT_CALENDAR") != null) {
                                //if the fragment exists, show it.
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager()
                                        .findFragmentByTag("MY_FRAGMENT_CALENDAR")).commit();
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                                        selectedFragment, "MY_FRAGMENT_CALENDAR").commit();
                            }
                            if(fm.findFragmentByTag("MY_FRAGMENT_EXAM") != null){
                                //if the other fragment is visible, hide it.
                                ft.hide(fm.findFragmentByTag("MY_FRAGMENT_EXAM"));
                            }
                            if(fm.findFragmentByTag("MY_FRAGMENT_TASK") != null){
                                //if the other fragment is visible, hide it.
                                ft.hide(fm.findFragmentByTag("MY_FRAGMENT_TASK"));
                            }
                            ft.commit();
                            break;
                        case R.id.action_task:
                            index = TASKS;
                            getSupportActionBar().setTitle("Task");
                            if(fm.findFragmentByTag("MY_FRAGMENT_TASK") != null) {
                                //if the fragment exists, show it.
                                ft.show(fm.findFragmentByTag("MY_FRAGMENT_TASK"));
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                ft.add(R.id.fragment_container, tasksFragment,"MY_FRAGMENT_TASK");
                            }
                            if(fm.findFragmentByTag("MY_FRAGMENT_EXAM") != null){
                                //if the other fragment is visible, hide it.
                                ft.hide(fm.findFragmentByTag("MY_FRAGMENT_EXAM"));
                            }
                            if(getSupportFragmentManager().findFragmentByTag("MY_FRAGMENT_CALENDAR") != null){
                                //if the other fragment is visible, hide it.
                                getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager()
                                        .findFragmentByTag("MY_FRAGMENT_CALENDAR")).commit();
                            }
                            ft.commit();
                            break;
                    }

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
            case R.id.nav_pomodoro:
                startActivity(new Intent(this, Pomodoro.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, Settings.class));
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
