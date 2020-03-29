package com.unc0ded.shopdeliver.mainActivities.ui.customerOrders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.unc0ded.shopdeliver.R;

public class customerOrdersFragment extends Fragment {

    private customerOrdersViewModel customerOrdersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerOrdersViewModel =
                ViewModelProviders.of(this).get(customerOrdersViewModel.class);
        View root = inflater.inflate(R.layout.customer_fragment_shops_list, container, false);

        return root;
    }
}
