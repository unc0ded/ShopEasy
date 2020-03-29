package com.unc0ded.shopdeliver.mainActivities.ui.customerShopsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textview.MaterialTextView;
import com.unc0ded.shopdeliver.R;

public class customerShopsListFragment extends Fragment {

    private customerShopsListViewModel customerShopsListViewModel;
    MaterialTextView nearbyVendorT;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customerShopsListViewModel = ViewModelProviders.of(this).get(customerShopsListViewModel.class);
        root = inflater.inflate(R.layout.customer_fragment_view_orders, container, false);

        attachID();

        nearbyVendorT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(root.getContext(), "Test Toast", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void attachID() {
        nearbyVendorT = root.findViewById(R.id.nearby_vendors_tv);
    }
}
