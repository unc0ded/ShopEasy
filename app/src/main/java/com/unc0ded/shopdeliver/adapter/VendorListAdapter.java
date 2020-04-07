package com.unc0ded.shopdeliver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.model.Vendor;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListVH> {

    private Context context;
    private ArrayList<Vendor> vendorList=new ArrayList<>();

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
}

class VendorListVH extends RecyclerView.ViewHolder {

    TextView nameTV, typeTV, addressTV;
    public VendorListVH(@NonNull View itemView) {
        super(itemView);
        attachID();
    }

    private void attachID() {
        nameTV=itemView.findViewById(R.id.vendor_name_tv);
        typeTV=itemView.findViewById(R.id.vendor_type_tv);
        addressTV=itemView.findViewById(R.id.vendor_address_tv);
    }

    public void populate(Vendor vendor) {
        nameTV.setText(vendor.getName());
        typeTV.setText(vendor.getType());
        addressTV.setText(vendor.getAddress());
    }
}