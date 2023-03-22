package com.example.aidl_client;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.aidl_service.aidlInterface;

import java.util.List;

public class MainActivity extends Activity{
    private Context mContext;
    private Button ShowToast,ShowAlert,TakePermission;
    public aidlInterface aidlInterfaceObject;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the UI
        mContext=this;

        TakePermission = findViewById(R.id.take_permission);

        bindAIDLService();


        TakePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.example.aidl_service", "com.example.aidl_service.ui.BluetoothActivity"));
                startActivity(intent);
            }
        });

    }


    ServiceConnection serviceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            aidlInterfaceObject=aidlInterface.Stub.asInterface( (IBinder) iBinder );

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void bindAIDLService() {
        //Try catch block is not added during video implementation
        try{
            Intent settingsIntent = new Intent("com.example.aidl_service");
            bindService(convertImplicitIntentToExplicitIntent(settingsIntent, mContext), serviceConnection, BIND_AUTO_CREATE);
        }catch (Exception e)
        {
            Toast.makeText(mContext, "Service App may not be present", Toast.LENGTH_SHORT).show();
            Log.e("AIDL_ERROR","EXCEPTION CAUGHT: "+e.toString());
            finish();
        }
    }




    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices( implicitIntent, 0);
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get( 0 );
        ComponentName component = new ComponentName( serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name );
        Intent explicitIntent = new Intent( implicitIntent );
        explicitIntent.setComponent( component );
        return explicitIntent;
    }





}
