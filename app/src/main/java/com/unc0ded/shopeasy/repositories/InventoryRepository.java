package com.unc0ded.shopeasy.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unc0ded.shopeasy.listenerinterfaces.ImageUploadCallback;
import com.unc0ded.shopeasy.listenerinterfaces.PostRequestCallback;
import com.unc0ded.shopeasy.models.Product;
import com.unc0ded.shopeasy.retrofit.RetrofitClient;
import com.unc0ded.shopeasy.utils.SessionManager;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryRepository {

    private static InventoryRepository instance;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Inventory/");

    public static InventoryRepository getInstance(){
        if (instance == null)
            instance = new InventoryRepository();
        return instance;
    }

    public MutableLiveData<ArrayList<Product>> loadInventory(SessionManager sessionManager, Map<String, String> query) {
        MutableLiveData<ArrayList<Product>> productList = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).getProducts(query).enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                if (response.isSuccessful())
                    productList.setValue(response.body());
                else {
                    Log.e("Inventory", "Not received");
                    productList.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                Log.e("Inventory", "Not received: " + t.getMessage());
                productList.setValue(null);
            }
        });
        return productList;
    }

    public void addProduct(SessionManager sessionManager, Product newProduct, PostRequestCallback callback) {
        RetrofitClient.getClient(sessionManager).createProduct(newProduct).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else {
                    Log.e("AddProduct", "Error");
                    callback.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                Log.e("AddProduct", "Error: " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void uploadImage(String path, Uri imageUri, ImageUploadCallback callback){
        StorageReference filePath = storageReference.child(path);
        filePath.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        callback.onSuccess(task.getResult().toString());
                    } else {
                        callback.onFailure(task.getException().toString());
                    }
                }))
                .addOnFailureListener(e -> callback.onFailure(e.toString()));
    }

    public MutableLiveData<Product> updateProduct(SessionManager sessionManager, String productId, Map<String, Object> updateFieldMap) {
        MutableLiveData<Product> updatedProduct = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).updateProduct(productId, updateFieldMap).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful())
                    updatedProduct.setValue(response.body());
                else {
                    Log.e("UpdateProductProcess", "Failed: " + response.message());
                    updatedProduct.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                Log.e("UpdateProductProcess", "Failed: " + t.getMessage());
                updatedProduct.setValue(null);
            }
        });
        return updatedProduct;
    }
}
