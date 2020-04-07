package com.unc0ded.shopdeliver.model;

public class Vendor {
    String Name;
    String Type;
    String Address;

    public Vendor(String name, String type, String address) {
        Name = name;
        Type = type;
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}


