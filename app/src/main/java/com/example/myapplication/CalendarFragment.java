package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    final List<EventDay> events = new ArrayList<>();
    private static final String TAG = "-----------------------";
    CalendarView calendarView;
    private FirebaseFirestore db;
    CollectionReference toDoItemsUsers;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            toDoItemsUsers = db.collection("Users").document(userId).collection("ToDoItems");
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 11, 2);


        calendarView = (CalendarView) getView().findViewById(R.id.calendarView);
        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        toDoItemsUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(d.getDate("Calendar"));

                        //if task then put task logo
                        if(d.getString("Category").equals("Task")){
                            events.add(new EventDay(calendar, R.drawable.tasks));
                        } else{
                            events.add(new EventDay(calendar, R.drawable.reminders));
                        }

                    }
                    calendarView.setEvents(events);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "INSERTION OF DATA IS SUCCESS");
            }
        });

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                final Calendar clickedDayCalendar = eventDay.getCalendar();

                int year = clickedDayCalendar.get(Calendar.YEAR);
                int month = clickedDayCalendar.get(Calendar.MONTH);
                int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);

                String date = year + "/" + month + "/" + day;

                //Create alertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tasks/Exams : " + date);

                //set linear layout
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);


                //views to set in dialog
                TextView text = new TextView(getActivity());
                text.setText("--------------------------------------\n");

                final TextView text2 = new TextView(getActivity());
                //check if events array exist in calendar

                toDoItemsUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            String tasksOrExams = "";

                            for (int i=0; i< events.size(); i++){
                                if(events.get(i).getCalendar() == clickedDayCalendar){

                                    final int finalI = i;

                                    for(DocumentSnapshot d : list){

                                        Calendar myCalendar = Calendar.getInstance();
                                        myCalendar.setTime(d.getDate("Calendar"));

                                        int year = myCalendar.get(Calendar.YEAR);
                                        int month = myCalendar.get(Calendar.MONTH);
                                        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
                                        if(events.get(i).getCalendar().get(Calendar.YEAR) == year &&
                                                events.get(i).getCalendar().get(Calendar.MONTH) == month &&
                                                events.get(i).getCalendar().get(Calendar.DAY_OF_MONTH) == day){

                                           tasksOrExams +=  "Category: " + d.getString("Category") + "\n" +
                                                            "Title: " + d.getString("Title") + "\n" +
                                                            "Type: " + d.getString("Type") + "\n" +
                                                            "Subject: " + d.getString("Subject" ) + "\n\n" +
                                                            "--------------------------------------\n\n";
                                           // break;
                                        }


                                    }

                                }

                            }
                            text2.setText(tasksOrExams);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "INSERTION OF DATA IS SUCCESS");
                    }
                });

                linearLayout.addView(text);
                linearLayout.addView(text2);

                linearLayout.setPadding(10,10,10,10);

                builder.setView(linearLayout);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel dialog
                        dialog.dismiss();
                    }
                });

                //show dialog
                builder.create().show();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();

        //to update calendar when a task or exam is made
        toDoItemsUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(d.getDate("Calendar"));

                        //if task then put task logo
                        if(d.getString("Category").equals("Task")){
                            events.add(new EventDay(calendar, R.drawable.tasks));
                        } else{
                            events.add(new EventDay(calendar, R.drawable.reminders));
                        }

                    }
                    calendarView.setEvents(events);
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
