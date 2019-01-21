package com.example.vmann.mapbox;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {


    //Create a list layout and ArrayList to list all of the bluetooth devices within a proximity
    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices) {
        //create layout inflater and the tvResourceId
        super(context, tvResourceId, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    //method to get the view with all of the
    public View getView(int position, View convertView, ViewGroup parent) {
        //switch the layout inflateer to a null value to empty the list
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        //find where the connected bluetooth device is
        BluetoothDevice device = mDevices.get(position);

        //find the bluetooth device by id
        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = convertView.findViewById(R.id.tvDeviceAddress);
            //find name of bluetooth device
            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            //get the address corresponding to the bluetooth device to establish connection
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }
        //return the full list of devices with all of the data
        return convertView;
    }

}