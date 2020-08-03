package com.unc0ded.shopdeliver.repositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class VendorRepository {

    private static VendorRepository instance;
    private ArrayList<Vendor> vendorDataSet = new ArrayList<>();

    private CollectionReference vendorFdb = FirebaseFirestore.getInstance().collection("Vendors");
    FirebaseAuth vendorAuth = FirebaseAuth.getInstance();

    Gson gsonInstance = new GsonBuilder().setPrettyPrinting().create();

    public static VendorRepository getInstance(){
        if(instance == null){
            instance = new VendorRepository();
        }
        return instance;
    }

    public void fetchVendorList(String PIN_CODE, OnCompleteFetchListener fetchListener){
        fetchListener.onStart();

        vendorFdb.document(PIN_CODE != null ? PIN_CODE:"000000" ).get().addOnSuccessListener(documentSnapshot -> {
            Log.i("pin code", PIN_CODE != null ? PIN_CODE : "000000");
            if(documentSnapshot.exists() && documentSnapshot.getData() != null){
                vendorDataSet.clear();
                JsonObject vendors_list = gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject();

                for (String key: vendors_list.keySet()) {
                    if (!key.equals("New pin code available:")){
                        vendorDataSet.add(gsonInstance.fromJson(vendors_list.getAsJsonObject(key), Vendor.class));
                    }
                }

                fetchListener.onSuccess(vendorDataSet);
            }else{
                fetchListener.onFailure(new Exception("Document snapshot is null"));
            }

        }).addOnFailureListener(fetchListener::onFailure); /* This is equivalent to fetchListener.onFailure(e) */
    }

    public void registerVendor(Vendor newVendor, String uid, OnCompletePostListener listener) {
        listener.onStart();
        vendorFdb.document(newVendor.getAddress().getPinCode()).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()){
                HashMap<String, Date> timeStamp = new HashMap<>();
                timeStamp.put("New pin code available:", Calendar.getInstance().getTime());
                vendorFdb.document(newVendor.getAddress().getPinCode()).set(timeStamp)
                        .addOnSuccessListener(aVoid -> vendorFdb.document(newVendor.getAddress().getPinCode()).update(Objects.requireNonNull(vendorAuth.getCurrentUser()).getUid(), newVendor)
                                .addOnSuccessListener(aVoid1 -> {
                                    listener.onSuccess(new Throwable("Upload success"));
                                })
                                .addOnFailureListener(listener::onFailure));
            }else{
                vendorFdb.document(newVendor.getAddress().getPinCode()).update(Objects.requireNonNull(vendorAuth.getCurrentUser()).getUid(), newVendor)
                        .addOnSuccessListener(aVoid -> {
                            listener.onSuccess(new Throwable("Upload success"));
                        })
                        .addOnFailureListener(listener::onFailure);
            }
        });
    }
}
