package com.unc0ded.shopdeliver.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.LoginActivity;
import com.unc0ded.shopdeliver.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.models.Vendor;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class vendorSignUpDetails extends Fragment {

    private MaterialButton signUp;
    private TextInputEditText shopNameE,vendorNameE,vendorTypeE,passwordE,reEnterPasswordE,emailE,addressE;

    String phone;

    private DatabaseReference userReference;
    FirebaseAuth vendorAuth;

    public vendorSignUpDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_sign_up_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopNameE = view.findViewById(R.id.shop_name);
        vendorNameE = view.findViewById(R.id.vendor_name);
        vendorTypeE = view.findViewById(R.id.vendor_type);
        addressE = view.findViewById(R.id.vendor_address);

        phone = vendorSignUpDetailsArgs.fromBundle(getArguments()).getPhone();

        emailE = view.findViewById(R.id.vendor_sign_up_email);
        passwordE = view.findViewById(R.id.vendor_sign_up_password);
        reEnterPasswordE = view.findViewById(R.id.vendor_sign_up_reenter_password);

        signUp = view.findViewById(R.id.vendor_sign_up_btn);

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!(passwordE.getText().toString().isEmpty()))&&(!(reEnterPasswordE.getText().toString().isEmpty()))
                        &&(!(shopNameE.getText().toString().trim().isEmpty()))&&(!(vendorNameE.getText().toString().trim().isEmpty()))
                        &&(!(vendorTypeE.getText().toString().trim().isEmpty()))&&(!(addressE.getText().toString().trim().isEmpty()))
                        &&(!(emailE.getText().toString().trim().isEmpty())))
                {
                    if((Objects.requireNonNull(passwordE.getText()).toString().equals(Objects.requireNonNull(reEnterPasswordE.getText()).toString())))
                    {
                        AuthCredential emailCredential = EmailAuthProvider.getCredential(emailE.getText().toString().trim(), reEnterPasswordE.getText().toString());
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
                        addVendor(shopNameE.getText().toString().trim(),vendorTypeE.getText().toString().trim(), vendorNameE.getText().toString().trim(), phone, emailE.getText().toString().trim(), addressE.getText().toString().trim());
                        Intent signUp = new Intent(getContext(), vendorMainActivity.class);
                        startActivity(signUp);
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(((passwordE.getText().toString().isEmpty()))&&((reEnterPasswordE.getText().toString().isEmpty()))
                        &&(!(shopNameE.getText().toString().trim().isEmpty()))&&(!(vendorNameE.getText().toString().trim().isEmpty()))
                        &&(!(vendorTypeE.getText().toString().trim().isEmpty()))&&(!(addressE.getText().toString().trim().isEmpty()))
                        &&((emailE.getText().toString().trim().isEmpty())))
                {
                    addVendor(shopNameE.getText().toString().trim(),vendorTypeE.getText().toString().trim(), vendorNameE.getText().toString().trim(), phone, addressE.getText().toString().trim());
                    Intent signUp = new Intent(getContext(), vendorMainActivity.class);
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

    private void addVendor(String shopName, String shopType, String propName, String phone, String email, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,email,address);
        userReference.child("Vendors").child(address).child(shopNameE.getText().toString().trim()).setValue(createVendor);
        Toast.makeText(getContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }

    private void addVendor(String shopName, String shopType, String propName, String phone, String address) {
        Vendor createVendor = new Vendor(shopName,shopType,propName,phone,address);
        userReference.child("Vendors").child(address).child(shopNameE.getText().toString().trim()).setValue(createVendor);
        Toast.makeText(getContext(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
    }
}
