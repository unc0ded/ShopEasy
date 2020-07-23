package com.unc0ded.shopdeliver.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.unc0ded.shopdeliver.adapters.InventoryItemAdapter;
import com.unc0ded.shopdeliver.databinding.FragmentVendorInventoryBinding;
import com.unc0ded.shopdeliver.widgets.AddItemDialog;

import java.util.Objects;


public class vendorInventoryFragment extends Fragment {

    FragmentVendorInventoryBinding binding;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    FirebaseAuth vendorAuth;
    JsonArray inventoryArray;

    InventoryItemAdapter adapter;

    public vendorInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        vendorAuth = FirebaseAuth.getInstance();
        inventoryArray = new JsonArray();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new InventoryItemAdapter(inventoryArray, requireContext());

        binding.inventoryRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.inventoryRv.setAdapter(adapter);

        binding.addFab.setOnClickListener(view1 -> {
            final AddItemDialog addItemDialog = new AddItemDialog(requireContext());
            new MaterialAlertDialogBuilder(requireContext()).setView(addItemDialog)
                    .setCancelable(false)
                    .setPositiveButton("Save", (dialogInterface, i) -> addItemDialog.save(db, vendorAuth))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        listenerRegistration = db.collection("Inventory").whereEqualTo("vendorId", Objects.requireNonNull(vendorAuth.getCurrentUser()).getUid())
                .addSnapshotListener(((value, error) -> {
                    if (error != null)
                        Log.e("Failed", error.toString());
                    else {
                        int size = inventoryArray.size();
                        for (int i = 0; i < size; i++) {
                            inventoryArray.remove(0);
                        }
                        for (QueryDocumentSnapshot documentSnapshot: value) {
                            inventoryArray.add(new Gson().toJsonTree(documentSnapshot.getData()).getAsJsonObject());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        listenerRegistration.remove();
    }
}
