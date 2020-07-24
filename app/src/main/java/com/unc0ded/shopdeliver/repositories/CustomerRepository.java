package com.unc0ded.shopdeliver.repositories;

import com.unc0ded.shopdeliver.OnCompleteFetchListener;

public class CustomerRepository {

    private static CustomerRepository instance;

    public static CustomerRepository getInstance(){
        if(instance == null){
            instance = new CustomerRepository();
        }
        return instance;
    }

    public void getCustomer(String uid, OnCompleteFetchListener listener){

    }
}
