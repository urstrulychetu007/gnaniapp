package com.gnani.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gnani.ai.Recorder;
import com.gnani.ai.SpeechService;

public class MainActivity extends AppCompatActivity implements SpeechService.Listener, Recorder.RecordingStatusListener {

    private ImageButton button;
    private TextView recText;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button  = (ImageButton) findViewById(R.id.recordBtn);
        recText = (TextView) findViewById(R.id.rec_text);

        Recorder.bind(MainActivity.this);

        Intent intent = getIntent();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recorder.onRecord(intent.getStringExtra("lang"));
            }
        });

    }

    @Override
    public void onRecordingStatus(boolean status) {
        Log.e("Status"," "+status);
        runOnUiThread(()->{
            if(status){
                button.setImageResource(R.drawable.stop);
            }
            else{
                button.setImageResource(R.drawable.record);
            }
        });
    }


    @Override
    public void onSpeechRecognized(String text, String asr, boolean isFinal) {

        Log.e("Message"," "+text);
        runOnUiThread(()->{
            recText.setText(text);
        });


    }

    @Override
    public void onError(Throwable t) {
        Log.e("Error"," "+t);
        runOnUiThread(()->{ button.setImageResource(R.drawable.record); });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Recorder.unbind(MainActivity.this);
    }
}