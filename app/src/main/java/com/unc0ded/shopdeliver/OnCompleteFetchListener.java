package com.unc0ded.shopdeliver;

public interface OnCompleteFetchListener {
    void onStart();
    void onSuccess(Object result);
    void onFailure(Exception e);
}
