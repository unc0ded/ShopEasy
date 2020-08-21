package com.unc0ded.shopdeliver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vendor {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("shopName")
    @Expose
    private String shopName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("proprietor")
    @Expose
    private Proprietor proprietor;

    public Vendor() {
        address = new Address();
        proprietor = new Proprietor();
    }

    public String getId() {
        return id;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Address getAddress(){
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Proprietor getProprietor() {
        return proprietor;
    }

    public void setProprietor(Proprietor proprietor) {
        this.proprietor = proprietor;
    }
}


