package com.example.aidl_service.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.aidl_service.Adapter.BluetoothDevicesAdapter;
import com.example.aidl_service.Adapter.ScanDeviceListAdapter;
import com.example.aidl_service.Device.OCR.TextScanner.GlucoMeterScanner;
import com.example.aidl_service.Device.OCR.TextScanner.SpO2Scanner;
import com.example.aidl_service.Model.ScannerDeviceModel;
import com.example.aidl_service.Device.Bluetooth.BPDrTrust1;
import com.example.aidl_service.Device.CommonDataArea;
import com.example.aidl_service.R;
import com.example.aidl_service.Device.Bluetooth.SPO2ControlD;
import com.example.aidl_service.Utils.Utils;
import com.example.aidl_service.databinding.ActivityBluetoothBinding;


import java.util.ArrayList;
import java.util.List;


public class BluetoothActivity extends AppCompatActivity {

    ActivityBluetoothBinding activityMainBinding;
    private BluetoothAdapter bluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Utils.requestPermissions(this);
        }else{
                Utils.requestPermissions(this);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        activityMainBinding.bleIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, 1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            return;
                        }
                    }
                    showBtConnectPopUp();
                }else {
                    showBtConnectPopUp();
                }
            }
        });

        activityMainBinding.scanIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanConnectPopUp();
            }
        });


    }



    private AlertDialog showScanConnectPopUp() {
        List<ScannerDeviceModel> deviceModelList = new ArrayList<>();
        deviceModelList.add(new ScannerDeviceModel(R.drawable.pulse_oximeter64, "Pulse Oximeter","(Control D)"));
        deviceModelList.add(new ScannerDeviceModel(R.drawable.glucometer64, "Blood Glucometer","(Contour plus ELITE)"));
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetooth_list, null);
        final ImageView refresh = view.findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ListView devicesListView = (ListView) view.findViewById(R.id.devices_list_view);

        ScanDeviceListAdapter scanDeviceListAdapter = new ScanDeviceListAdapter(getApplicationContext(),deviceModelList);
        devicesListView.setAdapter(scanDeviceListAdapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.parseColor("#E6F0FC"));
                dialog.dismiss();
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(BluetoothActivity.this, SpO2Scanner.class)
                                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        break;
                    case 1:
                        intent = new Intent(BluetoothActivity.this, GlucoMeterScanner.class)
                                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
                BluetoothActivity.this.startActivity(intent);}
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        return dialog;
    }

    @SuppressLint("MissingPermission")
    public AlertDialog showBtConnectPopUp() {

        final BluetoothDevicesAdapter bluetoothDevicesAdapter = new BluetoothDevicesAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetooth_list, null);
        final ImageView refresh = view.findViewById(R.id.refresh);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ListView devicesListView = (ListView) view.findViewById(R.id.devices_list_view);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        devicesListView.setAdapter(bluetoothDevicesAdapter);
        devicesListView.setEmptyView(emptyText);

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (bluetoothDevicesAdapter.getPosition(device) == -1) {
                            // -1 is returned when the item is not in the adapter
                            String devName = device.getName();
                            if (devName != null)
                            if (devName.startsWith(CommonDataArea.SUPPORTED_DEVICES_BP1) || devName.startsWith(CommonDataArea.SUPPORTED_DEVICES_SPO21) || devName.startsWith("JVH")) {
                                bluetoothDevicesAdapter.add(device);
                                bluetoothDevicesAdapter.notifyDataSetChanged();
                            }
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        try {
                            Glide.with(context).load((Bitmap) null).into(refresh);
                            refresh.setImageResource(R.drawable.whitebluettothnew);
                            context.unregisterReceiver(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                break;
                        }
                    }
                }catch (Exception exp){
                   // LogWriter.writeLog("Bluetooth scan","Exception->"+exp.getMessage());
                }
            }
        };


        if (bluetoothDevicesAdapter == null) {
           // Log.e(Constants.TAG, "Device has no bluetooth");
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("No Bluetooth")
                    .setMessage("Your device has no bluetooth")
                    .setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }


        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice devicee = bluetoothDevicesAdapter.getItem(position);
                bluetoothAdapter.cancelDiscovery();
                view.setBackgroundColor(Color.parseColor("#E6F0FC"));
                if ((devicee.getName() != null) && devicee.getName().contains(CommonDataArea.SUPPORTED_DEVICES_BP1)) {
                    CommonDataArea.bpDrTrust1 = new BPDrTrust1(BluetoothActivity.this);
                    CommonDataArea.bpDrTrust1.connectDevice(devicee.getAddress());
                } else if ((devicee.getName() != null) && devicee.getName().contains(CommonDataArea.SUPPORTED_DEVICES_SPO21)) {
                    CommonDataArea.spO2ControlD = new SPO2ControlD(BluetoothActivity.this);
                    CommonDataArea.spO2ControlD.connectDevice(devicee.getAddress());
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }}
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothDevicesAdapter.clear();
                    bluetoothDevicesAdapter.notifyDataSetChanged();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    getApplicationContext().registerReceiver(mReceiver, filter);
                    bluetoothAdapter.startDiscovery();
                    Glide.with(BluetoothActivity.this).load(R.drawable.whitebluettothnew).into(refresh);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        dialog.setCancelable(true);
        if (bluetoothAdapter.isEnabled()) {
            Log.e("startearch", "true");
            bluetoothDevicesAdapter.clear();
            bluetoothDevicesAdapter.notifyDataSetChanged();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            this.registerReceiver(mReceiver, filter);
            if(!bluetoothAdapter.startDiscovery()){
               // LogWriter.writeLog("Bluetooth", " Failed to start discovery");
            }
            Glide.with(this).load(R.drawable.whitebluettothnew).into(refresh);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }

        return dialog;
    }

}
