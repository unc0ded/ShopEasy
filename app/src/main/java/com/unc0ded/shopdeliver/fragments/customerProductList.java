package com.unc0ded.shopdeliver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.unc0ded.shopdeliver.databinding.FragmentCustomerProductListBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class customerProductList extends Fragment {

    FragmentCustomerProductListBinding binding;

    public customerProductList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
