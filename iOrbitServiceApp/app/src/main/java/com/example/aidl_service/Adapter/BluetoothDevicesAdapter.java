package com.example.aidl_service.Adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.aidl_service.R;

public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDevicesAdapter(Context context) {
        super(context, 0);
    }
    // View lookup cache
    static class ViewHolder {

        TextView name;

        TextView address;
        TextView assignee;



        public ViewHolder(View view) {
            address = (TextView) view.findViewById(R.id.device_address);
            name = (TextView) view.findViewById(R.id.device_name);
            assignee = (TextView) view.findViewById(R.id.assigned_to);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_device, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object

        if (device.getName().equals("Mike")) {
            viewHolder.name.setText("Pulse Oximeter");
        } else {
            viewHolder.name.setText(device.getName());
        }

        //  viewHolder.name.setText(device.getName());
        viewHolder.address.setText(device.getAddress());
        // Return the completed to render on screen
        return convertView;
    }

}
