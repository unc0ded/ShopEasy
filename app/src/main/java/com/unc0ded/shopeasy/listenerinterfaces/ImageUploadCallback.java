package com.unc0ded.shopeasy.listenerinterfaces;

public interface ImageUploadCallback {
    void onSuccess(String downloadUrl);
    void onFailure(String message);
}
