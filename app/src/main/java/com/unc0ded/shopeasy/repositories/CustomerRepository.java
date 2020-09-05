package com.unc0ded.shopeasy.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopeasy.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopeasy.models.Customer;
import com.unc0ded.shopeasy.retrofit.RetrofitClient;
import com.unc0ded.shopeasy.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {

    private static CustomerRepository instance;
    private static CollectionReference customerFdb = FirebaseFirestore.getInstance().collection("Customers");
    private Gson gsonInstance = new GsonBuilder().setPrettyPrinting().create();

    public static CustomerRepository getInstance(){
        if(instance == null){
            instance = new CustomerRepository();
        }
        return instance;
    }

    public MutableLiveData<Customer> registerCustomer(SessionManager sessionManager, Customer customer) {
        MutableLiveData<Customer> createdCustomer = new MutableLiveData<>();
        RetrofitClient.getClient(sessionManager).createCustomer(customer).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                if (response.isSuccessful())
                    createdCustomer.setValue(response.body());
                else
                    createdCustomer.setValue(null);
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                createdCustomer.setValue(null);
            }
        });
        return createdCustomer;
    }

    public void getCustomer(String uid, OnCompleteFetchListener listener){
        listener.onStart();

        customerFdb.document(uid).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                    JsonObject snapshot = gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject();
                    listener.onSuccess(new Gson().fromJson(snapshot.toString(), Customer.class));
                }else{
                    listener.onFailure(new Exception("Customer not found"));
                }
            }).addOnFailureListener(listener::onFailure);/*Same is doing listener.onFailure(e)*/
    }

}
