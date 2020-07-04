package com.unc0ded.shopdeliver.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unc0ded.shopdeliver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class vendorOrdersFragment extends Fragment {

    public vendorOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_orders, container, false);
    }
}
