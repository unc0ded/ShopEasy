package com.unc0ded.shopdeliver.models;

public class Vendor {
    String ShopName,Type, PropName, Phone, Email, Address;

    public Vendor(String shopName, String type, String propName, String phone, String email, String address) {
        ShopName = shopName;
        Type = type;
        PropName = propName;
        Phone = phone;
        Email = email;
        Address = address;
    }

    public Vendor(String shopName, String type, String propName, String phone, String address) {
        ShopName = shopName;
        Type = type;
        PropName = propName;
        Phone = phone;
        Address = address;
    }

    public Vendor() {
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPropName() {
        return PropName;
    }

    public void setPropName(String propName) {
        PropName = propName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}


