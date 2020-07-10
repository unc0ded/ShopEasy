package com.unc0ded.shopdeliver.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.databinding.FragmentVendorSignUpDetailsBinding;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.Objects;

public class vendorSignUpDetails extends Fragment {

    FragmentVendorSignUpDetailsBinding binding;

    String phone;

    private DatabaseReference userReference;
    FirebaseAuth vendorAuth;

    //empty constructor
    public vendorSignUpDetails() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorSignUpDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = vendorSignUpDetailsArgs.fromBundle(getArguments()).getPhone();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!(binding.password.getText().toString().isEmpty()))&&(!(binding.reEnterPassword.getText().toString().isEmpty()))
                        &&(!(binding.shopName.getText().toString().trim().isEmpty()))&&(!(binding.vendorName.getText().toString().trim().isEmpty()))
                        &&(!(binding.shopType.getText().toString().trim().isEmpty()))&&(!(binding.vendorAddress.getText().toString().trim().isEmpty()))
                        &&(!(binding.emailId.getText().toString().trim().isEmpty())))
                {
                    if((Objects.requireNonNull(binding.password.getText()).toString().equals(Objects.requireNonNull(binding.reEnterPassword.getText()).toString())))
                    {
                        AuthCredential emailCredential = EmailAuthProvider.getCredential(binding.emailId.getText().toString().trim(), binding.reEnterPassword.getText().toString());
                        vendorAuth.getCurrentUser().linkWithCredential(emailCredential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(getContext(), "Email Linked Successfully", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getContext(), "Email Link Failed: "+ task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        addVendor(binding.shopName.getText().toString().trim(),binding.shopType.getText().toString().trim(), binding.vendorName.getText().toString().trim(), phone, binding.emailId.getText().toString().trim(), binding.vendorAddress.getText().toString().trim());
                        Intent signUp = new Intent(getContext(), vendorMainActivity.class);
                        startActivity(signUp);
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(((binding.password.getText().toString().isEmpty()))&&((binding.reEnterPassword.getText().toString().isEmpty()))
                        &&(!(binding.shopName.getText().toString().trim().isEmpty()))&&(!(binding.vendorName.getText().toString().trim().isEmpty()))
                        &&(!(binding.shopType.getText().toString().trim().isEmpty()))&&(!(binding.vendorAddress.getText().toString().trim().isEmpty()))
                        &&((binding.emailId.getText().toString().trim().isEmpty())))
                {
                    addVendor(binding.shopName.getText().toString().trim(),binding.shopType.getText().toString().trim(), binding.vendorName.getText().toString().trim(), phone, binding.vendorAddress.getText().toString().trim());
                    Intent signUp= new Intent(getContext(), vendorMainActivity.class);
                    startActivity(signUp);
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getContext(), "Please fill necessary fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private void addVendor(String shopName, String shopType, String propName, String phone, String email, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,email,address);
        userReference.child("Vendors").child(address).child(binding.shopName.getText().toString().trim()).setValue(createVendor);
        Toast.makeText(getContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addVendor(String shopName, String shopType, String propName, String phone, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,address);
        userReference.child("Vendors").child(address).child(binding.shopName.getText().toString().trim()).setValue(createVendor);
        Toast.makeText(getContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }
}
