package com.example.aidl_service.Device;


import com.example.aidl_service.Device.Bluetooth.BPDrTrust1;
import com.example.aidl_service.Device.Bluetooth.SPO2ControlD;

public class CommonDataArea {
    public static String SUPPORTED_DEVICES_SPO21="Mike";
    public static String SUPPORTED_DEVICES_BP1="BP2941";

    public static BPDrTrust1 bpDrTrust1=null;
    public static SPO2ControlD spO2ControlD = null;

    public  static String SUPPORTED_PATTERN_GlUCOMETER="Contour plus ELITE\\s+\\S+\\s+\\S+\\s+((3[1-9]|[4-9]\\d|\\d{3})\\.?\\d*)";
    public  static String SUPPORTED_PATTERN_PULSEOXIMETER ="PULSE OXIMETER\\s+([1-9]|[1-9][0-9])\\s+([2-9]\\d+|[1-9]\\d{2,})";

}
