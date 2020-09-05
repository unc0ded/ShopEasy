package com.unc0ded.shopeasy.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopeasy.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopeasy.models.Vendor;
import com.unc0ded.shopeasy.retrofit.RetrofitClient;
import com.unc0ded.shopeasy.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorRepository {

    private static VendorRepository instance;
    private ArrayList<Vendor> vendorDataSet = new ArrayList<>();

    private CollectionReference vendorFdb = FirebaseFirestore.getInstance().collection("Vendors");

    private Gson gsonInstance = new GsonBuilder().setPrettyPrinting().create();

    public static VendorRepository getInstance(){
        if(instance == null){
            instance = new VendorRepository();
        }
        return instance;
    }

    public MutableLiveData<Vendor> registerVendor(SessionManager sessionManager, Vendor vendor) {
        MutableLiveData<Vendor> createdVendor = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).createVendor(vendor).enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(@NonNull Call<Vendor> call, @NonNull Response<Vendor> response) {
                if (response.isSuccessful())
                    createdVendor.setValue(response.body());
                else
                    createdVendor.setValue(null);
            }

            @Override
            public void onFailure(@NonNull Call<Vendor> call, @NonNull Throwable t) {
                createdVendor.setValue(null);
            }
        });
        return createdVendor;
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
}