package com.unc0ded.shopdeliver.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Product;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryRepository {

    private static InventoryRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference sr = FirebaseStorage.getInstance().getReference().child("Inventory/");

    private ArrayList<Product> dataSet = new ArrayList<>();
    private static Gson gsonInstance = new GsonBuilder().setPrettyPrinting().create();

    public static InventoryRepository getInstance(){
        if (instance == null)
            instance = new InventoryRepository();
        return instance;
    }

    public void fetchInventory(@NonNull FirebaseAuth vendor, @NonNull OnCompleteFetchListener listener){
        listener.onStart();
        db.collection("Inventory").whereEqualTo("vendorId", Objects.requireNonNull(vendor.getCurrentUser()).getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null){
                        listener.onFailure(error);
                    }
                    else if (value == null){
                        listener.onFailure(new Exception("Inventory is empty"));
                    }
                    else{
                        if (dataSet.size() > 0) {
                            dataSet.clear();
                        }
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            dataSet.add(gsonInstance.fromJson(new Gson().toJsonTree(documentSnapshot.getData()).getAsJsonObject(), Product.class));
                        }
                        listener.onSuccess(dataSet);
                    }
                });
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
