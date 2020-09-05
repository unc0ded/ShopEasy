package com.unc0ded.shopeasy.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unc0ded.shopeasy.listenerinterfaces.ImageUploadCallback;
import com.unc0ded.shopeasy.listenerinterfaces.PostRequestCallback;
import com.unc0ded.shopeasy.models.Product;
import com.unc0ded.shopeasy.repositories.InventoryRepository;
import com.unc0ded.shopeasy.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VendorMainActivityViewModel extends AndroidViewModel {

    private InventoryRepository inventoryRepo = InventoryRepository.getInstance();

    private MutableLiveData<Boolean> isFetching = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsFetching(){ return isFetching; }

    private MutableLiveData<String> isUploading = new MutableLiveData<>();
    public LiveData<String> getIsUploading(){ return isUploading; }

    private MutableLiveData<ArrayList<Product>> inventoryList = new MutableLiveData<>();
    private MutableLiveData<Product> addedProduct = new MutableLiveData<>();

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

    public void addProduct(SessionManager sessionManager, Product newProduct, String vendorId, Uri imageUri){
        inventoryRepo.addProduct(sessionManager, newProduct, new PostRequestCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data instanceof Product)
                    uploadImage(sessionManager, vendorId + "/" + ((Product) data).getId() + ".jpg", imageUri, ((Product) data).getId());
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void uploadImage(SessionManager sessionManager, String childPath, Uri imageUri, String productId) {
        inventoryRepo.uploadImage(childPath, imageUri, new ImageUploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                if (downloadUrl != null)
                    updateProductWithImage(sessionManager, productId, downloadUrl);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void updateProductWithImage(SessionManager sessionManager, String productId, String downloadUrl) {
        Map<String, Object> updateField = new HashMap<>();
        updateField.put("downloadUrl", downloadUrl);
        addedProduct = inventoryRepo.updateProduct(sessionManager, productId, updateField);
    }

    public LiveData<Product> getAddedProduct() {
        return addedProduct;
    }
}
