package com.unc0ded.shopdeliver.mainActivities.ui.customerSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.unc0ded.shopdeliver.R;

public class customerSettingsFragment extends Fragment {

    private customerSettingsViewModel customerSettingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerSettingsViewModel =
                ViewModelProviders.of(this).get(customerSettingsViewModel.class);
        View root = inflater.inflate(R.layout.customer_fragment_settings, container, false);
        return root;
    }
}
