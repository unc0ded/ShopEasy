package com.unc0ded.shopeasy.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unc0ded.shopeasy.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopeasy.models.Customer;
import com.unc0ded.shopeasy.models.Vendor;
import com.unc0ded.shopeasy.repositories.CustomerRepository;
import com.unc0ded.shopeasy.repositories.VendorRepository;

import java.util.ArrayList;

public class CustomerMainActivityViewModel extends ViewModel {

    private VendorRepository vendorRepo = VendorRepository.getInstance();
    private CustomerRepository customerRepo = CustomerRepository.getInstance();

    private MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsFetching(){
        return isFetching;
    }

    private MutableLiveData<Customer> customerInfo = new MutableLiveData<>();
    public LiveData<Customer> getCustomerInfo(){
        return customerInfo;
    }

    private MutableLiveData<String> customerPinCode = new MutableLiveData<>();
    public LiveData<String> getCustomerPinCode(){ return  customerPinCode; }

    private MutableLiveData<ArrayList<Vendor>> vendorsList = new MutableLiveData<>();
    public LiveData<ArrayList<Vendor>> getVendors(){
        return vendorsList;
    }


    public void fetchVendorList(String PIN_CODE){
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
                vendorsList.setValue(null);
                isFetching.setValue(false);
            }
        });
    }

    //TODO: Populate this whenever needed
    public void fetchCustomerInfo(String uid){
        customerRepo.getCustomer(uid, new OnCompleteFetchListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object result) {
                customerInfo.setValue((Customer) result);
            }

            @Override
            public void onFailure(Exception e) {

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
                    customerPinCode.setValue(((Customer) result).getAddress().getPinCode());
                    Log.i("pin code retrieved", "" + customerPinCode.getValue());
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("FetchListenerException", "" + e.getMessage());
                isFetching.setValue(false);
            }
        });
    }

}
