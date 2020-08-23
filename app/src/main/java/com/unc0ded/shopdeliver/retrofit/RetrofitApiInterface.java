package com.unc0ded.shopdeliver.retrofit;

import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.models.Customer;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RetrofitApiInterface {

    @POST("users/register/{type}")
    Call<JsonObject> getOtp(@Path("type") String type, @Body Map<String, Object> body);

    @POST("users/verify")
    Call<JsonObject> verifyOtp(@Body Map<String, Object> body);

    @POST("users/login/phone")
    Call<JsonObject> getLoginOtp(@Body Map<String, Object> body);

    @POST("users/login/verify")
    Call<JsonObject> verifyLoginOtp(@Body Map<String, Object> body);

    @POST("/customers")
    Call<Customer> createCustomer(@Body Customer newCustomer);

    @POST("/vendors")
    Call<Vendor> createVendor(@Body Vendor newVendor);

    @GET("/products")
    Call<ArrayList<Product>> getProducts(@QueryMap Map<String, String> queryMap);
}
