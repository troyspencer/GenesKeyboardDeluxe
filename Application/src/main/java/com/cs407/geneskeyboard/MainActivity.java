package com.cs407.geneskeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private Map<String, String[]> presetMap;
    private List<String> presetList;
    private String[] currSampleArray;
    private List<String> sampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout keyboard = (RelativeLayout) findViewById(R.id.keyboard);
        Button blackkey1 = (Button) findViewById(R.id.blackkey1);
        Button blackkey2 = (Button) findViewById(R.id.blackkey2);
        Button blackkey3 = (Button) findViewById(R.id.blackkey3);
        Button blackkey4 = (Button) findViewById(R.id.blackkey4);
        Button blackkey5 = (Button) findViewById(R.id.blackkey5);
        Button blackkey6 = (Button) findViewById(R.id.blackkey6);
        Button blackkey7 = (Button) findViewById(R.id.blackkey7);

        blackkey1.bringToFront();
        blackkey2.bringToFront();
        blackkey3.bringToFront();
        blackkey4.bringToFront();
        blackkey5.bringToFront();
        blackkey6.bringToFront();
        blackkey7.bringToFront();

        keyboard.invalidate();

        getPresetMap();
        getPresetList();
        getSampleList();
        getCurrSampleArray();
        putSampleArray(currSampleArray);

        Button assignSample = (Button) findViewById(R.id.assign);
        Button savePreset = (Button) findViewById(R.id.save);
        Button recordSample = (Button) findViewById(R.id.recordButton);
        Button editSample = (Button) findViewById(R.id.edit);
        Button loadPreset = (Button) findViewById(R.id.load);

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

    @Override
    protected void onStop() {
        super.onStop();

        saveCurrSampleArray();
        savePresetList();
        savePresetMap();
        saveSampleList();

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

    private void loadPreset() {


        ArrayAdapter<String> adapter;
        Activity context = MainActivity.this;

        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.choose_sample_dialog);
        dialog.setTitle("Choose a preset...");
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

        String[] sampleArray = new String[12];

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
        dialog.setContentView(R.layout.choose_sample_dialog);
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

    private Map<String, String[]> getPresetMap() {
        FileInputStream fileInputStream;

        try {
            fileInputStream = openFileInput("presetMap");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            presetMap = (HashMap<String, String[]>) objectInputStream.readObject();
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
