package com.unc0ded.shopdeliver;

public interface OnCompletePostListener {
    void onStart();
    void onSuccess(Throwable t);
    void onFailure(Exception e);
}
