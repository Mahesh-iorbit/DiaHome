package com.example.aidl_service;


import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;



public class MyService extends Service {
    private Handler handler;
    private AlertDialog alertDialog;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        handler = new Handler(Looper.getMainLooper());
        // Check if the app has permission to draw overlays
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // If the app does not have permission, request it
            Intent overlay = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(overlay);
        } else {
            // The app already has permission, handle accordingly
            showToast("The app already has overlay permission");
        }
        return AidlBinder;
    }

    private final aidlInterface.Stub AidlBinder=new aidlInterface.Stub() {

        @Override
        public void showToast(String message) throws RemoteException {
            MyService.this.showToast(message);

        }

        @Override
        public void showUI(String message) throws RemoteException {

            MyService.this.showUi(message);

        }

        @Override
        public void requestPermission() throws RemoteException {

        }


    };



    private void showUi(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                alertDialog = new AlertDialog.Builder(getApplicationContext())
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MyService.this, "Hello cilent I am from Server", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                alertDialog.show();
            }
        });
    }

    private void showToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }


}
