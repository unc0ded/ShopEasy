package com.unc0ded.shopdeliver.mainActivities.ui.customerOrders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class customerOrdersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public customerOrdersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}