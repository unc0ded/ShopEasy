package com.unc0ded.shopdeliver.models;

public class Customer {
    String Name, SocietyName, Flat, Locality, Phone, Email;

    public Customer(String name, String societyName, String flat, String locality, String phone, String email) {
        Name = name;
        SocietyName = societyName;
        Flat = flat;
        Locality = locality;
        Phone = phone;
        Email = email;
    }

    public Customer(String name, String societyName, String flat, String locality, String phone) {
        Name = name;
        SocietyName = societyName;
        Flat = flat;
        Locality = locality;
        Phone = phone;
    }

    public Customer() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSocietyName() {
        return SocietyName;
    }

    public void setSocietyName(String societyName) {
        SocietyName = societyName;
    }

    public String getFlat() {
        return Flat;
    }

    public void setFlat(String flat) {
        Flat = flat;
    }

    public String getLocality() {
        return Locality;
    }

    public void setLocality(String locality) {
        Locality = locality;
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
}
