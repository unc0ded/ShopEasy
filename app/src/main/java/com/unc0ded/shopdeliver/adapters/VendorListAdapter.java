package com.unc0ded.shopdeliver.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopdeliver.R;
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
        return new VendorListVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_list_item,parent,false));
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

        TextView nameTV, typeTV, addressTV;
        View deliveryStatus;

        public VendorListVH(@NonNull View itemView) {
            super(itemView);
            attachID();
        }

        private void attachID() {
            nameTV=itemView.findViewById(R.id.vendor_name_tv);
            typeTV=itemView.findViewById(R.id.vendor_type_tv);
            addressTV=itemView.findViewById(R.id.vendor_address_tv);
            deliveryStatus = itemView.findViewById(R.id.delivery_status);
        }

        public void populate(Vendor vendor) {
            nameTV.setText(vendor.getShopName());
            typeTV.setText(vendor.getType());
            addressTV.setText(vendor.getAddress());
            deliveryStatus.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.available), PorterDuff.Mode.SRC_ATOP);
        }
    }
}