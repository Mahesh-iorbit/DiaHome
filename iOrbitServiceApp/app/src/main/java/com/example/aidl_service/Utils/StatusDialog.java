package com.example.aidl_service.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class StatusDialog {
    static AlertDialog userDialog = null;
    static AlertDialog.Builder builder=null;
    private static OnPositiveButtonClickedListener listener;
    private static OnNegativeButtonClickedListener listenerNegative;


    public static void showDialogMessage(Context context, String title, String body) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(body)
                .setPositiveButton("Save", new PositiveButtonHandler())
                .setNegativeButton("Cancel", new NegativeButtonHandler());

        userDialog = builder.create();
        userDialog.setCanceledOnTouchOutside(false);
        userDialog.show();
    }
    /*public static void setPositiveButtonHandler(String label, DialogInterface.OnClickListener handler) {
        builder.setPositiveButton(label, handler);
    }*/
    public static void setMessage(String message) {
        userDialog.setMessage(message);

    }

    public static void close(){
        userDialog.dismiss();
    }

    public static void setOnPositiveButtonClickedListener(String label,OnPositiveButtonClickedListener listener) {
        StatusDialog.listener = listener;
        builder.setPositiveButton(label,new PositiveButtonHandler());
    }
    public static void setOnNegativeButtonClickedListener(String label,OnNegativeButtonClickedListener listener) {
        StatusDialog.listenerNegative = listener;
        builder.setNegativeButton(label,new PositiveButtonHandler());
    }
    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked();
    }

    public interface OnNegativeButtonClickedListener {
        void onNegativeButtonClicked();
    }

    static class PositiveButtonHandler implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                //userDialog.dismiss();
                if (listener != null) {
                    listener.onPositiveButtonClicked();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class NegativeButtonHandler implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                //userDialog.dismiss();
                if (listenerNegative != null) {
                    listenerNegative.onNegativeButtonClicked();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

