package com.unc0ded.shopdeliver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Pin code")
    @Expose
    private String pinCode;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Address Line 1")
    @Expose
    private String addressLine1;
    @SerializedName("Address Line 2")
    @Expose
    private String addressLine2;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

}
