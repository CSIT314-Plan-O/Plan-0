package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

public class Reminder extends AppCompatActivity  {

    private static final String KEY_CATEGORY = "Category";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_SUBJECT = "Subject";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_PRIORITY = "Priority";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_CALENDAR = "Calendar";
    private static final String KEY_DETAILS = "Details";
    private static final String TAG = "-----------------------";

    Toolbar toolbar;
    ToDoListAdapterForReminder mAdapter;
    ListView listView;

    private FirebaseFirestore db;
    CollectionReference toDoItemsUsers;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            toDoItemsUsers = db.collection("Users").document(userId).collection("ToDoItems");
            getDatabase();
        }

        listView = findViewById(R.id.listview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reminder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable goBack = getResources().getDrawable(R.drawable.ic_go_back);
        goBack.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(goBack);

        //go back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Reminder.this, Menu.class));
                finish();
            }
        });

        mAdapter = new ToDoListAdapterForReminder(this);
        listView.setAdapter(mAdapter);
    }

    //Get toDoItem from database
    public void getDatabase(){
        toDoItemsUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){

                        String category = d.getString(KEY_CATEGORY);
                        String title = d.getString(KEY_TITLE);
                        String subject = d.getString(KEY_SUBJECT);
                        String type = d.getString(KEY_TYPE);
                        ToDoItem.Priority priority;
                        if(d.getString(KEY_PRIORITY).equals("LOW")){
                            priority = ToDoItem.Priority.LOW;
                        }else{
                            priority = ToDoItem.Priority.HIGH;
                        }
                        ToDoItem.Status status;
                        if(d.getString(KEY_STATUS).equals("DONE")){
                            status = ToDoItem.Status.DONE;
                        }else{
                            status = ToDoItem.Status.NOTDONE;
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(d.getDate(KEY_CALENDAR));
                        String details = d.getString(KEY_DETAILS);

                        mAdapter.add(new ToDoItem(category,title,subject,type,priority,status,calendar,details));
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

}
