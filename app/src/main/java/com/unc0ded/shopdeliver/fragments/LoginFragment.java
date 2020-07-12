package com.unc0ded.shopdeliver.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.customerMainActivity;
import com.unc0ded.shopdeliver.activities.vendorMainActivity;
import com.unc0ded.shopdeliver.databinding.FragmentLoginBinding;


public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    View root;

    //TODO: the loginBar was initialized as loginBar = root.findViewById(R.id.login_tool_bar) but was never used.
    // Have to figure out what it is used for and how to implement it.
//    Toolbar loginBar;

    //empty constructor
    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog signUpPopUp = new Dialog(requireContext());
                signUpPopUp.setContentView(R.layout.sign_up_dialogbox);
                Button customer = signUpPopUp.findViewById(R.id.popup_customer_sign_up_btn),vendor = signUpPopUp.findViewById(R.id.popup_vendor_sign_up_btn);

                //Customer and Vendor Buttons
                {
                    customer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigation.findNavController(root).navigate(R.id.action_login_to_customer_sign_up_main);
                            signUpPopUp.dismiss();
                        }
                    });

                    vendor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigation.findNavController(root).navigate(R.id.action_login_to_vendorSignUpMain);
                            signUpPopUp.dismiss();
                        }
                    });
                }
                signUpPopUp.show();
            }
        });

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder choose = new AlertDialog.Builder(requireContext());
                choose.setCancelable(true);
                choose.setTitle("Debug");
                choose.setMessage("Which activity do you want to debug?");
                choose.setPositiveButton("customerMainActivity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), customerMainActivity.class));
                        requireActivity().finish();
                    }
                });

                choose.setNegativeButton("vendorMainActivity", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), vendorMainActivity.class));
                        requireActivity().finish();
                    }
                });

                choose.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //de-initialize binding object
        binding = null;
    }

}
