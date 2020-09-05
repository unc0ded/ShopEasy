package com.unc0ded.shopeasy.listenerinterfaces;

public interface OnCompleteFetchListener {
    void onStart();
    void onSuccess(Object result);
    void onFailure(Exception e);
}
