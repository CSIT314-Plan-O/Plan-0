package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSchedule extends AppCompatActivity {

    private static final String TAG = "-----------------------";
    private EditText mClassText;
    private EditText mTimingText;

    private FirebaseFirestore db;
    CollectionReference academicClassItemsUsers;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            academicClassItemsUsers = db.collection("Users").document(userId).collection("AcademicClasses");
        }


        mClassText = (EditText) findViewById(R.id.mClass);
        mTimingText = (EditText) findViewById(R.id.mTiming);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_todo, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_button:

                String mClass = mClassText.getText().toString();
                String mTiming = mTimingText.getText().toString();

                //store Classes in database
                Map<String, Object> user = new HashMap<>();
                user.put("Class", mClass);
                user.put("Timing", mTiming);

                //add item to database
                academicClassItemsUsers.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "INSERTION OF DATA IS SUCCESS");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "INSERTION OF DATA IS FAILED");
                    }
                });

                finish();
                startActivity(new Intent(AddSchedule.this, Schedule.class));

                return true;

            case R.id.cancel_button:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
