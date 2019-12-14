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

public class ToDoListAdapterForSchedule extends BaseAdapter{

    private final List<AcademicClass> mItems = new ArrayList<AcademicClass>();
    private final Context mContext;

    public ToDoListAdapterForSchedule(Context context) {

        mContext = context;

    }

    public void add(AcademicClass item) {
        mItems.add(item);
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
        final AcademicClass classItem = mItems.get(position);

        FrameLayout itemLayout = (FrameLayout) convertView;
        if (itemLayout == null)
            itemLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item_for_academic_class, parent, false);

        String academicClass = classItem.getmClass();
        String timing = classItem.getmTiming();

        final View colorBarView = itemLayout.findViewById(R.id.color_bar);

        if (position % 7 == 0)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        else if (position % 7 == 1)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        else if (position % 7 == 2)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange));
        else if (position % 7 == 3)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
        else if (position % 7 == 4)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        else if (position % 7 == 5)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
        else if (position % 7 == 6)
            colorBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple));

        final TextView classView = (TextView) itemLayout.findViewById(R.id.classView);
        classView.setText(academicClass);

        final TextView timingView = (TextView) itemLayout.findViewById(R.id.timingView);
        timingView.setText(timing);

        return itemLayout;
    }
}
