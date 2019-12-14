package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ToDoListAdapterForReminder extends BaseAdapter{

    private static final String KEY_CATEGORY = "Category";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_SUBJECT = "Subject";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_PRIORITY = "Priority";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_CALENDAR = "Calendar";
    private static final String KEY_DETAILS = "Details";
    private static final String TAG = "-----------------------";

    private final List<ToDoItem> mItems = new ArrayList<ToDoItem>();
    private final Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ToDoListAdapterForReminder(Context context) {

        mContext = context;

    }

    public void add(ToDoItem item) {
        mItems.add(item);
        sort("date");
    }


    public void edit(int position, Bundle extras) {
        ToDoItem item = mItems.get(position);
        item.setCategory(extras.getString(ToDoItem.CATEGORY));
        item.setTitle(extras.getString(ToDoItem.TITLE));
        item.setSubject(extras.getString(ToDoItem.SUBJECT));
        item.setType(extras.getString(ToDoItem.TYPE));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, extras.getInt(ToDoItem.DAY_OF_YEAR, 0));
        calendar.set(Calendar.YEAR, extras.getInt(ToDoItem.YEAR, 0));
        calendar.set(Calendar.HOUR_OF_DAY, extras.getInt(ToDoItem.HOUR_OF_DAY, 0));
        calendar.set(Calendar.MINUTE, extras.getInt(ToDoItem.MINUTE, 0));
        calendar.set(Calendar.SECOND, extras.getInt(ToDoItem.SECOND, 0));
        item.setDate(calendar);
        item.setPriority(ToDoItem.Priority.valueOf(extras.getString(ToDoItem.PRIORITY)));
        sort("date");
    }

    public void remove(int position) {
        mItems.remove(position);
    }

    public void sort(String sorter) {
        Collections.sort(mItems);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ToDoItem toDoItem = mItems.get(position);

        FrameLayout itemLayout = (FrameLayout) convertView;
        if (itemLayout == null)
            itemLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item_for_reminder, parent, false);

        String category = toDoItem.getCategory();
        String title = toDoItem.getTitle();
        String subject = toDoItem.getSubject();
        String type = toDoItem.getType();

        if (subject != null && !subject.equals("Other")) {
            if (title.equals(""))
                title = subject + " " + type;
            else
                title = subject + ": " + title;
        }

        if (title.equals(""))
            title = type;

        final View colorBarView = itemLayout.findViewById(R.id.color_bar);

        if (subject == null || subject.equals("Other"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        else if (subject.equals("CSIT111"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        else if (subject.equals("CSIT113"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange));
        else if (subject.equals("CSIT114"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
        else if (subject.equals("CSIT128"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        else if (subject.equals("CSIT115"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
        else if (subject.equals("CSIT212"))
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple));
        else
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(title);

        final TextView typeView = (TextView) itemLayout.findViewById(R.id.typeView);
        typeView.setText(type);

        final CheckBox statusView = (CheckBox) itemLayout.findViewById(R.id.statusCheckBox);

        if (toDoItem.getStatus() == ToDoItem.Status.DONE) {
            statusView.setChecked(true);
        } else {
            statusView.setChecked(false);
        }

        statusView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toDoItem.setStatus(ToDoItem.Status.DONE);
                } else {
                    toDoItem.setStatus(ToDoItem.Status.NOTDONE);
                }
            }
        });

        final ImageView priorityView = (ImageView) itemLayout.findViewById(R.id.priorityView);

        if (toDoItem.getPriority() == ToDoItem.Priority.HIGH) {
            priorityView.setImageResource(R.drawable.star_full);
        } else {
            priorityView.setImageResource(R.drawable.star_empty);
        }

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);

        Calendar date = toDoItem.getDate();
        Calendar c = Calendar.getInstance();

        String dateString;

        if (c.get(Calendar.YEAR) == date.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR) + 1 == date.get(Calendar.DAY_OF_YEAR))
            dateString = "Tomorrow";

        else if (c.get(Calendar.YEAR) == date.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
            dateString = "Today";

        else {
            int monthOfYear = date.get(Calendar.MONTH) + 1;
            int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
            String mon = "" + monthOfYear;
            String day = "" + dayOfMonth;

            if (monthOfYear < 10)
                mon = "0" + monthOfYear;
            if (dayOfMonth < 10)
                day = "0" + dayOfMonth;

            dateString = mon + "/" + day;
        }

        if (category.contains("Task"))
            dateString = "Due " + dateString;
        else if (category.contains("Event") || category.contains("Reminder")) {
            String timeString;

            int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
            int minuteOfDay = date.get(Calendar.MINUTE);
            String ampm = "AM";

            if (hourOfDay >= 12) {
                if (hourOfDay > 12)
                    hourOfDay = hourOfDay - 12;
                ampm = "PM";
            }

            if (hourOfDay == 0)
                hourOfDay = 12;

            String hour = "" + hourOfDay;
            String minute = "" + minuteOfDay;

            if (hourOfDay < 10)
                hour = "0" + hourOfDay;

            if (minuteOfDay < 10)
                minute = "0" + minuteOfDay;

            timeString = hour + ":" + minute + " " + ampm;

            dateString = dateString + " " + timeString;
        }

        dateView.setText((dateString));

        return itemLayout;
    }
}
