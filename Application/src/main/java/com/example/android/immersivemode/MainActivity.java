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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons.
 */
public class MainActivity extends SampleActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button assignSample = (Button) findViewById(R.id.assign);
        Button savePreset = (Button) findViewById(R.id.save);
        Button recordSample = (Button) findViewById(R.id.recordButton);
        Button editSample = (Button) findViewById(R.id.edit);

        samplePlayMode();

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
            @Override
            public void onClick(View view) {
                saveSampleName();
            }
        });

        editSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSampleList();
            }
        });
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

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button11);
                samplePlayMode();
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button12);
                samplePlayMode();
            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button13);
                samplePlayMode();
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button14);
                samplePlayMode();
            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button15);
                samplePlayMode();

            }
        });
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button21);
                samplePlayMode();

            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button22);
                samplePlayMode();

            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button23);
                samplePlayMode();

            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button24);
                samplePlayMode();

            }
        });
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unhighLightSamples();
                sampleDialog(button25);
                samplePlayMode();

            }
        });
    }

    private void savePreset(){

        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_preset_dialog);
        dialog.setTitle("Save Preset As...");

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);

        final EditText presetName = (EditText) dialog.findViewById(R.id.presetName);


        //
        //
        // image.setImageResource(R.drawable.ic_launcher);

        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetName.getText().toString();
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


        //Here's the magic..
        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());

        //Show the dialog! (Hopefully no soft navigation...)
        dialog.show();

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Update the WindowManager with the new attributes (no nicer way I know of to do this)..
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());

    }

    private void saveSampleName(){
        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.record_sample_dialog);
        dialog.setTitle("Save Sample As...");

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);

        final EditText sampleName = (EditText) dialog.findViewById(R.id.presetName);


        //
        //
        // image.setImageResource(R.drawable.ic_launcher);

        Button acceptButton = (Button) dialog.findViewById(R.id.acceptButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> sampleList = getSampleList();
                sampleList.add(sampleName.getText().toString());
                saveSampleList(sampleList);
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


        //Here's the magic..
        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());

        //Show the dialog! (Hopefully no soft navigation...)
        dialog.show();

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Update the WindowManager with the new attributes (no nicer way I know of to do this)..
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }
    private void sampleDialog(Button button){

        final Button chosenSample = button;

        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog);
        dialog.setTitle("Choose a sample...");

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        final ListView sampleListView = (ListView) dialog.findViewById(R.id.sampleList);

        final List<String> sampleList = getSampleList();
        if(sampleList != null){
            // Defined Array values to show in ListView
            String[] values = new String[sampleList.size()];

            for(int i = 0; i < sampleList.size(); i++ ){
                values[i] =  sampleList.get(i);
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

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    chosenSample.setText(sampleList.get(position));
                    dialog.dismiss();
                    //Toast.makeText(eventChosen, Toast.LENGTH_LONG);

                }

            });
        }


        //
        //
        // image.setImageResource(R.drawable.ic_launcher);


        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //Here's the magic..
        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());

        //Show the dialog! (Hopefully no soft navigation...)
        dialog.show();

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Update the WindowManager with the new attributes (no nicer way I know of to do this)..
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }

    private List<String> getSampleList(){
        FileInputStream fileInputStream;

        List<String> sampleList = new ArrayList<>();
        try {
            fileInputStream = openFileInput("sampleList");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sampleList = (List<String>) objectInputStream.readObject();
            objectInputStream.close();
            return sampleList;
        } catch (Exception e){
            e.printStackTrace();
        }
        return sampleList;
    }

    private void viewSampleList(){

        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog);
        dialog.setTitle("Choose a sample...");

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        final ListView sampleListView = (ListView) dialog.findViewById(R.id.sampleList);

        final List<String> sampleList = getSampleList();
        if(sampleList != null){
            // Defined Array values to show in ListView
            String[] values = new String[sampleList.size()];

            for(int i = 0; i < sampleList.size(); i++ ){
                values[i] =  sampleList.get(i);
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

                    sampleList.remove(position);
                    saveSampleList(sampleList);
                    dialog.dismiss();
                    //Toast.makeText(eventChosen, Toast.LENGTH_LONG);

                }

            });
        }


        //
        //
        // image.setImageResource(R.drawable.ic_launcher);


        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //Here's the magic..
        //Set the dialog to not focusable (makes navigation ignore us adding the window)
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                context.getWindow().getDecorView().getSystemUiVisibility());

        //Show the dialog! (Hopefully no soft navigation...)
        dialog.show();

        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        //Update the WindowManager with the new attributes (no nicer way I know of to do this)..
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(getWindow().getDecorView(), getWindow().getAttributes());
    }

    private void saveSampleList(List<String> sampleList){
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("sampleList", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(sampleList);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private void samplePlayMode(){
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

        final Context context = getApplicationContext();
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, button11.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button12.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button13.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button14.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button15.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button21.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(context, button22.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button23.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button24.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, button25.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        updateSystemUiVisibility();
    }
}
