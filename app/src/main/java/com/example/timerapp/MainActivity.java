package com.example.timerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Chronometer timer;
    TextView prevTime;
    EditText taskType;
    TextView timeDisplay;
    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton stopButton;
    SharedPreferences sharedPreferences;
    static String result;

    public static final String LAST_RESULT = "0";
    public static final String LAST_TASK = "1";

    static long elapsed;
    static boolean running;
    static long startTime;
    static long pauseTime;
    static long stopTime;
    static boolean started;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.timerChron);
        prevTime = findViewById(R.id.prevTime);
        taskType = findViewById(R.id.taskType);
        timeDisplay  = findViewById(R.id.timeDisplay);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        sharedPreferences = getSharedPreferences("com.example.timerapp", MODE_PRIVATE);


        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer timer)
            {
                if (running == true) {
                    elapsed = SystemClock.elapsedRealtime() - startTime - pauseTime;

                    long sec = (elapsed / 1000) % 60;
                    long min = (elapsed / (1000 * 60)) % 60;
                    long hour = (elapsed / (1000 * 60 * 60)) % 24;

                    result = String.format("%02d:%02d:%02d", hour, min, sec);
                    timeDisplay.setText(result);

                }
            }
        });

        if (savedInstanceState != null)
        {
            prevTime.setText(savedInstanceState.getString("prev_time"));
            timeDisplay.setText(result);
            timer.start();
        }
        else
        {
            checkSharedPreferences();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("prev_time", prevTime.getText().toString());

    }

    public void playClick(View view)
    {
        if(running == false)
        {
            running = true;

            if(started == false)
            {
                startTime = SystemClock.elapsedRealtime();
                timer.start();
                started = true;
            }
            else
            {
                pauseTime += SystemClock.elapsedRealtime() - stopTime;
                timer.start();
                started = true;
            }
        }
    }

    public void pauseClick(View view)
    {
        stopTime = SystemClock.elapsedRealtime();
        timer.stop();
        running = false;
    }

    public void stopClick(View view)
    {
        timer.stop();

        if(started == true)
        {
            started = false;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_RESULT, result);
            editor.putString(LAST_TASK, taskType.getText().toString());
            editor.apply();
        }
        running = false;
        pauseTime = 0;
    }

    public void checkSharedPreferences()
    {

        String lastResult = sharedPreferences.getString(LAST_RESULT, "");
        String lastTask = sharedPreferences.getString(LAST_TASK, "");

        if(lastResult.equals(""))
        {
            prevTime.setText("");
        }
        else if(lastTask.equals(""))
        {
            prevTime.setText("You spent " + lastResult + " on an unspecified task last time.");
        }
        else
        {
            prevTime.setText("You spent " + lastResult + " on " + lastTask + " last time.");
        }

    }
}