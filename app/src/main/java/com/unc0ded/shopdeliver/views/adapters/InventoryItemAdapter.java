package com.unc0ded.shopdeliver.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.unc0ded.shopdeliver.databinding.InventoryListItemBinding;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryVH> {

    JsonArray inventoryList;
    Context context;

    public InventoryItemAdapter(JsonArray inventoryList, Context context) {
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
        holder.populate(inventoryList.get(position).getAsJsonObject());
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    class InventoryVH extends RecyclerView.ViewHolder {

        InventoryListItemBinding binding;

        public InventoryVH(@NonNull InventoryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void populate(JsonObject inventoryItem) {
            binding.itemNameTv.setText(inventoryItem.get("name").getAsString());
            binding.priceTv.setText(inventoryItem.get("price").getAsString());
            binding.quantityTv.setText(inventoryItem.get("quantity").getAsString());
        }
    }
}
