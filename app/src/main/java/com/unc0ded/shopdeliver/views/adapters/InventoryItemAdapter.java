package com.unc0ded.shopdeliver.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopdeliver.databinding.InventoryListItemBinding;
import com.unc0ded.shopdeliver.models.Product;

import java.util.ArrayList;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryVH> {

    private ArrayList<Product> inventoryList;
    Context context;

    public InventoryItemAdapter(ArrayList<Product> inventoryList, Context context) {
        this.inventoryList = inventoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InventoryVH(InventoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryVH holder, int position) {
        holder.populate(inventoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryVH extends RecyclerView.ViewHolder {

        InventoryListItemBinding binding;

        public InventoryVH(@NonNull InventoryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void populate(Product product) {
            binding.itemNameTv.setText(product.getName());
            binding.priceTv.setText(String.valueOf(product.getPrice()));
            binding.quantityTv.setText(String.valueOf(product.getQuantity()));
        }
    }
}
