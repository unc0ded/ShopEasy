package com.unc0ded.shopdeliver.repositories;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;

public class VendorRepository {

    private static VendorRepository instance;
    private ArrayList<Vendor> vendorDataSet = new ArrayList<>();

    private CollectionReference vendorFdb = FirebaseFirestore.getInstance().collection("Vendors");
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
                        vendorDataSet.add(new Vendor(vendors_list.getAsJsonObject(key).get("Shop Name").getAsString(),
                                vendors_list.getAsJsonObject(key).get("Shop Type").getAsString(),
                                vendors_list.getAsJsonObject(key).get("Address").getAsJsonObject().get("Address Line 2").getAsString(),
                                "null"));
                    }
                }

                fetchListener.onSuccess(vendorDataSet);
            }else{
                fetchListener.onFailure(new Exception("Document snapshot is null"));
            }

        }).addOnFailureListener(fetchListener::onFailure); /* This is equivalent to fetchListener.onFailure(e) */
    }

}
