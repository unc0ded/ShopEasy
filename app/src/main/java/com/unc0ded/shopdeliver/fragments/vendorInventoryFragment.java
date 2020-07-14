package com.unc0ded.shopdeliver.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.databinding.FragmentVendorInventoryBinding;
import com.unc0ded.shopdeliver.widgets.AddItemDialog;


public class vendorInventoryFragment extends Fragment {

    FragmentVendorInventoryBinding binding;
    FirebaseFirestore db;
    FirebaseAuth vendorAuth;

    public vendorInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        vendorAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addFab.setOnClickListener(view1 -> {
            final AddItemDialog addItemDialog = new AddItemDialog(requireContext());
            new MaterialAlertDialogBuilder(requireContext()).setView(addItemDialog)
                    .setCancelable(false)
                    .setPositiveButton("Save", (dialogInterface, i) -> addItemDialog.save(db, vendorAuth))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
