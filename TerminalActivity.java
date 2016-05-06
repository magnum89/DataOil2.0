package com.furetech.dataoil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class TerminalActivity extends AppCompatActivity {

    final static private String TAG = "bluetooth";
    final static private int MESSAGE_SIZE = 3;

    BluetoothSPP bt;
    Bundle savedInstanceState;

    //    BluetoothPackage btPackage = new BluetoothPackage();
    private ArrayList<GraphDataPackage> graphData;
    private TextView textStatus, textRead;

    private NumberPicker npDia, npMes, npAnual;

    private ScrollView scrollView1;
    private Menu menu;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "Estoy en onCreate");

        textRead = (TextView)findViewById(R.id.textRead);
        textStatus = (TextView)findViewById(R.id.textStatus);

        npDia = (NumberPicker)findViewById(R.id.numberPickerDay);
        npMes = (NumberPicker)findViewById(R.id.numberPickerMonth);
        npAnual = (NumberPicker)findViewById(R.id.numberPickerYear);

        textStatus.setText(getString(R.string.EstadoNoConectado));
        textStatus.setTextColor(Color.RED);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);

        this.savedInstanceState = savedInstanceState;

        setupNumberPickers();

        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , getString(R.string.BluetoothNoExiste)
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                textRead.append(message + "\n");
                scrollView1.fullScroll(View.FOCUS_DOWN);
                String[] bluetoothData = message.split(",");

                if (bluetoothData.length == MESSAGE_SIZE) {
                    String date = bluetoothData[0];
                    int sensor1 = Integer.valueOf(bluetoothData[1]);
                    int sensor2 = Integer.valueOf(bluetoothData[2]);

                    graphData.add(new GraphDataPackage(sensor1, sensor2, date));

                }
            }
        });

        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i("Check", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("Check", "Auto menu_connection started");
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                textStatus.setText(getString(R.string.EstadoNoConectado));
                textStatus.setTextColor(Color.RED);
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
            }

            public void onDeviceConnectionFailed() {
                textStatus.setText(getString(R.string.EstadoConeccionFallo));
                textStatus.setTextColor(Color.RED);
            }

            public void onDeviceConnected(String name, String address) {
                textStatus.setText(getString(R.string.EstadoConectado) + name);
                textStatus.setTextColor(Color.WHITE);
                menu.clear();
                //TODO
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);
            }
        });



        if (savedInstanceState == null) {
            graphData = new ArrayList<GraphDataPackage>();

            graphData.add(new GraphDataPackage(15, 15,"16:01:12-14/10/2015"));
            graphData.add(new GraphDataPackage(16,"16:02:13-14/10/2015"));
            graphData.add(new GraphDataPackage(11,"16:03:14-14/10/2015"));
            graphData.add(new GraphDataPackage(18,"16:04:15-14/10/2015"));
            graphData.add(new GraphDataPackage(19,"16:05:16-14/10/2015"));
            graphData.add(new GraphDataPackage(11,"16:08:17-14/10/2015"));
            graphData.add(new GraphDataPackage(15,"16:09:18-14/10/2015"));
            graphData.add(new GraphDataPackage(16,"16:10:19-14/10/2015"));
            graphData.add(new GraphDataPackage(11,"16:11:20-14/10/2015"));
            graphData.add(new GraphDataPackage(18,"16:12:21-14/10/2015"));
            graphData.add(new GraphDataPackage(19,"16:13:22-14/10/2015"));
            graphData.add(new GraphDataPackage(11,15,  "16:14:23-14/10/2015"));
            graphData.add(new GraphDataPackage(12,"16:15:24-14/10/2015"));
            graphData.add(new GraphDataPackage(11,"16:16:25-14/10/2015"));
            graphData.add(new GraphDataPackage(11, 15,  "16:17:26-14/10/2015"));
            graphData.add(new GraphDataPackage(18,"16:18:27-14/10/2015"));
            graphData.add(new GraphDataPackage(11,"16:19:28-14/10/2015"));
            graphData.add(new GraphDataPackage(11, "16:20:29-14/10/2015"));

        } else {
            graphData = savedInstanceState.getParcelableArrayList("graphData");
            textRead.setText(savedInstanceState.getString("textReadString"));
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("graphData", graphData);
        savedInstanceState.putString("textReadString", textRead.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setupNumberPickers(){
        npDia.setMinValue(1);
        npDia.setMaxValue(31);
        npDia.setWrapSelectorWheel(false);
        npDia.setValue(1);

        npMes.setMinValue(1);
        npMes.setMaxValue(12);
        npMes.setWrapSelectorWheel(false);
        npMes.setValue(1);

        npAnual.setMinValue(2015);
        npAnual.setMaxValue(2020);
        npAnual.setWrapSelectorWheel(false);
        npAnual.setValue(2015);

        setNumberPickerTextColor(npDia, Color.WHITE);
        setNumberPickerTextColor(npMes, Color.WHITE);
        setNumberPickerTextColor(npAnual, Color.WHITE);
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    ((EditText)child).setTypeface(null, Typeface.BOLD);;
                    numberPicker.invalidate();
                    return true;
                } catch(Exception e){
                    Log.w(TAG, e);
                }
            }
        }
        return false;
    }

    private String toStringNumberPickersMessage(){
        //return "" + npDia.getValue() + npMes.getValue() + npAnual.getValue() + "#";        // int
        return String.format("%02d%02d%d#",npDia.getValue() , npMes.getValue() , npAnual.getValue());
    }

    private String toStringNumberPickersShow(){
        return String.format("%02d/%02d/%d",npDia.getValue() , npMes.getValue() , npAnual.getValue());
    }

    private void clearGraphData(){
        graphData.clear();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if (id == R.id.menu_disconnect) {
            if (bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        } else if (id == R.id.menu_drawgraph){
            if (graphData.size() >= 2 ) {
                Intent intent = new Intent(this, GraphActivity.class);
                intent.putExtra("graphData", graphData);
                intent.putExtra("graphSomethin", graphData);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext()
                        , "No hay suficientes datos"
                        , Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.menu_clean_terminal){
            textRead.setText("");
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                Log.i(TAG, "------------onStart");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            }
        }
    }

    private void setup() {
        FloatingActionButton btnSend = (FloatingActionButton) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO
                //textRead.setTextColor(0xFFFF0F27);
                textRead.append("----------> " + toStringNumberPickersShow() + "\n");
                bt.send(toStringNumberPickersMessage(), false);
                scrollView1.fullScroll(View.FOCUS_DOWN);

                clearGraphData();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , getString(R.string.BluetoothNoFueHabilitado)
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}