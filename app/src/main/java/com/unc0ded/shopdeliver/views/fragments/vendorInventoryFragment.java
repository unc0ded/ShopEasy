package com.unc0ded.shopdeliver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.databinding.FragmentVendorInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;
import com.unc0ded.shopdeliver.views.adapters.InventoryItemAdapter;
import com.unc0ded.shopdeliver.views.widgets.AddItemDialog;

import java.util.ArrayList;

import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_FAILED;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_IS_UPLOADING;
import static com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel.STATUS_SUCCESS;


public class vendorInventoryFragment extends Fragment {

    FragmentVendorInventoryBinding binding;
    FirebaseAuth vendorAuth;
    private ArrayList<Product> inventoryList = new ArrayList<>();

    VendorMainActivityViewModel vendorMainActivityViewModel = new VendorMainActivityViewModel();

    InventoryItemAdapter adapter;

    public vendorInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vendorAuth = FirebaseAuth.getInstance();

        vendorMainActivityViewModel.getIsFetching().observe(this, status -> {
            binding.swipeRefreshItemList.setRefreshing(status);
        });

        vendorMainActivityViewModel.getVendorList().observe(this, inventory -> {
            inventoryList = inventory;
            if(inventoryList.size() > 0){
                binding.message.setVisibility(View.GONE);
            }else{
                binding.message.setVisibility(View.VISIBLE);
            }
            refreshRv();
        });

        vendorMainActivityViewModel.getIsUploading().observe(this, status -> {
            switch (status){
                case STATUS_IS_UPLOADING:
                    binding.swipeRefreshItemList.setRefreshing(true);
                    break;
                case STATUS_SUCCESS:
                    Toast.makeText(requireContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    vendorMainActivityViewModel.fetchVendorInventory(vendorAuth);
                    break;
                case STATUS_FAILED:
                    Toast.makeText(requireContext(), "Could not add item", Toast.LENGTH_SHORT).show();
                    binding.swipeRefreshItemList.setRefreshing(false);
                    break;
                default:
                    binding.swipeRefreshItemList.setRefreshing(false);
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.inventoryRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        refreshRv();

        binding.swipeRefreshItemList.setOnRefreshListener(() -> {
            if (vendorAuth.getUid() != null)
                vendorMainActivityViewModel.fetchVendorInventory(vendorAuth);
            else{
                binding.swipeRefreshItemList.setRefreshing(false);
                binding.message.setVisibility(View.VISIBLE);
            }
        });

        binding.addFab.setOnClickListener(view1 -> {
            final AddItemDialog addItemDialog = new AddItemDialog(requireContext());
            new MaterialAlertDialogBuilder(requireContext()).setView(addItemDialog)
                    .setCancelable(false)
                    .setPositiveButton("Save", (dialogInterface, i) -> addItemDialog.save(dialogInterface, vendorAuth))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        });

        if (vendorAuth.getUid() != null){
            vendorMainActivityViewModel.fetchVendorInventory(vendorAuth);
        }else{
            binding.message.setVisibility(View.VISIBLE);
        }
    }

    private void refreshRv(){
        adapter = new InventoryItemAdapter(inventoryList, requireContext());
        binding.inventoryRv.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
