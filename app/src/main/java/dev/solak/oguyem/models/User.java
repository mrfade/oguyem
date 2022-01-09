package dev.solak.oguyem.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("android_identifier")
    @Expose
    private String androidIdentifier;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAndroidIdentifier() {
        return androidIdentifier;
    }

    public void setAndroidIdentifier(String androidIdentifier) {
        this.androidIdentifier = androidIdentifier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}