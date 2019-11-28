package com.example.myapplication;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ToDoListFragment extends ListFragment {

    private static final int ADD_TODO_ITEM_REQUEST = 0;

    private static final String FILE_NAME = "TodoManagerActivityData.txt";
    private static final String TAG = "-----------------------";

    // IDs for menu items
    private static final int MENU_DELETE = android.view.Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    ToDoListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ToDoListAdapter(getActivity());
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