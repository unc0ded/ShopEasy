package com.unc0ded.shopdeliver.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.repositories.InventoryRepository;

import java.util.ArrayList;

public class VendorMainActivityViewModel extends ViewModel {

    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();

    private MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsFetching(){ return isFetching; }

    private MutableLiveData<String> isUploading = new MutableLiveData<>();
    public LiveData<String> getIsUploading(){ return isUploading; }

    private MutableLiveData<ArrayList<Product>> vendorList = new MutableLiveData<>();
    public LiveData<ArrayList<Product>> getVendorList(){ return vendorList; }

    public static final String STATUS_IS_UPLOADING = "processing";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";

    public void fetchVendorInventory(FirebaseAuth vendor){
        inventoryRepo.fetchInventory(vendor, new OnCompleteFetchListener() {
            @Override
            public void onStart() {
                isFetching.setValue(true);
            }

            @Override
            public void onSuccess(Object result) {
                isFetching.setValue(false);
                Log.i("fetched list", "success: " + result.toString());
                vendorList.setValue((ArrayList<Product>) result);
            }

            @Override
            public void onFailure(Exception e) {
                isFetching.setValue(false);
                Log.i("InventoryFetchException", "" + e.getMessage());
            }
        });
    }

    public void addProduct(Product newProduct, FirebaseAuth auth, Uri uploadUri){
        inventoryRepo.addProduct(newProduct, uploadUri, auth,  new OnCompletePostListener() {
            @Override
            public void onStart() {
                isUploading.setValue(STATUS_IS_UPLOADING);
            }

            @Override
            public void onSuccess(Throwable t) {
                Log.i("AddProductThrowable", "" + t.getMessage());
                isUploading.setValue(STATUS_SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("AddProductException", "" + e.getMessage());
                isUploading.setValue(STATUS_FAILED);
            }
        });
    }
}
