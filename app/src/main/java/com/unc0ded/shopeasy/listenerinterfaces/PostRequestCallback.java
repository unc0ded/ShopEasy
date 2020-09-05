package com.unc0ded.shopeasy.listenerinterfaces;

public interface PostRequestCallback {
    void onSuccess(Object data);
    void onFailure(String message);
}
