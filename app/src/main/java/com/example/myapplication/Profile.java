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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class Profile extends AppCompatActivity {

//    private final int requestCode = 1;
    private static final String TAG = "-----------------------";
    Toolbar toolbar;
    Button btnLogOut, btnEditProfile;
    Calendar mCalendarDate;
    TextView profileEmail, profileName, profileBirthdate;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;

    DocumentReference documentReference;
    String userId;
    private FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mCalendarDate = Calendar.getInstance();

        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileBirthdate = findViewById(R.id.profileBirthdate);

        //set instance of firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            documentReference = db.collection("Users").document(userId);
        }

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
                    startActivity(new Intent(Profile.this, Login.class));
                }
            }
        };

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable goBack = getResources().getDrawable(R.drawable.ic_go_back);
        goBack.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(goBack);


        Drawable dr = getResources().getDrawable(R.drawable.logout);
        btnLogOut = findViewById(R.id.btn_logout);
        dr.setBounds(0,0, 50,50);
        btnLogOut.setCompoundDrawables(dr, null,null,null);
        //go back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, Menu.class));
            }
        });

        //Setting email profile from database
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String email = documentSnapshot.getString("Email");
                    String name = documentSnapshot.getString("Name");
                    Date date = documentSnapshot.getDate("Birthdate");


                    mCalendarDate.setTime(date);

                    int year = mCalendarDate.get(Calendar.YEAR);
                    int month = mCalendarDate.get(Calendar.MONTH);
                    int day = mCalendarDate.get(Calendar.DAY_OF_MONTH);

                    profileEmail.setText(email);
                    profileName.setText(name);
                    profileBirthdate.setText(day + "/" + (month+1) + "/" +year);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILURE " + e.getMessage());
            }
        });


        btnEditProfile = findViewById(R.id.btn_edit);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = profileEmail.getText().toString();
                String name = profileName.getText().toString();
                String birthdate = profileBirthdate.getText().toString();

                Intent i = new Intent(Profile.this, EditProfile.class);
                i.putExtra("Email", email);
                i.putExtra("Name", name);
                i.putExtra("Birthdate", birthdate);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void logOut(View view){
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut();
    }
}
