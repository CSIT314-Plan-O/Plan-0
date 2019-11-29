package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import com.example.myapplication.ToDoItem.Priority;
import com.example.myapplication.ToDoItem.Status;

public class AddExam extends AppCompatActivity {

    private static String dateString;
    private static TextView dateView;
    private static Calendar mCalendarDate;

    private Boolean isEdit;
    private CheckBox mPriorityCheckBox;
    private EditText mTitleText;
    private EditText mDetailsText;
    private Spinner mSubjectSpinner;
    private Spinner mTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        mTitleText = (EditText) findViewById(R.id.title);
        mDetailsText = (EditText) findViewById(R.id.details);
        mSubjectSpinner = (Spinner) findViewById(R.id.subject_spinner);
        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);
        mPriorityCheckBox = (CheckBox) findViewById(R.id.priority_checkBox);
        dateView = (TextView) findViewById(R.id.date);
        isEdit = false;

        Bundle extras = getIntent().getExtras();
        if (extras == null)
            setDefaultDateTime();
        else {
            isEdit = true;
            fillData(extras);
        }

        final Button datePickerButton = (Button) findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_todo, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_button:
                Priority priority = getPriority();
                Status status = Status.NOTDONE;
                String title = mTitleText.getText().toString();
                String details = mDetailsText.getText().toString();
                String subject = mSubjectSpinner.getSelectedItem().toString();
                String type = mTypeSpinner.getSelectedItem().toString();

                Intent data = new Intent();
                if (isEdit)
                    ToDoItem.packageIntent(data, "EditExam", title, subject, type, priority, status, mCalendarDate, details);
                else
                    ToDoItem.packageIntent(data, "Exam", title, subject, type, priority, status, mCalendarDate, details);

                setResult(RESULT_OK, data);
                finish();
                return true;

            case R.id.cancel_button:
                setResult(RESULT_CANCELED, null);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillData(Bundle extras) {
        mTitleText.setText(extras.getString(ToDoItem.TITLE));
        mSubjectSpinner.setSelection(((ArrayAdapter) mSubjectSpinner.getAdapter()).getPosition(extras.getString(ToDoItem.SUBJECT)));
        mTypeSpinner.setSelection(((ArrayAdapter) mTypeSpinner.getAdapter()).getPosition(extras.getString(ToDoItem.TYPE)));
        mCalendarDate.set(Calendar.YEAR, extras.getInt(ToDoItem.YEAR));
        mCalendarDate.set(Calendar.DAY_OF_YEAR, extras.getInt(ToDoItem.DAY_OF_YEAR));
        setDateString(mCalendarDate.get(Calendar.YEAR), mCalendarDate.get(Calendar.MONTH), mCalendarDate.get(Calendar.DAY_OF_MONTH));
        dateView.setText(dateString);
        if (extras.getString(ToDoItem.PRIORITY).equals("HIGH")) mPriorityCheckBox.setChecked(true);
        mDetailsText.setText(extras.getString(ToDoItem.DETAILS));

        getSupportActionBar();setTitle("Edit Exam");
        Window window = this.getWindow();
        String subject = extras.getString(ToDoItem.SUBJECT);

        if (subject == null) ;
        else if (subject.equals("CSIT111")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.red)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.redDark));
        } else if (subject.equals("CSIT113")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.orange)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.orangeDark));
        } else if (subject.equals("CSIT114")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.yellow)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.yellowDark));
        } else if (subject.equals("CSIT128")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.green)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.greenDark));
        } else if (subject.equals("CSIT115")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.blue)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blueDark));
        } else if (subject.equals("CSIT212")) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.purple)));
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.purpleDark));
        } else ;
    }

    private void setDefaultDateTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);

        mCalendarDate = c;

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateView.setText(dateString);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

        Calendar c = Calendar.getInstance();

        if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == monthOfYear && c.get(Calendar.DAY_OF_MONTH) + 1 == dayOfMonth)
            dateString = "Tomorrow";

        else if (c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == monthOfYear && c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
            dateString = "Today";

        else {
            monthOfYear++;
            String mon = "" + monthOfYear;
            String day = "" + dayOfMonth;
            year -= 2000;

            if (monthOfYear < 10)
                mon = "0" + monthOfYear;
            if (dayOfMonth < 10)
                day = "0" + dayOfMonth;

            dateString = mon + "/" + day + "/" + year;
        }
    }

    private Priority getPriority() {
        if (mPriorityCheckBox.isChecked()) {
            return Priority.HIGH;
        } else return Priority.LOW;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH) + 1;

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendarDate.set(year, monthOfYear, dayOfMonth);
            setDateString(year, monthOfYear, dayOfMonth);
            dateView.setText(dateString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

}
