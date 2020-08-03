package com.unc0ded.shopdeliver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("quantity")
    @Expose
    private Long quantity;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vendorId")
    @Expose
    private String vendorId;
    @SerializedName("newLabel")
    @Expose
    private Boolean newLabel;
    @SerializedName("popularLabel")
    @Expose
    private Boolean popularLabel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Boolean getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(Boolean newLabel) {
        this.newLabel = newLabel;
    }

    public Boolean getPopularLabel() {
        return popularLabel;
    }

    public void setPopularLabel(Boolean popularLabel) {
        this.popularLabel = popularLabel;
    }
}
