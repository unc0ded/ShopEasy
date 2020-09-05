package com.unc0ded.shopeasy.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopeasy.R;
import com.unc0ded.shopeasy.databinding.ItemVendorListBinding;
import com.unc0ded.shopeasy.models.Vendor;

import java.util.ArrayList;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorListVH> {

    private Context context;
    private ArrayList<Vendor> vendorList;

    public VendorListAdapter(Context context, ArrayList<Vendor> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    public VendorListAdapter() {
    }

    @NonNull
    @Override
    public VendorListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VendorListVH(ItemVendorListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VendorListVH holder, int position) {
        holder.populate(vendorList.get(position));
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    class VendorListVH extends RecyclerView.ViewHolder {

        ItemVendorListBinding binding;

        public VendorListVH(@NonNull ItemVendorListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void populate(Vendor vendor) {
            binding.vendorNameTv.setText(vendor.getShopName());
            binding.vendorTypeTv.setText(vendor.getType());
            binding.vendorAddressTv.setText(vendor.getAddress().getLocality());
            binding.deliveryStatus.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.available), PorterDuff.Mode.SRC_ATOP);
        }
    }
}