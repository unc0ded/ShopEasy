package com.unc0ded.shopdeliver.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.databinding.FragmentVendorSignUpDetailsBinding;
import com.unc0ded.shopdeliver.models.Vendor;
import com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopdeliver.views.activities.vendorMainActivity;

import java.util.Objects;

public class vendorSignUpDetails extends Fragment {

    FragmentVendorSignUpDetailsBinding binding;
    LoginActivityViewModel loginActivityVM = new LoginActivityViewModel();

    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_STARTS = "start";

    String phone;

    FirebaseAuth vendorAuth;

    Vendor newVendor = new Vendor();

    //empty constructor
    public vendorSignUpDetails() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginActivityVM.getIsUploading().observe(this, isUploading -> {
            switch (isUploading){
                case STATUS_FAILED:
                    Toast.makeText(requireContext(), "Authentication failed! Please try again", Toast.LENGTH_LONG).show();
                    binding.progressbar.setVisibility(View.GONE);
                    break;
                case STATUS_SUCCESS:
                    Toast.makeText(getActivity(), "You have been registered successfully!", Toast.LENGTH_LONG).show();
                    binding.progressbar.setVisibility(View.GONE);
                    startActivity(new Intent(requireContext(), vendorMainActivity.class));
                    requireActivity().finish();
                    break;
                case STATUS_STARTS:
                    binding.progressbar.setVisibility(View.VISIBLE);
                    break;
                default:
                    binding.progressbar.setVisibility(View.GONE);
                    break;
            }
        });

        loginActivityVM.getAuthStatus().observe(this, status -> {
            switch (status){
                case STATUS_SUCCESS:
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Email linked successfully", Toast.LENGTH_SHORT).show();
                    loginActivityVM.registerUser(newVendor, vendorAuth.getUid());
                    break;
                case STATUS_FAILED:
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Could not link email! Try again later!", Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_STARTS:
                    binding.progressbar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendorSignUpDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = vendorSignUpDetailsArgs.fromBundle(requireArguments()).getPhone();

        vendorAuth = FirebaseAuth.getInstance();

        final String[] STATES = new String[]{"MAHARASHTRA", "GOA"};
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, STATES);
        binding.state.setAdapter(adapter_state);
        final String[] SHOP_TYPES = new String[]{"Grocery", "Medical Store", "Supermarket"};
        ArrayAdapter<String> adapter_shop_type = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, SHOP_TYPES);
        binding.shopType.setAdapter(adapter_shop_type);

        binding.signUpBtn.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                newVendor.getProprietor().setFirstName(binding.firstName.getText().toString().trim());
                newVendor.getProprietor().setLastName(binding.lastName.getText().toString().trim());
                newVendor.setShopName(binding.shopName.getText().toString().trim());
                newVendor.setType(binding.shopType.getText().toString().trim());
                newVendor.getAddress().setAddressLine1(binding.firstLine.getText().toString().trim());
                newVendor.getAddress().setAddressLine2(binding.secondLine.getText().toString().trim());
                newVendor.getAddress().setPinCode(binding.pinCode.getText().toString().trim());
                newVendor.getAddress().setCity(binding.city.getText().toString().trim());
                newVendor.getAddress().setState(binding.state.getText().toString().trim());
                newVendor.getProprietor().setEmail(binding.emailId.getText().toString().trim());
                newVendor.getProprietor().setPhone(phone);
                String password = binding.password.getText().toString().trim();
                String re_enter_password = binding.reEnterPassword.getText().toString().trim();

                if (newVendor.getProprietor().getFirstName().isEmpty() || newVendor.getProprietor().getLastName().isEmpty() || newVendor.getShopName().isEmpty()
                        || newVendor.getType().isEmpty() || newVendor.getAddress().getAddressLine1().isEmpty() || newVendor.getAddress().getAddressLine2().isEmpty()
                        || newVendor.getAddress().getCity().isEmpty() || newVendor.getAddress().getState().isEmpty() || newVendor.getAddress().getPinCode().isEmpty())
                    Toast.makeText(getContext(), "Please fill all the compulsory fields", Toast.LENGTH_LONG).show();
                else if (newVendor.getProprietor().getEmail().isEmpty()) {
                    if (validatePinCode(newVendor.getAddress().getPinCode())){
                        newVendor.getProprietor().setEmail(null);
                        loginActivityVM.registerUser(newVendor, vendorAuth.getUid());
                    }
                    else
                        Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                }
                else if ((password.isEmpty() || re_enter_password.isEmpty()))
                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                else if (password.equals(re_enter_password)) {
                    loginActivityVM.linkEmail(newVendor.getProprietor().getEmail(), re_enter_password);
                }
                else
                    Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

    private boolean validatePinCode(String pin_code) {
        if (pin_code.length() == 6 && !pin_code.startsWith("0"))
            return true;
        else
            return false;
    }
}
