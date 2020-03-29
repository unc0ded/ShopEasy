package com.unc0ded.shopdeliver.mainActivities.ui.customerShopsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class customerShopsListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public customerShopsListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}