package com.unc0ded.shopdeliver.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.ShopEasy;
import com.unc0ded.shopdeliver.databinding.FragmentVendorSignUpDetailsBinding;
import com.unc0ded.shopdeliver.models.Vendor;
import com.unc0ded.shopdeliver.utils.SessionManager;
import com.unc0ded.shopdeliver.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopdeliver.views.activities.vendorMainActivity;

import java.util.Objects;

public class vendorSignUpDetails extends Fragment {

    FragmentVendorSignUpDetailsBinding binding;
    LoginActivityViewModel loginActivityVM;
    private SessionManager sessionManager;

    private String phone;

    FirebaseAuth vendorAuth;

    //empty constructor
    public vendorSignUpDetails() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();
        loginActivityVM = new ViewModelProvider(requireActivity()).get(LoginActivityViewModel.class);

        vendorAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapterState = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, getResources().getStringArray(R.array.states));
        binding.state.setAdapter(adapterState);
        final String[] SHOP_TYPES = new String[]{"Grocery", "Medical Store", "Supermarket"};
        ArrayAdapter<String> adapterShopType = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, SHOP_TYPES);
        binding.shopType.setAdapter(adapterShopType);

        binding.signUpBtn.setOnClickListener(v -> {
            binding.progressbar.setVisibility(View.VISIBLE);
            Vendor newVendor = new Vendor();
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                newVendor.getProprietor().setFirstName(binding.firstName.getText().toString().trim());
                newVendor.getProprietor().setLastName(binding.lastName.getText().toString().trim());
                newVendor.setShopName(binding.shopName.getText().toString().trim());
                newVendor.setType(binding.shopType.getText().toString().trim());
                newVendor.getAddress().setLine1(binding.firstLine.getText().toString().trim());
                newVendor.getAddress().setLine2(binding.secondLine.getText().toString().trim());
                newVendor.getAddress().setPinCode(binding.pinCode.getText().toString().trim());
                newVendor.getAddress().setCity(binding.city.getText().toString().trim());
                newVendor.getAddress().setState(binding.state.getText().toString().trim());
                newVendor.getProprietor().setEmail(binding.emailId.getText().toString().trim());
                newVendor.getProprietor().setPhone(phone);
                final String password = binding.password.getText().toString().trim();
                final String reEnterPassword = binding.reEnterPassword.getText().toString().trim();

                if (newVendor.getProprietor().getFirstName().isEmpty() || newVendor.getProprietor().getLastName().isEmpty() || newVendor.getShopName().isEmpty()
                        || newVendor.getType().isEmpty() || newVendor.getAddress().getLine1().isEmpty() || newVendor.getAddress().getLine2().isEmpty()
                        || newVendor.getAddress().getCity().isEmpty() || newVendor.getAddress().getState().isEmpty() || newVendor.getAddress().getPinCode().isEmpty())
                    Toast.makeText(getContext(), "Please fill all the compulsory fields", Toast.LENGTH_LONG).show();
                else if (newVendor.getProprietor().getEmail().isEmpty()) {
                    if (validatePinCode(newVendor.getAddress().getPinCode())){
                        newVendor.getProprietor().setEmail(null);
                        loginActivityVM.registerVendor(sessionManager, newVendor).observe(getViewLifecycleOwner(), vendor -> {
                            if (vendor != null) {
                                binding.progressbar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), getResources().getString(R.string.welcome_vendor, vendor.getShopName()), Toast.LENGTH_SHORT).show();
                                loginActivityVM.clearNewVendor();
                                startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                requireActivity().finish();
                            }
                            else {
                                Log.e("Vendor Registration", "Unknown error");
                                Toast.makeText(requireContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                        Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                }
                else if ((password.isEmpty() || reEnterPassword.isEmpty()))
                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                else if (password.equals(reEnterPassword)) {
                    /*TODO Link Email
                    if (validatePinCode(newVendor.getAddress().getPinCode())){
                        loginActivityVM.registerVendor(sessionManager, newVendor).observe(getViewLifecycleOwner(), vendor -> {
                            if (vendor != null) {
                                binding.progressbar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), getResources().getString(R.string.welcome_vendor, vendor.getShopName()), Toast.LENGTH_SHORT).show();
                                loginActivityVM.clearNewVendor();
                                startActivity(new Intent(requireContext(), vendorMainActivity.class));
                                requireActivity().finish();
                            }
                        });
                    }
                    else
                        Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                     */
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
        return pin_code.length() == 6 && !pin_code.startsWith("0");
    }
}
