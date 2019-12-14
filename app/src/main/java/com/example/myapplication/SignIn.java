package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "-----------------------";
    private EditText emailText, passwordText, confirmPasswordText;
    private Button btnSignUp;
    FirebaseAuth mFirebaseAuth;
    ProgressDialog progressDialog;
    private FirebaseFirestore db;
    DocumentReference documentReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignIn);

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String confirmPassword = confirmPasswordText.getText().toString();

                if(email.isEmpty()){
                    emailText.setError("Please enter Email");
                    emailText.requestFocus();
                }else if(password.isEmpty()){
                    passwordText.setError("Please enter Password");
                    passwordText.requestFocus();
                }else if(confirmPassword.isEmpty()){
                    confirmPasswordText.setError("Confirm Password");
                    confirmPasswordText.requestFocus();
                }else if(!(email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty())){
                    //CHECK IF PASSWORD IS >= 6 characters
                    progressDialog.setMessage("Creating account...");
                    progressDialog.show();
                    if(password.equals(confirmPassword)){
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(!task.isSuccessful()){
                                    passwordText.setError("Password must have more than 6 characters");
                                }else{
                                    Toast.makeText(SignIn.this, "Signup Successful", Toast.LENGTH_LONG);

                                    //Put user Email in fireStore
                                    userId = mFirebaseAuth.getCurrentUser().getUid();
                                    documentReference = db.collection("Users").document(userId);

                                    Calendar setCalendar = Calendar.getInstance();
                                    setCalendar.set(1900,0,1);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Email", emailText.getText().toString());
                                    user.put("Birthdate", setCalendar.getTime());

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "user Created" );

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure" + e.getMessage());
                                        }
                                    });

                                }
                            }

                        });
                    }else{
                        progressDialog.dismiss();
                        passwordText.setError("Password is not the same");
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(SignIn.this, "Error occured", Toast.LENGTH_SHORT);
                }
            }
        });

    }

}
