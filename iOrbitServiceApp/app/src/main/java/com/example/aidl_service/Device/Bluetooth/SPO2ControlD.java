package com.example.aidl_service.Device.Bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.example.aidl_service.Model.SaveMeasureModel;
import com.example.aidl_service.Model.StatusResponseModel;
import com.example.aidl_service.Network.RetrofitClient;
import com.example.aidl_service.Network.ServiceApi;
import com.example.aidl_service.Utils.StatusDialog;


import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SPO2ControlD {

    private BluetoothGatt mGatt;
    private BluetoothGattService mBPService;
    private BluetoothGattCharacteristic mBPDevNotify;
    private BluetoothGattCharacteristic mBPDeviceWrite;
    private Context mContext;
    public static final UUID UUID_SERVICE_DATA = UUID.fromString((String) "49535343-FE7D-4AE5-8FA9-9FAFD205E455");
    public static final UUID UUID_CHARACTER_RECEIVE = UUID.fromString((String) "49535343-1E4D-4BD9-BA61-23C647249616");
    // public static final UUID UUID_MODIFY_BT_NAME=UUID.fromString((String)"00005343-0000-1000-8000.00805F9B34FB");
    public static final UUID UUID_CLIENT_CHARACTER_CONFIG = UUID.fromString((String) "00002902-0000-1000-8000-00805f9b34fb");
    private String patientId = "1aac7dde-8de2-11ec-b1ec-6302cf60bd3c";
    private String patientId2 = "b1449ca6-a1f0-11ed-97a5-110533cdf146";
    private String PatientSsid = "b7e0602";
    private int val1;
    private int spo2;

    private int pulse;

    boolean discOnnected = false;

    public SPO2ControlD(Context context) {
        mContext = context;
    }

    @SuppressLint("MissingPermission")
    public void connectDevice(String deviceName) {
        try {

            BluetoothManager btManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter btAdapter = btManager.getAdapter();
            BluetoothDevice device = btAdapter.getRemoteDevice(deviceName);
            mGatt = device.connectGatt(mContext, true, mGattCallback);
            //  statusDialog = new StatusDialog(mContext,"Measurement Atatus","Connecting..");
            StatusDialog.showDialogMessage(mContext,"Measurement Status", "Connecting..");
            StatusDialog.setOnNegativeButtonClickedListener("Cancel", new StatusDialog.OnNegativeButtonClickedListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onNegativeButtonClicked() {
                    mGatt.disconnect();
                    mGatt.close();
                    discOnnected = true;
                }
            });
            StatusDialog.setOnPositiveButtonClickedListener("Close", new StatusDialog.OnPositiveButtonClickedListener() {
                @Override
                public void onPositiveButtonClicked() {
                    mGatt.disconnect();
                    mGatt.close();
                    discOnnected = true;
                    Toast.makeText(mContext,"Nothing to save, Try after completing the measurement",Toast.LENGTH_LONG ).show();
                }
            });
            Log.i("SPO2BLE", "Connecting...");
        } catch (Exception exp) {
            Log.i("BLE Exception", exp.getMessage());
        }
    }

    private void saveMeasurement() {
        int[] intVal = {spo2, pulse};
        String[] BpName = {"oxygen_level", "BPM"};
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(mContext);
        if (retrofitClient == null) {
            return;
        }

        for (int i = 0; i < intVal.length; i++) {
            SaveMeasureModel measure = new SaveMeasureModel();
            measure.setParamName(BpName[i]);
            measure.setParamFraction("");
            measure.setDevmodelId("2ab90e73-99c5-11eb-853f-e9af88721123");
            measure.setDevId("852a2034-c8dd-11eb-a396-755a8569ff4d");
            measure.setIntVal(String.valueOf(intVal[i]));
            measure.setPatientId("1aa0001");
            Call<StatusResponseModel> call = retrofitClient.create(ServiceApi.class).SaveMeasure("1aa0001",measure);
            call.enqueue(new Callback<StatusResponseModel>() {
                @Override
                public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                    if(response.isSuccessful()) {
                        StatusResponseModel saveMeasureModel = new StatusResponseModel();
                        saveMeasureModel = response.body();
                        if(saveMeasureModel.getStatus().getMessage().equalsIgnoreCase("Success")){
                            Toast.makeText(mContext, saveMeasureModel.getStatus().getDetails(), Toast.LENGTH_SHORT).show();


                            StatusDialog.close();
                        }else {
                            Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<StatusResponseModel> call, Throwable t) {
                    Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("SPO2BLE", "Connected..");
                //Utils.showSnackbar(null, "Connected", com.sensesemi.hospital.Bluetooth.Constants.HIGH, Snackbar.LENGTH_LONG);
                StatusDialog.setMessage("Connected");

                mGatt.discoverServices();
                Log.i("SPO2BLE", "Discovering..");
            }
        }

        void enableCharacteristic(BluetoothGatt gatt) {
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                if (!bluetoothGattService.getUuid().equals((Object) UUID_SERVICE_DATA)) continue;
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    if (!bluetoothGattCharacteristic.getUuid().equals((Object) UUID_CHARACTER_RECEIVE))
                        continue;
                    setCharacteristicNotification(gatt, bluetoothGattCharacteristic, true);
                }
            }
        }

        @SuppressLint("MissingPermission")
        public void setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean bl) {
            BluetoothGatt bluetoothGatt;
            if (gatt != null) {
                gatt.setCharacteristicNotification(bluetoothGattCharacteristic, bl);
                if (UUID_CHARACTER_RECEIVE.equals((Object) bluetoothGattCharacteristic.getUuid())) {
                    BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID_CLIENT_CHARACTER_CONFIG);
                    if (bl) {
                        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    } else {
                        bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                    gatt.writeDescriptor(bluetoothGattDescriptor);
                }
                Log.w((String) "SPO2BLE", (String) "Chareteristic enabled");

            }

        }

        class MyRunnable implements Runnable {
            private BluetoothGatt gatt;

            public MyRunnable(BluetoothGatt gatt) {
                this.gatt = gatt;
            }

            @Override
            public void run() {
                // Code to be executed, using the myArgument variable
                enableCharacteristic(gatt);
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                try {
                    Log.i("SPO2BLE", "Service Discovered..");

                    StatusDialog.setMessage("Enabling services");
                    //statusDialog.setText("Discovering...");
                    enableCharacteristic(gatt);
                    //showSnackBar("Loading...");
                    //showDialogMessage("Connected","Measurement Loading...");

                } catch (Exception exp) {
                    Log.i("SPO2BLE", exp.getMessage());
                }

            }
        }

        int updateCouner = 0;

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("SPO2BLE", "data..");
            if (discOnnected) {
                gatt.disconnect();
                gatt.close();
            }
            byte[] data = characteristic.getValue();
            if (data != null && data[2] != 127 && data[3] != 127 && data[4] != 127 && data.length > 0) {
                Log.i("SPO2BLE", "SPO2 Values ->" + val1 + "-" + pulse + "-" + spo2);
                if (updateCouner % 10 == 0) if (val1 < 80) {
                    val1 = data[2];
                    pulse = data[3];
                    spo2 = data[4];
                    StatusDialog.setMessage("Oxygen Level: " + spo2 + " Pulse: " + pulse);
                    StatusDialog.setOnPositiveButtonClickedListener("Save", new StatusDialog.OnPositiveButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            gatt.disconnect();
                            gatt.close();
                            discOnnected = true;
                            saveMeasurement();
                        }
                    });

                }

                ++updateCouner;


            }
        }


    };
}

