package com.example.aidl_service.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;

import com.example.aidl_service.R;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    static Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestPermissions(Context context) {
        List<String> permissionToRequest = new ArrayList<>();
        if (!hasReadExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!hasWriteExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasManageExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        if (!hasBluetoothPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH);
        if (!hasBluetoothAdminPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (!hasAccessFineLocationPermission(context))
            permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!hasBluetoothScanPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
        if (!hasBluetoothConnectPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
        if (!hasBluetoothPrivilegedPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_PRIVILEGED);
        if (!hasCameraPermission(context))
            permissionToRequest.add(Manifest.permission.CAMERA);
        if (!permissionToRequest.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissionToRequest.toArray(new String[0]), 0);
        }
    }

    private static boolean hasReadExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasWriteExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasManageExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothAdminPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasAccessFineLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothScanPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothConnectPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothPrivilegedPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_PRIVILEGED) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasCameraPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    public static void showLoaderDialog(Context context){
        dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_loader_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public static void closeLoaderDialog(){
        dialog.dismiss();

    }

    public static void showSuccess(Context mContext, String msg, String title) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.Widget_AppCompat_ButtonBar_AlertDialog));
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                // Dismiss the dialog when the "OK" button is clicked
                dialogInterface.dismiss();
            });
            builder.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }
}
