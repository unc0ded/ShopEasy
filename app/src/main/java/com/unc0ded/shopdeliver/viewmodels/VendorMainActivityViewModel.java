package com.unc0ded.shopdeliver.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompleteFetchListener;
import com.unc0ded.shopdeliver.listenerinterfaces.OnCompletePostListener;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.repositories.InventoryRepository;
import com.unc0ded.shopdeliver.utils.SessionManager;

import java.util.ArrayList;
import java.util.Map;

public class VendorMainActivityViewModel extends AndroidViewModel {

    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();

    private MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsFetching(){ return isFetching; }

    private MutableLiveData<String> isUploading = new MutableLiveData<>();
    public LiveData<String> getIsUploading(){ return isUploading; }

    private MutableLiveData<ArrayList<Product>> inventoryList = new MutableLiveData<>();

    public static final String STATUS_IS_UPLOADING = "processing";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";

    public VendorMainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadInventory(SessionManager sessionManager, Map<String, String> query) {
        inventoryList = inventoryRepo.loadInventory(sessionManager, query);
    }

    public LiveData<ArrayList<Product>> getInventoryList() {
        return inventoryList;
    }

    public void addProduct(Product newProduct, String vendorId, Uri uploadUri){
        inventoryRepo.addProduct(newProduct, uploadUri, vendorId,  new OnCompletePostListener() {
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
