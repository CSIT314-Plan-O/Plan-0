package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.Calendar;
import java.util.List;

public class TaskFragment extends ListFragment {

    private static final int ADD_TODO_ITEM_REQUEST = 0;

    private static final String FILE_NAME = "TodoManagerActivityData.txt";
    private static final String TAG = "-----------------------";

    // IDs for menu items
    private static final int MENU_DELETE = android.view.Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    ToDoListAdapter mAdapter;

    private String mCategory = "Exam";
    private String mTitle = "CSIT113";
    private String mSubject = "Programming";
    private String mType = "IDK";
    private ToDoItem.Priority mPriority = ToDoItem.Priority.LOW;
    private ToDoItem.Status mStatus = ToDoItem.Status.NOTDONE;
    private Calendar mCalendarDate = Calendar.getInstance();
    private String mDetails = "who are you";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ToDoListAdapter(getActivity());
        mCalendarDate.set(2019,10,10,1,1,1);
        mAdapter.add(new ToDoItem(mCategory, mTitle, mSubject, mType, mPriority, mStatus, mCalendarDate, mDetails));
        mAdapter.add(new ToDoItem("Task", "CSIT314", "Software Methodology", "low", ToDoItem.Priority.HIGH, ToDoItem.Status.DONE, mCalendarDate, mDetails));
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ToDoItem toDoItem = (ToDoItem) getListView().getItemAtPosition(position);

        String category = toDoItem.getCategory();
        Intent intent = null;
        if (category.contains("Task"))
            intent = new Intent(getActivity(), AddTask.class);

        ToDoItem.packageIntent(intent, toDoItem.getCategory(), toDoItem.getTitle(), toDoItem.getSubject(),
                toDoItem.getType(), toDoItem.getPriority(), toDoItem.getStatus(), toDoItem.getDate(), toDoItem.getDetails());
        intent.putExtra("position", position);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Entered onActivityResult for fragment");

        if (resultCode == Activity.RESULT_OK && requestCode == ADD_TODO_ITEM_REQUEST) {
            ToDoItem newItem = new ToDoItem(data);
            if (newItem.getCategory().contains("Edit")) {
                Bundle extras = data.getExtras();
                mAdapter.edit(data.getIntExtra("position", 0), extras);
            } else
                mAdapter.add(newItem);
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


}
