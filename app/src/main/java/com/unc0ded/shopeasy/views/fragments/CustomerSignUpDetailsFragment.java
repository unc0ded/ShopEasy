package com.unc0ded.shopeasy.views.fragments;

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

import com.unc0ded.shopeasy.R;
import com.unc0ded.shopeasy.ShopEasy;
import com.unc0ded.shopeasy.databinding.FragmentCustomerSignUpDetailsBinding;
import com.unc0ded.shopeasy.models.Customer;
import com.unc0ded.shopeasy.utils.SessionManager;
import com.unc0ded.shopeasy.viewmodels.LoginActivityViewModel;
import com.unc0ded.shopeasy.views.activities.CustomerMainActivity;

import java.util.Objects;

public class CustomerSignUpDetailsFragment extends Fragment {

    FragmentCustomerSignUpDetailsBinding binding;
    LoginActivityViewModel loginActivityViewModel;
    private SessionManager sessionManager;

    private String phone;

    //empty constructor
    public CustomerSignUpDetailsFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerSignUpDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phone = customerSignUpDetailsArgs.fromBundle(requireArguments()).getPhone();
        sessionManager = ((ShopEasy)requireActivity().getApplication()).getSessionManager();
        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, getResources().getStringArray(R.array.states));
        binding.state.setAdapter(adapter);

        binding.signUpBtn.setOnClickListener(v -> {
            binding.progressbar.setVisibility(View.VISIBLE);
            Customer newCustomer = new Customer();
            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected){
                newCustomer.setFirstName(binding.firstName.getText().toString().trim());
                newCustomer.setLastName(binding.lastName.getText().toString().trim());
                newCustomer.getAddress().setLine1(binding.firstLine.getText().toString().trim());
                newCustomer.getAddress().setLine2(binding.secondLine.getText().toString().trim());
                newCustomer.getAddress().setPinCode(binding.pinCode.getText().toString().trim());
                newCustomer.getAddress().setCity(binding.city.getText().toString().trim());
                newCustomer.getAddress().setState(binding.state.getText().toString().trim());
                newCustomer.setEmail(binding.emailId.getText().toString().trim());
                newCustomer.setPhone(phone);
                final String password = binding.password.getText().toString().trim();
                final String reEnterPassword = binding.reEnterPassword.getText().toString().trim();

                if (newCustomer.getFirstName().isEmpty() || newCustomer.getLastName().isEmpty() || newCustomer.getAddress().getLine1().isEmpty()
                        || newCustomer.getAddress().getLine2().isEmpty() || newCustomer.getAddress().getCity().isEmpty()
                        || newCustomer.getAddress().getState().isEmpty() || newCustomer.getAddress().getPinCode().isEmpty())
                    Toast.makeText(getContext(), "Please fill all the compulsory fields", Toast.LENGTH_LONG).show();
                else if (newCustomer.getEmail().isEmpty()) {
                    if (validatePinCode(newCustomer.getAddress().getPinCode())){
                        newCustomer.setEmail(null);
                        loginActivityViewModel.registerCustomer(sessionManager, newCustomer).observe(getViewLifecycleOwner(), customer -> {
                            if (customer != null) {
                                binding.progressbar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), getResources().getString(R.string.welcome_message, customer.getFirstName()), Toast.LENGTH_SHORT).show();
                                //loginActivityViewModel.clearNewCustomer();
                                startActivity(new Intent(requireContext(), CustomerMainActivity.class));
                                requireActivity().finish();
                            }
                            else {
                                Log.e("Customer Registration", "Unknown error");
                                Toast.makeText(requireContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                        Toast.makeText(getContext(), "Please enter a valid pin code", Toast.LENGTH_SHORT).show();
                }
                else if ((password.isEmpty() || reEnterPassword.isEmpty()))
                    Toast.makeText(getContext(), "Passwords are required!", Toast.LENGTH_LONG).show();
                else if (password.equals(reEnterPassword)) {
                    /*TODO link email
                    if (validatePinCode(newCustomer.getAddress().getPinCode())){
                        loginActivityViewModel.registerCustomer(sessionManager, newCustomer).observe(getViewLifecycleOwner(), customer -> {
                            if (customer != null) {
                                binding.progressbar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), getResources().getString(R.string.welcome_message, customer.getFirstName()), Toast.LENGTH_SHORT).show();
                                loginActivityViewModel.clearNewCustomer();
                                startActivity(new Intent(requireContext(), customerMainActivity.class));
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

    private boolean validatePinCode(String code) {
        return code.length() == 6 && !code.startsWith("0");
    }

}
