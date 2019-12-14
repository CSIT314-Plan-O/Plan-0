package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

public class Schedule extends AppCompatActivity {

    private static final String KEY_CLASS = "Class";
    private static final String KEY_TIMING = "Timing";

    private static final String TAG = "-----------------------";

    Toolbar toolbar;
    ToDoListAdapterForSchedule mAdapter;
    ListView listView;
    FloatingActionButton fab;

    private FirebaseFirestore db;
    CollectionReference academicClassesUsers;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            academicClassesUsers = db.collection("Users").document(userId).collection("AcademicClasses");
            getDatabase();
        }

        listView = findViewById(R.id.listview);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable goBack = getResources().getDrawable(R.drawable.ic_go_back);
        goBack.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(goBack);

        //go back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Schedule.this, Menu.class));
                finish();
            }
        });

        fab =(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Schedule.this, AddSchedule.class);
                startActivity(myIntent);
            }
        });

        mAdapter = new ToDoListAdapterForSchedule(this);
        listView.setAdapter(mAdapter);

        //removing item when pressed long
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mAdapter.remove(position);

                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    //Get toDoItem from database
    public void getDatabase(){
        academicClassesUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){

                        String academicClass = d.getString(KEY_CLASS);
                        String timing = d.getString(KEY_TIMING);

                        mAdapter.add(new AcademicClass(academicClass, timing));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "INSERTION OF DATA IS SUCCESS");
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        //getDatabase();
    }
}
