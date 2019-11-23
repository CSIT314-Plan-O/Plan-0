package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class Pomodoro extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 1500000;
    //private static final long START_TIME_IN_MILLIS = 3000;

    private TextView textViewCountDown;
    private Button buttonStartPause, buttonReset;

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        textViewCountDown = findViewById(R.id.text_view_countdown);
        buttonStartPause = findViewById(R.id.btn_start_pause);
        buttonReset = findViewById(R.id.btn_reset);

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        timerRunning = false;
        buttonStartPause.setText("start");
        buttonReset.setVisibility(View.VISIBLE);
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                buttonStartPause.setText("start");
                buttonStartPause.setVisibility(View.INVISIBLE);
                buttonReset.setVisibility(View.VISIBLE);
                showForgotPasswordDialog();
            }
        }.start();

        timerRunning = true;
        buttonStartPause.setText("pause");
        buttonReset.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText(){
        int minutes = (int) (timeLeftInMillis / 1000)/60;
        int seconds = (int) (timeLeftInMillis / 1000)%60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountDown.setText(timeLeftFormatted);
    }

    private void resetTimer(){
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        buttonReset.setVisibility(View.INVISIBLE);
        buttonStartPause.setVisibility(View.VISIBLE);
    }

    private void showForgotPasswordDialog(){
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pomodoro Timer");

        //set linear layout
        LinearLayout linearLayout = new LinearLayout(this);

        //views to set in dialog
        TextView text = new TextView(this);
        text.setText("Take a 5 minutes break!");
        text.setTextSize(20);
        text.setPadding(50,10,10,10);
        linearLayout.addView(text);
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

    public void goBack(View view){
        finish();
    }
}
