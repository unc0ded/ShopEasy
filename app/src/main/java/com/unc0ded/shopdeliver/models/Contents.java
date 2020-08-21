
package com.unc0ded.shopdeliver.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contents {

    @SerializedName("quantifier")
    @Expose
    private String quantifier;
    @SerializedName("value")
    @Expose
    private Integer value;

    public String getQuantifier() {
        return quantifier;
    }

    public void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
