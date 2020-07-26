package com.unc0ded.shopdeliver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("credentials")
    @Expose
    private Credentials credentials;
    @SerializedName("name")
    @Expose
    private Name name;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) { this.name = name; }

}
