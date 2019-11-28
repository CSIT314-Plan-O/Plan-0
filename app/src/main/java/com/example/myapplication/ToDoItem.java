package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ToDoItem implements Comparable<ToDoItem> {

    public enum Priority {
        LOW, HIGH
    }

    ;

    public enum Status {
        NOTDONE, DONE
    }

    ;

    public final static String ITEM_SEP = System.getProperty("line.separator");
    public final static String CATEGORY = "category";
    public final static String TITLE = "title";
    public final static String SUBJECT = "subject";
    public final static String TYPE = "type";
    public final static String PRIORITY = "priority";
    public final static String STATUS = "status";
    public final static String YEAR = "year";
    public final static String DAY_OF_YEAR = "day_of_year";
    public final static String HOUR_OF_DAY = "hour_of_day";
    public final static String MINUTE = "minute";
    public final static String SECOND = "second";
    public final static String DETAILS = "details";
    public final static String FILENAME = "filename";

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private String mCategory = "";
    private String mTitle = "";
    private String mSubject = "";
    private String mType = "";
    private Priority mPriority = Priority.LOW;
    private Status mStatus = Status.NOTDONE;
    private Calendar mCalendarDate = Calendar.getInstance();
    private String mDetails = "";

    ToDoItem(String category, String title, String subject, String type, Priority priority, Status status, Calendar calendarDate, String details) {
        this.mCategory = category;
        this.mTitle = title;
        this.mSubject = subject;
        this.mType = type;
        this.mPriority = priority;
        this.mStatus = status;
        this.mCalendarDate = calendarDate;
        this.mDetails = details;
    }

    ToDoItem(Intent intent) {
        Bundle extras = intent.getExtras();
        mCategory = intent.getStringExtra(ToDoItem.CATEGORY);
        mTitle = intent.getStringExtra(ToDoItem.TITLE);
        mSubject = intent.getStringExtra(ToDoItem.SUBJECT);
        mType = intent.getStringExtra(ToDoItem.TYPE);
        mPriority = Priority.valueOf(intent.getStringExtra(ToDoItem.PRIORITY));
        mStatus = Status.valueOf(intent.getStringExtra(ToDoItem.STATUS));
        mCalendarDate.set(Calendar.DAY_OF_YEAR, intent.getIntExtra(DAY_OF_YEAR, 0));
        mCalendarDate.set(Calendar.YEAR, intent.getIntExtra(YEAR, 0));
        mCalendarDate.set(Calendar.HOUR_OF_DAY, intent.getIntExtra(HOUR_OF_DAY, 0));
        mCalendarDate.set(Calendar.MINUTE, intent.getIntExtra(MINUTE, 0));
        mCalendarDate.set(Calendar.SECOND, intent.getIntExtra(SECOND, 0));
        mDetails = intent.getStringExtra(ToDoItem.DETAILS);
    }

    public int compareTo(ToDoItem item) {
        if (this.getStatus() == Status.DONE && item.getStatus() == Status.NOTDONE)
            return 1;
        if (this.getStatus() == Status.NOTDONE && item.getStatus() == Status.DONE)
            return -1;
        if (this.getPriority() == Priority.HIGH && item.getPriority() == Priority.LOW)
            return -1;
        if (this.getPriority() == Priority.LOW && item.getPriority() == Priority.HIGH)
            return 1;
        return this.getDate().compareTo(item.getDate());
    }

    public static void packageIntent(Intent intent, String category, String title, String subject, String type,
                                     Priority priority, Status status, Calendar date, String details) {
        intent.putExtra(ToDoItem.CATEGORY, category);
        intent.putExtra(ToDoItem.TITLE, title);
        intent.putExtra(ToDoItem.SUBJECT, subject);
        intent.putExtra(ToDoItem.TYPE, type);
        intent.putExtra(ToDoItem.PRIORITY, priority.toString());
        intent.putExtra(ToDoItem.STATUS, status.toString());
        intent.putExtra(ToDoItem.YEAR, date.get(Calendar.YEAR));
        intent.putExtra(ToDoItem.DAY_OF_YEAR, date.get(Calendar.DAY_OF_YEAR));
        intent.putExtra(ToDoItem.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(ToDoItem.MINUTE, date.get(Calendar.MINUTE));
        intent.putExtra(ToDoItem.SECOND, date.get(Calendar.SECOND));
        intent.putExtra(ToDoItem.DETAILS, details);
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public Calendar getDate() {
        return mCalendarDate;
    }

    public void setDate(Calendar date) {
        mCalendarDate = date;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String toString() {
        return mTitle + ITEM_SEP + mPriority + ITEM_SEP + mStatus + ITEM_SEP
                + FORMAT.format(mCalendarDate);
    }

    public String toLog() {
        return "Title:" + mTitle + ITEM_SEP + "Subject:" + mSubject + ITEM_SEP + "Type:" + mType + ITEM_SEP + "Priority:" + mPriority
                + ITEM_SEP + "Status:" + mStatus + ITEM_SEP + "Date:" + FORMAT.format(mCalendarDate) + "\n";
    }

}
