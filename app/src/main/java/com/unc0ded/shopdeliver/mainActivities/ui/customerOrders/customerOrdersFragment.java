package com.unc0ded.shopdeliver.mainActivities.ui.customerOrders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.unc0ded.shopdeliver.R;

public class customerOrdersFragment extends Fragment {

    private customerOrdersViewModel customerOrdersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerOrdersViewModel =
                ViewModelProviders.of(this).get(customerOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_customer_orders, container, false);
        final TextView textView = root.findViewById(R.id.view_orders_tv);
        customerOrdersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
