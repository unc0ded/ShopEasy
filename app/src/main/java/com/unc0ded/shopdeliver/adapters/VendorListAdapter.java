package com.unc0ded.shopdeliver.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.VendorListItemBinding;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.ArrayList;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorListVH> {

    private Context context;
    private ArrayList<Vendor> vendorList;

    public VendorListAdapter(Context context, ArrayList<Vendor> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public VendorListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VendorListVH(VendorListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

        VendorListItemBinding binding;

        public VendorListVH(@NonNull VendorListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void populate(Vendor vendor) {
            binding.vendorNameTv.setText(vendor.getShopName());
            binding.vendorTypeTv.setText(vendor.getType());
            binding.vendorAddressTv.setText(vendor.getAddress());
            binding.deliveryStatus.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.available), PorterDuff.Mode.SRC_ATOP);
        }
    }
}