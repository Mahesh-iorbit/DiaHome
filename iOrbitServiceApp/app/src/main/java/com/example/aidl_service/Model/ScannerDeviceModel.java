package com.example.aidl_service.Model;

public class ScannerDeviceModel {
    private int imageId;
    private String name;
    private String address;

    public ScannerDeviceModel(int imageId, String name, String address) {
        this.imageId = imageId;
        this.name = name;
        this.address = address;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
