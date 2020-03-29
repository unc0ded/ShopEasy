package com.unc0ded.shopdeliver.mainActivities.ui.customerSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class customerSettingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public customerSettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}