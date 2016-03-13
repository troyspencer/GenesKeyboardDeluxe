/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.immersivemode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import java.util.List;
import java.util.Map;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    public static final String FRAGTAG = "ImmersiveModeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button assignSample = (Button) findViewById(R.id.assign);
        Button savePreset = (Button) findViewById(R.id.save);

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


        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        logFragment.getLogView().setTextAppearance(this, R.style.Log);
        logFragment.getLogView().setBackgroundColor(Color.WHITE);


        Log.i(TAG, "Ready");
    }

    private void highLightSamples(){

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

    private void unhighLightSamples(){

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

    private void selectSample(){

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

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();

            }
        });
    }

    private void savePreset(){

        // Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);


        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(input)
                .setTitle("Save preset as...")
                .setMessage("")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        input.getText().toString();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();



    }
}
