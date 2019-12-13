package com.example.myapplication;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class ToDoListFragment extends ListFragment {
    private static final String KEY_CATEGORY = "Category";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_SUBJECT = "Subject";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_PRIORITY = "Priority";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_CALENDAR = "Calendar";
    private static final String KEY_DETAILS = "Details";
    private static final String TAG = "-----------------------";
    private static final int ADD_TODO_ITEM_REQUEST = 0;

    private static final String FILE_NAME = "TodoManagerActivityData.txt";


    // IDs for menu items
    private static final int MENU_DELETE = android.view.Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    ToDoListAdapter mAdapter;

    private FirebaseFirestore db;
    CollectionReference toDoItemsUsers;
    FirebaseAuth firebaseAuth;
    String userId;
    FirebaseUser firebaseUser;

    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = getView().findViewById(android.R.id.list);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            userId = firebaseUser.getUid();
            toDoItemsUsers = db.collection("Users").document(userId).collection("ToDoItems");
            getDatabase();
        }

        mAdapter = new ToDoListAdapter(getActivity());
        setListAdapter(mAdapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray positionChecker = list.getCheckedItemPositions();
                int count = list.getCount();

                for(int item=count-1; item>0; item--){
                    if(positionChecker.get(item)){
                        mAdapter.remove(item);
                    }
                }

                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ToDoItem toDoItem = (ToDoItem) getListView().getItemAtPosition(position);

        String category = toDoItem.getCategory();
        Intent intent = null;
        if (category.contains("Task"))
            intent = new Intent(getActivity(), AddTask.class);
        else if (category.contains("Exam"))
            intent = new Intent(getActivity(), AddExam.class);

        ToDoItem.packageIntent(intent, toDoItem.getCategory(), toDoItem.getTitle(), toDoItem.getSubject(),
                toDoItem.getType(), toDoItem.getPriority(), toDoItem.getStatus(), toDoItem.getDate(), toDoItem.getDetails());
        intent.putExtra("position", position);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Entered onActivityResult for fragment");

        if (resultCode == RESULT_OK && requestCode == ADD_TODO_ITEM_REQUEST) {
            ToDoItem newItem = new ToDoItem(data);
            if (newItem.getCategory().contains("Edit")) {
                Bundle extras = data.getExtras();
                mAdapter.edit(data.getIntExtra("position", 0), extras);

                //create map to update database
                final Map<String, Object> user = new HashMap<>();
                //user.put("Category", newItem.getCategory());
                user.put("Title", newItem.getTitle());
                user.put("Subject", newItem.getSubject());
                user.put("Type", newItem.getType());
                user.put("Priority", newItem.getPriority().toString());
                user.put("Status", newItem.getStatus().toString());
                user.put("Calendar", newItem.getDate().getTime());
                user.put("Details", newItem.getDetails());

                //get the document id where category is exam
                toDoItemsUsers.whereEqualTo("Category", "Exam").whereEqualTo("Title", newItem.getTitle())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //update user info
                                db.collection("Users").document(userId)
                                        .collection("ToDoItems").document(document.getId())
                                        .update(user);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

                //get the document id where category is task
                toDoItemsUsers.whereEqualTo("Category", "Task").whereEqualTo("Title", newItem.getTitle())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //update user info
                                db.collection("Users").document(userId)
                                        .collection("ToDoItems").document(document.getId())
                                        .update(user);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

            }else{
                mAdapter.add(newItem);

                //create map to store in database
                Map<String, Object> user = new HashMap<>();
                user.put("Category", newItem.getCategory());
                user.put("Title", newItem.getTitle());
                user.put("Subject", newItem.getSubject());
                user.put("Type", newItem.getType());
                user.put("Priority", newItem.getPriority().toString());
                user.put("Status", newItem.getStatus().toString());
                user.put("Calendar", newItem.getDate().getTime());
                user.put("Details", newItem.getDetails());

                //add item to database
                toDoItemsUsers.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
            }

            Log.i(TAG, "added item to adapter");
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        //	if (mAdapter.getCount() == 0)
        //loadItems();
    }

    @Override
    public void onPause() {
        super.onPause();

        //saveItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                mAdapter.clear();
                Log.i(TAG, "cleared");
                return true;
            case MENU_DUMP:
                dump();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dump() {

        for (int i = 0; i < mAdapter.getCount(); i++) {
            String data = ((ToDoItem) mAdapter.getItem(i)).toLog();
            Log.i(TAG, "Item " + i + ": " + data.replace(ToDoItem.ITEM_SEP, ","));
        }

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