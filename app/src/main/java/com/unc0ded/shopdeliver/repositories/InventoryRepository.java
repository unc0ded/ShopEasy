package com.unc0ded.shopdeliver.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.retrofit.RetrofitClient;
import com.unc0ded.shopdeliver.utils.SessionManager;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryRepository {

    private static InventoryRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference sr = FirebaseStorage.getInstance().getReference().child("Inventory/");

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
                    productList.postValue(response.body());
                else {
                    Log.e("Inventory", "Not received");
                    productList.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                Log.e("Inventory", "Not received: " + t.getMessage());
                productList.postValue(null);
            }
        });
        return productList;
    }

    public void addProduct(Product newProduct, Uri imageUri, String vendorId, @NonNull OnCompletePostListener listener){
        listener.onStart();
        db.collection("Inventory").add(newProduct)
                .addOnSuccessListener(documentReference -> {
                    listener.onSuccess(new Throwable("Added product(" + documentReference.getId() +") successfully"));
                    if (imageUri != null)
                        uploadImage(vendorId + "/" + documentReference.getId() + ".jpeg", imageUri, documentReference.getId());
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void uploadImage(String path, Uri imageUri, String documentReferenceId){
        StorageReference filePath = sr.child(path);
        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null){
                db.collection("Inventory").document(documentReferenceId)
                        .update("downloadUrl", task.getResult().toString())
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful())
                                Log.i("Upload product " + documentReferenceId + " image", "SUCCESS");
                            else
                                Log.i("Upload product " + documentReferenceId + " image", "FAILED - " + task.getException());
                        });
            } else
                Log.i("Upload product " + documentReferenceId + " image", "FAILED - " + task.getException());
        }));
    }
}
