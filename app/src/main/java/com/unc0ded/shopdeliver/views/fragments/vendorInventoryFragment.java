package com.unc0ded.shopdeliver.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.ShopEasy;
import com.unc0ded.shopdeliver.databinding.FragmentVendorInventoryBinding;
import com.unc0ded.shopdeliver.models.Product;
import com.unc0ded.shopdeliver.utils.SessionManager;
import com.unc0ded.shopdeliver.viewmodels.VendorMainActivityViewModel;
import com.unc0ded.shopdeliver.views.adapters.InventoryItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class vendorInventoryFragment extends Fragment {

    FragmentVendorInventoryBinding binding;
    VendorMainActivityViewModel vendorMainActivityViewModel;
    SessionManager sessionManager;

    private ArrayList<Product> inventoryList = new ArrayList<>();
    private Map<String, String> queryMap = new HashMap<>();


    public vendorInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vendorMainActivityViewModel = new ViewModelProvider(requireActivity()).get(VendorMainActivityViewModel.class);
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();

        binding.inventoryRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        InventoryItemAdapter rvAdapter = new InventoryItemAdapter(inventoryList, requireContext());
        binding.inventoryRv.setAdapter(rvAdapter);

        if (sessionManager.fetchUserId() != null) {
            binding.swipeRefreshItemList.setRefreshing(true);
            binding.message.setText(getResources().getString(R.string.loading_inventory));
            binding.message.setVisibility(View.VISIBLE);
            queryMap.put("vendor", sessionManager.fetchUserId());
            vendorMainActivityViewModel.loadInventory(sessionManager, queryMap);
        }
        else {
            Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show();
        }

        vendorMainActivityViewModel.getInventoryList().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                inventoryList = products;
                rvAdapter.notifyDataSetChanged();
                binding.message.setVisibility(View.GONE);
                if (binding.swipeRefreshItemList.isRefreshing()) binding.swipeRefreshItemList.setRefreshing(false);
            }
            else {
                Toast.makeText(requireContext(), "Empty Inventory/Unknown Error", Toast.LENGTH_SHORT).show();
                if (binding.swipeRefreshItemList.isRefreshing()) binding.swipeRefreshItemList.setRefreshing(false);
                binding.message.setText(getResources().getString(R.string.empty_inventory));
                binding.message.setVisibility(View.VISIBLE);
            }
        });

        binding.swipeRefreshItemList.setOnRefreshListener(() -> {
            if (queryMap.get("vendor") != null) {
                vendorMainActivityViewModel.loadInventory(sessionManager, queryMap);
            }
            else {
                Toast.makeText(requireContext(), "Blank query", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addFab.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_add_to_inventory));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (queryMap.get("vendor") != null) {
            binding.swipeRefreshItemList.setRefreshing(true);
            vendorMainActivityViewModel.loadInventory(sessionManager, queryMap);
        }
        else {
            if (sessionManager.fetchUserId() != null) {
                binding.swipeRefreshItemList.setRefreshing(true);
                queryMap.put("vendor", sessionManager.fetchUserId());
                vendorMainActivityViewModel.loadInventory(sessionManager, queryMap);
            }
            else {
                Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
