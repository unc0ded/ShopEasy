package com.unc0ded.shopdeliver;

public interface OnAuthenticationListener {
    void onStart();
    void onSuccess(Throwable t);
    void onFailure(Exception e);
}
