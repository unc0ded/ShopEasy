package com.unc0ded.shopdeliver.viewmodels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unc0ded.shopdeliver.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.models.Customer;
import com.unc0ded.shopdeliver.models.Vendor;
import com.unc0ded.shopdeliver.repositories.CustomerRepository;
import com.unc0ded.shopdeliver.repositories.VendorRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class CustomerMainActivityViewModel extends ViewModel {

    private VendorRepository vendorRepo = VendorRepository.getInstance();
    private CustomerRepository customerRepo = CustomerRepository.getInstance();
    private Gson gsonInstance = new GsonBuilder().setPrettyPrinting().create();


    private MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);
    private MutableLiveData<String> customerPinCode = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Vendor>> vendorsList = new MutableLiveData<>();

    public LiveData<Boolean> getIsFetching(){
        return isFetching;
    }
    public LiveData<String> getCustomerPinCode(){
        return  customerPinCode;
    }
    public LiveData<ArrayList<Vendor>> getVendors(){
        return vendorsList;
    }


    public void initializeVendorList(String PIN_CODE){
        vendorRepo.fetchVendorList(PIN_CODE, new OnCompleteFetchListener() {
            @Override
            public void onStart() {
                isFetching.setValue(true);
            }

            @Override
            public void onSuccess(Object result) {
                if (result != null && result.getClass().equals(ArrayList.class))
                    vendorsList.setValue((ArrayList<Vendor>) result);

                isFetching.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("FetchDataException", "" + e.getMessage());
                isFetching.setValue(false);
            }
        });
    }

    public void fetchCustomerPinCode(String uid){
        customerRepo.getCustomer(uid, new OnCompleteFetchListener() {
            @Override
            public void onStart() {
                isFetching.setValue(true);
            }

            @Override
            public void onSuccess(Object result) {
                isFetching.setValue(false);
//                customerPinCode.setValue(gsonInstance.toJsonTree(documentSnapshot.getData()).getAsJsonObject().getAsJsonObject("Address").get("Pin code").getAsString());
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("FetchListenerException", "" + e.getMessage());
                isFetching.setValue(false);
            }
        });
    }

}
