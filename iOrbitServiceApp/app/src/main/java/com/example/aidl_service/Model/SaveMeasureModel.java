package com.example.aidl_service.Model;

import com.google.gson.annotations.SerializedName;

public class SaveMeasureModel {

    private String paramName;
    private String paramFraction;
    private String devmodelId;
    private String devId;
    private String intVal;
    private String patientId;
    private String message;
    private String code;
    private String details;


    public SaveMeasureModel() {

    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamFraction() {
        return paramFraction;
    }

    public void setParamFraction(String paramFraction) {
        this.paramFraction = paramFraction;
    }

    public String getDevmodelId() {
        return devmodelId;
    }

    public void setDevmodelId(String devmodelId) {
        this.devmodelId = devmodelId;
    }

    public String getIntVal() {
        return intVal;
    }

    public void setIntVal(String intVal) {
        this.intVal = intVal;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
}
