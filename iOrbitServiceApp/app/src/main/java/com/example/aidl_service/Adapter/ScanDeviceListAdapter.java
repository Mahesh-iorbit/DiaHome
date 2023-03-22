package com.example.aidl_service.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aidl_service.Model.ScannerDeviceModel;
import com.example.aidl_service.R;


import java.util.List;

public class ScanDeviceListAdapter extends BaseAdapter {
    private Context context;
    private List<ScannerDeviceModel> myList;

    public ScanDeviceListAdapter(Context context, List<ScannerDeviceModel> myList) {
        this.context = context;
        this.myList = myList;
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int i) {
        return myList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_device, viewGroup, false);
        }

        ScannerDeviceModel scanner = myList.get(i);

        ImageView image = view.findViewById(R.id.scan_device);
        image.setImageResource(scanner.getImageId());

        TextView name = view.findViewById(R.id.device_name);
        name.setText(scanner.getName());

        TextView address = view.findViewById(R.id.device_address);
        address.setText(scanner.getAddress());

        return view;
    }
}
