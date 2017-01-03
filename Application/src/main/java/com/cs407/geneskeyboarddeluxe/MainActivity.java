package com.cs407.geneskeyboarddeluxe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public static final String modeFile = "Mode";

    SoundPool keySoundPool;
    HashMap<String, Integer> keySoundPoolMap;

    HashMap<String, Integer> keyStreamMap;

    SoundPool sampleSoundPool;
    HashMap<String, Integer> sampleSoundPoolMap;

    private MediaRecorder recorder = null;
    private static String mFileName = null;
    private static String mFileNameBase = null;

    private Map<String, String[]> presetMap;
    private List<String> presetList;
    private String[] currSampleArray;
    private List<String> sampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAudioRecording();
        requestFileSaving();

        getPresetMap();
        getPresetList();
        getSampleList();
        getCurrSampleArray();
        putSampleArray(currSampleArray);

        Button assignSample = (Button) findViewById(R.id.assign);
        Button savePreset = (Button) findViewById(R.id.save);
        final Button recordSample = (Button) findViewById(R.id.recordButton);
        Button editSample = (Button) findViewById(R.id.edit);
        Button loadPreset = (Button) findViewById(R.id.load);

        final TextView recordText = (TextView) findViewById(R.id.recordText);

        final Button pianoButton = (Button) findViewById(R.id.pianoButton);
        pianoButton.setBackgroundResource(R.drawable.settingsbutton);

        final Button synthButton = (Button) findViewById(R.id.synthButton);
        final TextView modeLoad = (TextView) findViewById(R.id.mode);

        keySoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 100);

        final SharedPreferences settings = getSharedPreferences(modeFile, 0);
        final SharedPreferences.Editor editor = settings.edit();
        String savedMode = settings.getString("savedMode", "synth");
        if(savedMode.equals("synth")){
            modeLoad.setText("LOADING");
            new swapKeys().execute("synth");

            synthButton.setBackgroundResource(R.drawable.samplebutton);
            synthButton.setTextColor(Color.parseColor("#000000"));
            pianoButton.setBackgroundResource(R.drawable.settingsbutton);
            pianoButton.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            modeLoad.setText("LOADING");
            new swapKeys().execute("piano");

            pianoButton.setBackgroundResource(R.drawable.samplebutton);
            pianoButton.setTextColor(Color.parseColor("#000000"));
            synthButton.setBackgroundResource(R.drawable.settingsbutton);
            synthButton.setTextColor(Color.parseColor("#FFFFFF"));
        }



        pianoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("savedMode", "piano");
                editor.commit();
                modeLoad.setText("LOADING");
                new swapKeys().execute("piano");

                pianoButton.setBackgroundResource(R.drawable.samplebutton);
                pianoButton.setTextColor(Color.parseColor("#000000"));
                synthButton.setBackgroundResource(R.drawable.settingsbutton);
                synthButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });

        synthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("savedMode", "synth");
                editor.commit();
                modeLoad.setText("LOADING");
                new swapKeys().execute("synth");

                synthButton.setBackgroundResource(R.drawable.samplebutton);
                synthButton.setTextColor(Color.parseColor("#000000"));
                pianoButton.setBackgroundResource(R.drawable.settingsbutton);
                pianoButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });


        samplePlayMode();

        loadPreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPreset();
            }
        });

        savePreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreset();
            }
        });

        assignSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highLightSamples();
                selectSample();
            }
        });

        recordSample.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;
            @Override
            public void onClick(View view) {
                onRecord(mStartRecording);

                if (mStartRecording) {
                    recordText.setText("");
                    recordSample.setBackgroundResource(R.drawable.recordingbutton);
                } else {
                    recordText.setText("");
                    recordSample.setBackgroundResource(R.drawable.recordbutton);
                }
                mStartRecording = !mStartRecording;

            }
        });
        editSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSampleList();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveCurrSampleArray();
        presetCleanup();
        savePresetList();
        savePresetMap();
        saveSampleList();

    }

    private class swapKeys extends AsyncTask<String, Void, Integer> {
        final TextView modeLoad = (TextView) findViewById(R.id.mode);
        protected Integer doInBackground(String... type){

            disableKeys();
            if(type[0].equals("synth")){
                synthLoad();
                return 0;
            }else{
                pianoLoad();
                return 1;
            }

        }

        protected void onPostExecute(Integer result) {
            if(result == 0){
                keyPressSetupSynth();
            }else{
                keyPressSetupPiano();
            }
            modeLoad.setText("MODE");
        }

    }
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void requestAudioRecording() {
        int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;

        String TAG = "yo";
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Permission to record denied");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
    }


    private void requestFileSaving() {
        int MY_PERMISSIONS_REQUEST_SAVE_FILES = 101;

        String TAG = "FileSave";

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Permission to filesave denied");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_SAVE_FILES);
        }
    }


    public MainActivity(){

        mFileNameBase = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(mFileNameBase + "/geneskeyboardsamples");
        if(!dir.exists()){
            dir.mkdirs();
        }
        mFileName = mFileNameBase + "/geneskeyboardsamples/temp.3gp";
    }
    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(mFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("record", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        saveSampleName();
    }


    private void disableKeys(){
        final Button keyC4 = (Button)this.findViewById(R.id.key1);
        final Button keyC4sharp = (Button)this.findViewById(R.id.blackkey1);
        final Button keyD4 = (Button)this.findViewById(R.id.key2);
        final Button keyD4sharp = (Button)this.findViewById(R.id.blackkey2);
        final Button keyE4 = (Button)this.findViewById(R.id.key3);

        final Button keyF4 = (Button)this.findViewById(R.id.key4);
        final Button keyF4sharp = (Button)this.findViewById(R.id.blackkey3);
        final Button keyG4 = (Button)this.findViewById(R.id.key5);
        final Button keyG4sharp = (Button)this.findViewById(R.id.blackkey4);
        final Button keyA4 = (Button)this.findViewById(R.id.key6);
        final Button keyA4sharp = (Button)this.findViewById(R.id.blackkey5);

        final Button keyB4 = (Button)this.findViewById(R.id.key7);
        final Button keyC5 = (Button)this.findViewById(R.id.key8);
        final Button keyC5sharp = (Button)this.findViewById(R.id.blackkey6);
        final Button keyD5 = (Button)this.findViewById(R.id.key9);
        final Button keyD5sharp = (Button)this.findViewById(R.id.blackkey7);
        final Button keyE5 = (Button)this.findViewById(R.id.key10);

        keyC4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyC4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });

        keyC4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyC4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyD4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyD4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyE4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyE4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyF4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyF4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyF4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyF4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyF4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyF4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyG4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyG4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyG4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyG4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyA4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyA4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyA4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyA4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyB4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyB4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyB4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyC5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyC5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyD5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyD5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyE5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyE5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
    }
    private void synthLoad(){
        keyStreamMap = new HashMap<String, Integer>();
        keySoundPoolMap = new HashMap<String, Integer>();
        keySoundPoolMap.put("C4", keySoundPool.load(this, R.raw.c4synth, 1));
        keySoundPoolMap.put("C4sharp", keySoundPool.load(this, R.raw.c4sharpsynth, 1));
        keySoundPoolMap.put("D4", keySoundPool.load(this, R.raw.d4synth, 1));
        keySoundPoolMap.put("D4sharp", keySoundPool.load(this, R.raw.d4sharpsynth, 1));
        keySoundPoolMap.put("E4", keySoundPool.load(this, R.raw.e4synth, 1));

        keySoundPoolMap.put("F4", keySoundPool.load(this, R.raw.f4synth, 1));
        keySoundPoolMap.put("F4sharp", keySoundPool.load(this, R.raw.f4sharpsynth, 1));
        keySoundPoolMap.put("G4", keySoundPool.load(this, R.raw.g4synth, 1));
        keySoundPoolMap.put("G4sharp", keySoundPool.load(this, R.raw.g4sharpsynth, 1));
        keySoundPoolMap.put("A4", keySoundPool.load(this, R.raw.a4synth, 1));
        keySoundPoolMap.put("A4sharp", keySoundPool.load(this, R.raw.a4sharpsynth, 1));
        keySoundPoolMap.put("B4", keySoundPool.load(this, R.raw.b4synth, 1));

        keySoundPoolMap.put("C5", keySoundPool.load(this, R.raw.c5synth, 1));
        keySoundPoolMap.put("C5sharp", keySoundPool.load(this, R.raw.c5sharpsynth, 1));
        keySoundPoolMap.put("D5", keySoundPool.load(this, R.raw.d5synth, 1));
        keySoundPoolMap.put("D5sharp", keySoundPool.load(this, R.raw.d5sharpsynth, 1));
        keySoundPoolMap.put("E5", keySoundPool.load(this, R.raw.e5synth, 1));
    }

    private void keyPressSetupSynth(){

        final Button keyC4 = (Button)this.findViewById(R.id.key1);
        final Button keyC4sharp = (Button)this.findViewById(R.id.blackkey1);
        final Button keyD4 = (Button)this.findViewById(R.id.key2);
        final Button keyD4sharp = (Button)this.findViewById(R.id.blackkey2);
        final Button keyE4 = (Button)this.findViewById(R.id.key3);

        final Button keyF4 = (Button)this.findViewById(R.id.key4);
        final Button keyF4sharp = (Button)this.findViewById(R.id.blackkey3);
        final Button keyG4 = (Button)this.findViewById(R.id.key5);
        final Button keyG4sharp = (Button)this.findViewById(R.id.blackkey4);
        final Button keyA4 = (Button)this.findViewById(R.id.key6);
        final Button keyA4sharp = (Button)this.findViewById(R.id.blackkey5);

        final Button keyB4 = (Button)this.findViewById(R.id.key7);
        final Button keyC5 = (Button)this.findViewById(R.id.key8);
        final Button keyC5sharp = (Button)this.findViewById(R.id.blackkey6);
        final Button keyD5 = (Button)this.findViewById(R.id.key9);
        final Button keyD5sharp = (Button)this.findViewById(R.id.blackkey7);
        final Button keyE5 = (Button)this.findViewById(R.id.key10);

        keyC4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED

                        keyStreamMap.put("C4", playSynthSound("C4"));
                        keyC4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("C4") != null){
                            keySoundPool.stop(keyStreamMap.get("C4"));
                        }

                        keyC4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("C4sharp", playSynthSound("C4sharp"));
                        keyC4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("C4sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("C4sharp"));
                        }
                        keyC4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("D4", playSynthSound("D4"));
                        keyD4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("D4") != null){
                            keySoundPool.stop(keyStreamMap.get("D4"));
                        }
                        keyD4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("D4sharp", playSynthSound("D4sharp"));
                        keyD4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("D4sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("D4sharp"));
                        }
                        keyD4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("E4", playSynthSound("E4"));
                        keyE4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("E4") != null){
                            keySoundPool.stop(keyStreamMap.get("E4"));
                        }
                        keyE4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });




        keyF4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("F4", playSynthSound("F4"));
                        keyF4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("F4") != null){
                            keySoundPool.stop(keyStreamMap.get("F4"));
                        }
                        keyF4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyF4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("F4sharp", playSynthSound("F4sharp"));
                        keyF4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("F4sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("F4sharp"));
                        }
                        keyF4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("G4", playSynthSound("G4"));
                        keyG4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("G4") != null){
                            keySoundPool.stop(keyStreamMap.get("G4"));
                        }
                        keyG4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("G4sharp", playSynthSound("G4sharp"));
                        keyG4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("G4sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("G4sharp"));
                        }
                        keyG4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("A4", playSynthSound("A4"));
                        keyA4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("A4") != null){
                            keySoundPool.stop(keyStreamMap.get("A4"));
                        }
                        keyA4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("A4sharp", playSynthSound("A4sharp"));
                        keyA4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("A4sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("A4sharp"));
                        }
                        keyA4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyB4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("B4", playSynthSound("B4"));
                        keyB4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("B4") != null){
                            keySoundPool.stop(keyStreamMap.get("B4"));
                        }
                        keyB4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });





        keyC5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("C5", playSynthSound("C5"));
                        keyC5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("C5") != null){
                            keySoundPool.stop(keyStreamMap.get("C5"));
                        }
                        keyC5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("C5sharp", playSynthSound("C5sharp"));
                        keyC5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("C5sharp") != null){
                            keySoundPool.stop(keyStreamMap.get("C5sharp"));
                        }
                        keyC5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("D5", playSynthSound("D5"));
                        keyD5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("D5") != null){
                            keySoundPool.stop(keyStreamMap.get("D5"));
                        }
                        keyD5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("D5sharp", playSynthSound("D5sharp"));
                        keyD5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("D5sharp") != null) {
                            keySoundPool.stop(keyStreamMap.get("D5sharp"));
                        }
                        keyD5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        keyStreamMap.put("E5", playSynthSound("E5"));
                        keyE5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if(keyStreamMap.get("E5") != null){
                            keySoundPool.stop(keyStreamMap.get("E5"));
                        }
                        keyE5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
    }

    private void pianoLoad(){
        keySoundPoolMap = new HashMap<String, Integer>();
        keyStreamMap = new HashMap<String, Integer>();
        keySoundPoolMap.put("C4", keySoundPool.load(this, R.raw.c4, 1));
        keySoundPoolMap.put("C4sharp", keySoundPool.load(this, R.raw.c4sharp, 1));
        keySoundPoolMap.put("D4", keySoundPool.load(this, R.raw.d4, 1));
        keySoundPoolMap.put("D4sharp", keySoundPool.load(this, R.raw.d4sharp, 1));
        keySoundPoolMap.put("E4", keySoundPool.load(this, R.raw.e4, 1));

        keySoundPoolMap.put("F4", keySoundPool.load(this, R.raw.f4, 1));
        keySoundPoolMap.put("F4sharp", keySoundPool.load(this, R.raw.f4sharp, 1));
        keySoundPoolMap.put("G4", keySoundPool.load(this, R.raw.g4, 1));
        keySoundPoolMap.put("G4sharp", keySoundPool.load(this, R.raw.g4sharp, 1));
        keySoundPoolMap.put("A4", keySoundPool.load(this, R.raw.a4, 1));
        keySoundPoolMap.put("A4sharp", keySoundPool.load(this, R.raw.a4sharp, 1));
        keySoundPoolMap.put("B4", keySoundPool.load(this, R.raw.b4, 1));

        keySoundPoolMap.put("C5", keySoundPool.load(this, R.raw.c5, 1));
        keySoundPoolMap.put("C5sharp", keySoundPool.load(this, R.raw.c5sharp, 1));
        keySoundPoolMap.put("D5", keySoundPool.load(this, R.raw.d5, 1));
        keySoundPoolMap.put("D5sharp", keySoundPool.load(this, R.raw.d5sharp, 1));
        keySoundPoolMap.put("E5", keySoundPool.load(this, R.raw.e5, 1));
    }
    private void keyPressSetupPiano(){

        final Button keyC4 = (Button)this.findViewById(R.id.key1);
        final Button keyC4sharp = (Button)this.findViewById(R.id.blackkey1);
        final Button keyD4 = (Button)this.findViewById(R.id.key2);
        final Button keyD4sharp = (Button)this.findViewById(R.id.blackkey2);
        final Button keyE4 = (Button)this.findViewById(R.id.key3);

        final Button keyF4 = (Button)this.findViewById(R.id.key4);
        final Button keyF4sharp = (Button)this.findViewById(R.id.blackkey3);
        final Button keyG4 = (Button)this.findViewById(R.id.key5);
        final Button keyG4sharp = (Button)this.findViewById(R.id.blackkey4);
        final Button keyA4 = (Button)this.findViewById(R.id.key6);
        final Button keyA4sharp = (Button)this.findViewById(R.id.blackkey5);

        final Button keyB4 = (Button)this.findViewById(R.id.key7);
        final Button keyC5 = (Button)this.findViewById(R.id.key8);
        final Button keyC5sharp = (Button)this.findViewById(R.id.blackkey6);
        final Button keyD5 = (Button)this.findViewById(R.id.key9);
        final Button keyD5sharp = (Button)this.findViewById(R.id.blackkey7);
        final Button keyE5 = (Button)this.findViewById(R.id.key10);

        keyC4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("C4");
                        keyC4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("C4sharp");
                        keyC4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("D4");
                        keyD4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("D4sharp");
                        keyD4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("E4");
                        keyE4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyE4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });




        keyF4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("F4");
                        keyF4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyF4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyF4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("F4sharp");
                        keyF4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyF4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("G4");
                        keyG4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyG4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyG4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("G4sharp");
                        keyG4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyG4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("A4");
                        keyA4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyA4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyA4sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("A4sharp");
                        keyA4sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyA4sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyB4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("B4");
                        keyB4.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyB4.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });





        keyC5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("C5");
                        keyC5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyC5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("C5sharp");
                        keyC5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyC5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("D5");
                        keyD5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyD5sharp.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("D5sharp");
                        keyD5sharp.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyD5sharp.setBackgroundResource(R.drawable.blackkey);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        keyE5.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        playKeySound("E5");
                        keyE5.setBackgroundResource(R.drawable.key_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        keyE5.setBackgroundResource(R.drawable.key_unpressed);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });

    }
    private void playKeySound(String sound){
        AudioManager mgr = (AudioManager)this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        if(keySoundPool != null){
            keySoundPool.play(keySoundPoolMap.get(sound), (float)(volume*.7), (float)(volume*.7), 1, 0, 1.0f);
        }

    }


    private int playSynthSound(String sound){
        AudioManager mgr = (AudioManager)this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        int streamID = 0;
        if(keySoundPool != null){
           streamID =  keySoundPool.play(keySoundPoolMap.get(sound), (float)(volume*.7), (float)(volume*.7), 1, -1, 1.0f);
        }
        return streamID;
    }

    private void playSampleSound(String sound){
        AudioManager mgr = (AudioManager)this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        if(sampleSoundPool != null){
            sampleSoundPool.play(sampleSoundPoolMap.get(sound), (float)1, (float)1, 1, 0, 1.0f);

        }

    }
    private void highLightSamples() {

        Button button11 = (Button) findViewById(R.id.button11);
        Button button12 = (Button) findViewById(R.id.button12);
        Button button13 = (Button) findViewById(R.id.button13);
        Button button14 = (Button) findViewById(R.id.button14);
        Button button15 = (Button) findViewById(R.id.button15);
        Button button21 = (Button) findViewById(R.id.button21);
        Button button22 = (Button) findViewById(R.id.button22);
        Button button23 = (Button) findViewById(R.id.button23);
        Button button24 = (Button) findViewById(R.id.button24);
        Button button25 = (Button) findViewById(R.id.button25);

        button11.setBackgroundResource(R.drawable.highlighted_sample_button);
        button12.setBackgroundResource(R.drawable.highlighted_sample_button);
        button13.setBackgroundResource(R.drawable.highlighted_sample_button);
        button14.setBackgroundResource(R.drawable.highlighted_sample_button);
        button15.setBackgroundResource(R.drawable.highlighted_sample_button);
        button21.setBackgroundResource(R.drawable.highlighted_sample_button);
        button22.setBackgroundResource(R.drawable.highlighted_sample_button);
        button23.setBackgroundResource(R.drawable.highlighted_sample_button);
        button24.setBackgroundResource(R.drawable.highlighted_sample_button);
        button25.setBackgroundResource(R.drawable.highlighted_sample_button);
    }

    private void unhighLightSamples() {

        Button button11 = (Button) findViewById(R.id.button11);
        Button button12 = (Button) findViewById(R.id.button12);
        Button button13 = (Button) findViewById(R.id.button13);
        Button button14 = (Button) findViewById(R.id.button14);
        Button button15 = (Button) findViewById(R.id.button15);
        Button button21 = (Button) findViewById(R.id.button21);
        Button button22 = (Button) findViewById(R.id.button22);
        Button button23 = (Button) findViewById(R.id.button23);
        Button button24 = (Button) findViewById(R.id.button24);
        Button button25 = (Button) findViewById(R.id.button25);

        button11.setBackgroundResource(R.drawable.samplebutton);
        button12.setBackgroundResource(R.drawable.samplebutton);
        button13.setBackgroundResource(R.drawable.samplebutton);
        button14.setBackgroundResource(R.drawable.samplebutton);
        button15.setBackgroundResource(R.drawable.samplebutton);
        button21.setBackgroundResource(R.drawable.samplebutton);
        button22.setBackgroundResource(R.drawable.samplebutton);
        button23.setBackgroundResource(R.drawable.samplebutton);
        button24.setBackgroundResource(R.drawable.samplebutton);
        button25.setBackgroundResource(R.drawable.samplebutton);

    }

    private void selectSample() {

        final Button button11 = (Button) findViewById(R.id.button11);
        final Button button12 = (Button) findViewById(R.id.button12);
        final Button button13 = (Button) findViewById(R.id.button13);
        final Button button14 = (Button) findViewById(R.id.button14);
        final Button button15 = (Button) findViewById(R.id.button15);
        final Button button21 = (Button) findViewById(R.id.button21);
        final Button button22 = (Button) findViewById(R.id.button22);
        final Button button23 = (Button) findViewById(R.id.button23);
        final Button button24 = (Button) findViewById(R.id.button24);
        final Button button25 = (Button) findViewById(R.id.button25);


        button11.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button11);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });

        button12.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button12);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button13.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button13);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button14.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button14);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button15.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button15);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button21.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button21);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button22.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button22);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button23.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button23);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button24.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button24);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button25.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        unhighLightSamples();
                        sampleDialog(button25);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
    }

    private void loadPreset() {


        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog);
        dialog.setTitle("Choose a preset...");
        dialog.setCancelable(false);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());

        final ListView presetListView = (ListView) dialog.findViewById(R.id.sampleList);

        if (presetList != null) {
            // Defined Array values to show in ListView
            String[] values = new String[presetList.size()];

            for (int i = 0; i < presetList.size(); i++) {
                values[i] = presetList.get(i);
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            presetListView.setAdapter(adapter);

            // ListView Item Click Listener
            presetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item value
                    String[] buttonArray = presetMap.get(presetList.get(position));
                    putSampleArray(buttonArray);

                    dialog.dismiss();
                    samplePlayMode();

                }

            });
        }

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private List<String> getPresetList() {
        FileInputStream fileInputStream;

        try {
            fileInputStream = openFileInput("presetList");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            presetList = (List<String>) objectInputStream.readObject();
            if(presetList == null){
                presetList = new ArrayList<String>();
            }
            objectInputStream.close();
            return presetList;
        } catch (Exception e) {
            e.printStackTrace();
            presetList = new ArrayList<String>();
        }
        return presetList;
    }

    private void savePresetList() {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("presetList", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(presetList);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePreset() {

        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Save Preset As...");

        final EditText presetName = (EditText) dialog.findViewById(R.id.saveName);

        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        final ListView presetListView = (ListView) dialog.findViewById(R.id.sampleList);

        if (presetList != null) {
            // Defined Array values to show in ListView
            String[] values = new String[presetList.size()];

            for (int i = 0; i < presetList.size(); i++) {
                values[i] = presetList.get(i);
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            presetListView.setAdapter(adapter);

            // ListView Item Click Listener
            presetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item value
                    String[] buttonArray = presetMap.get(presetList.get(position));
                    presetName.setText(presetList.get(position));

                }

            });
        }

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (presetMap.get(presetName.getText().toString()) != null) {
                    presetOverwrite(presetName.getText().toString());
                }else {
                    presetMap.put(presetName.getText().toString(), getSampleArray());
                    presetList.add(presetName.getText().toString());
                }

                dialog.dismiss();

            }
        });

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());

    }

    private String[] getCurrSampleArray(){
        FileInputStream fileInputStream;

        try {
            fileInputStream = openFileInput("currSampleArray");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            currSampleArray = (String[]) objectInputStream.readObject();
            objectInputStream.close();
            return currSampleArray;
        } catch (Exception e) {
            e.printStackTrace();
            currSampleArray = getSampleArray();

        }
        return currSampleArray;
    }

    private void saveCurrSampleArray(){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("currSampleArray", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(getSampleArray());
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putSampleArray(String[] sampleArray) {
        final Button button11 = (Button) findViewById(R.id.button11);
        final Button button12 = (Button) findViewById(R.id.button12);
        final Button button13 = (Button) findViewById(R.id.button13);
        final Button button14 = (Button) findViewById(R.id.button14);
        final Button button15 = (Button) findViewById(R.id.button15);
        final Button button21 = (Button) findViewById(R.id.button21);
        final Button button22 = (Button) findViewById(R.id.button22);
        final Button button23 = (Button) findViewById(R.id.button23);
        final Button button24 = (Button) findViewById(R.id.button24);
        final Button button25 = (Button) findViewById(R.id.button25);

        button11.setText(sampleArray[0]);
        button12.setText(sampleArray[1]);
        button13.setText(sampleArray[2]);
        button14.setText(sampleArray[3]);
        button15.setText(sampleArray[4]);
        button21.setText(sampleArray[5]);
        button22.setText(sampleArray[6]);
        button23.setText(sampleArray[7]);
        button24.setText(sampleArray[8]);
        button25.setText(sampleArray[9]);

    }

    private String[] getSampleArray() {

        final Button button11 = (Button) findViewById(R.id.button11);
        final Button button12 = (Button) findViewById(R.id.button12);
        final Button button13 = (Button) findViewById(R.id.button13);
        final Button button14 = (Button) findViewById(R.id.button14);
        final Button button15 = (Button) findViewById(R.id.button15);
        final Button button21 = (Button) findViewById(R.id.button21);
        final Button button22 = (Button) findViewById(R.id.button22);
        final Button button23 = (Button) findViewById(R.id.button23);
        final Button button24 = (Button) findViewById(R.id.button24);
        final Button button25 = (Button) findViewById(R.id.button25);

        String[] sampleArray = new String[10];

        sampleArray[0] = button11.getText().toString();
        sampleArray[1] = button12.getText().toString();
        sampleArray[2] = button13.getText().toString();
        sampleArray[3] = button14.getText().toString();
        sampleArray[4] = button15.getText().toString();
        sampleArray[5] = button21.getText().toString();
        sampleArray[6] = button22.getText().toString();
        sampleArray[7] = button23.getText().toString();
        sampleArray[8] = button24.getText().toString();
        sampleArray[9] = button25.getText().toString();

        return sampleArray;

    }

    private void saveSampleName() {
        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Save Sample As...");

        final EditText sampleName = (EditText) dialog.findViewById(R.id.saveName);

        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        final ListView sampleListView = (ListView) dialog.findViewById(R.id.sampleList);

        if (sampleList != null) {
            // Defined Array values to show in ListView
            String[] values = new String[sampleList.size()];

            for (int i = 0; i < sampleList.size(); i++) {
                values[i] = sampleList.get(i);
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            sampleListView.setAdapter(adapter);

            // ListView Item Click Listener
            sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item value
                    sampleName.setText(sampleList.get(position));

                }

            });
        }

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sampleList.contains(sampleName.getText().toString())){
                    sampleOverwrite(sampleName.getText().toString());
                }else {
                    sampleList.add(sampleName.getText().toString());
                }
                File file = new File(mFileName);
                File newName = new File(mFileNameBase + "/geneskeyboardsamples/" + sampleName.getText().toString() + ".3gp");
                file.renameTo(newName);
                Log.i("File location", mFileNameBase + "/geneskeyboardsamples/" + sampleName.getText().toString() + ".3gp");
                samplePlayMode();
                dialog.dismiss();
            }
        });

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }

    private void sampleDialog(Button button) {

        final Button chosenSample = button;

        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog_removal);
        dialog.setCancelable(false);
        dialog.setTitle("Choose a sample...");

        final ListView sampleListView = (ListView) dialog.findViewById(R.id.sampleList);

        if (sampleList != null) {
            // Defined Array values to show in ListView
            String[] values = new String[sampleList.size()];

            for (int i = 0; i < sampleList.size(); i++) {
                values[i] = sampleList.get(i);
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            sampleListView.setAdapter(adapter);

            // ListView Item Click Listener
            sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item value
                    chosenSample.setText(sampleList.get(position));
                    dialog.dismiss();
                    samplePlayMode();

                }

            });
        }
        Button removeButton = (Button) dialog.findViewById(R.id.removeButton);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenSample.setText("");
                dialog.dismiss();
                samplePlayMode();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                samplePlayMode();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }

    private List<String> getSampleList() {
        FileInputStream fileInputStream;

        try {
            fileInputStream = openFileInput("sampleList");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sampleList = (List<String>) objectInputStream.readObject();
            objectInputStream.close();
            return sampleList;
        } catch (Exception e) {
            e.printStackTrace();
            sampleList = new ArrayList<String>();
        }
        return sampleList;
    }

    private void viewSampleList() {

        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Choose a sample...");


        final ListView sampleListView = (ListView) dialog.findViewById(R.id.sampleList);

        if (sampleList != null) {
            // Defined Array values to show in ListView
            String[] values = new String[sampleList.size()];

            for (int i = 0; i < sampleList.size(); i++) {
                values[i] = sampleList.get(i);
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            sampleListView.setAdapter(adapter);

            // ListView Item Click Listener
            sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    sampleRemove(sampleList.get(position));
                    sampleList.remove(position);

                    dialog.dismiss();

                }

            });
        }



        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }

    private void sampleRemove(String sample){
        currSampleArray = getSampleArray();

        for(int i = 0; i < currSampleArray.length; i++){
            if(currSampleArray[i].equals(sample)){
                currSampleArray[i] = "";
                putSampleArray(currSampleArray);
            }
        }

        for(Map.Entry<String, String[]> entry : presetMap.entrySet()){
            boolean changed = false;
            for(int i = 0; i < entry.getValue().length; i++){
                if(entry.getValue()[i].equals(sample)){
                    entry.getValue()[i] = "";
                    changed = true;
                }
            }
            if(changed){
                presetMap.put(entry.getKey(), entry.getValue());
            }
        }

        File sampleToRemove = new File(mFileNameBase + "/geneskeyboardsamples/" + sample + ".3gp");
        sampleToRemove.delete();
    }
    private void saveSampleList() {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("sampleList", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(sampleList);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void samplePlayMode() {
        final Button button11 = (Button) findViewById(R.id.button11);
        final Button button12 = (Button) findViewById(R.id.button12);
        final Button button13 = (Button) findViewById(R.id.button13);
        final Button button14 = (Button) findViewById(R.id.button14);
        final Button button15 = (Button) findViewById(R.id.button15);
        final Button button21 = (Button) findViewById(R.id.button21);
        final Button button22 = (Button) findViewById(R.id.button22);
        final Button button23 = (Button) findViewById(R.id.button23);
        final Button button24 = (Button) findViewById(R.id.button24);
        final Button button25 = (Button) findViewById(R.id.button25);

        currSampleArray = getSampleArray();

        sampleSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        sampleSoundPoolMap = new HashMap<String, Integer>();
        if(currSampleArray != null) {
            for (int i = 0; i < currSampleArray.length; i++) {
                Log.i("sample array", currSampleArray[i] + "");
                if (!currSampleArray[i].equals("")) {
                    sampleSoundPoolMap.put(currSampleArray[i], sampleSoundPool.load(mFileNameBase + "/geneskeyboardsamples/" + currSampleArray[i] + ".3gp", 1));
                }
            }
        }
        button11.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button11.getText().toString().equals("")) {
                            playSampleSound(button11.getText().toString());
                        }
                        button11.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button11.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });

        button12.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button12.getText().toString().equals("")) {
                            playSampleSound(button12.getText().toString());
                        }
                        button12.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button12.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button13.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button13.getText().toString().equals("")) {
                            playSampleSound(button13.getText().toString());
                        }
                        button13.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button13.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button14.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button14.getText().toString().equals("")) {
                            playSampleSound(button14.getText().toString());
                        }
                        button14.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button14.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button15.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button15.getText().toString().equals("")) {
                            playSampleSound(button15.getText().toString());
                        }
                        button15.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button15.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button21.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button21.getText().toString().equals("")) {
                            playSampleSound(button21.getText().toString());
                        }
                        button21.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button21.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button22.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button22.getText().toString().equals("")) {
                            playSampleSound(button22.getText().toString());
                        }
                        button22.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button22.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button23.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button23.getText().toString().equals("")) {
                            playSampleSound(button23.getText().toString());
                        }
                        button23.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button23.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button24.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button24.getText().toString().equals("")) {
                            playSampleSound(button24.getText().toString());
                        }
                        button24.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button24.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });
        button25.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(!button25.getText().toString().equals("")) {
                            playSampleSound(button25.getText().toString());
                        }
                        button25.setBackgroundResource(R.drawable.highlighted_sample_button);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        button25.setBackgroundResource(R.drawable.samplebutton);
                        return true; // if you want to handle the touch event
                }
                return false;

            }
        });



    }

    private Map<String, String[]> getPresetMap() {
        FileInputStream fileInputStream;

        try {
            fileInputStream = openFileInput("presetMap");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            presetMap = (HashMap<String, String[]>) objectInputStream.readObject();
            if (presetMap == null){
                presetMap = new HashMap<String, String[]>();
            }
            objectInputStream.close();
            return presetMap;
        } catch (Exception e) {
            e.printStackTrace();
            presetMap = new HashMap<String, String[]>();
        }
        return presetMap;
    }

    private void savePresetMap() {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("presetMap", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(presetMap);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void presetOverwrite(final String presetToAdd){

        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.overwrite_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Overwrite preset?");

        final TextView overwriteName = (TextView) dialog.findViewById(R.id.overwriteName);

        overwriteName.setText(presetToAdd);
        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetMap.put(presetToAdd, getSampleArray());
                dialog.dismiss();
            }
        });

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());

    }

    private void sampleOverwrite(final String sampleToAdd){

        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.overwrite_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Overwrite sample?");

        final TextView overwriteName = (TextView) dialog.findViewById(R.id.overwriteName);

        overwriteName.setText(sampleToAdd);
        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // IMMERSIVEMODE FIX
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());

    }

    private void presetCleanup(){

        //check for empty presets
        for(int j = 0; j < presetList.size(); j++){
            String [] presetToCheck = presetMap.get(presetList.get(j));
            boolean clean = true;
            for(int i = 0; i < presetToCheck.length; i++){
                if(!presetToCheck[i].equals("")){
                    clean = false;
                }
            }
            if(clean){
                presetMap.remove(presetList.get(j));
                presetList.remove(j);
                j--;
            }
        }
    }
    @SuppressLint("NewApi")
    private void disableImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN);

        }
    }

    @SuppressLint("NewApi")
    private void enableImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
    }


    /**
     * Set the Immersive mode or not according to its state: enabled or not.
     */
    protected void updateSystemUiVisibility() {
        // Retrieve if the Immersive mode is enabled or not.
        enableImmersiveMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSystemUiVisibility();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        updateSystemUiVisibility();
    }
}
