package com.unc0ded.shopdeliver.listenerinterfaces;

public interface OnCompletePostListener {
    void onStart();
    void onSuccess(Throwable t);
    void onFailure(Exception e);
}
