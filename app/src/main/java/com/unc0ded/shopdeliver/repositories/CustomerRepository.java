package com.unc0ded.shopdeliver.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Customer;

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

    public void registerCustomer(Customer customer, String uid, OnCompletePostListener listener){
        listener.onStart();
        customerFdb.document(uid).set(customer)
                .addOnSuccessListener(aVoid -> {
                    listener.onSuccess(new Throwable("Upload success"));
                })
                .addOnFailureListener(listener::onFailure);
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
