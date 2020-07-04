package com.unc0ded.shopdeliver.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.unc0ded.shopdeliver.R;
import com.unc0ded.shopdeliver.activities.customerMainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    TextInputEditText emailOrPhoneE,passwordE;
    MaterialTextView signUp;
    MaterialButton login;
    Toolbar loginBar;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        attachID();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog signUpPopUp = new Dialog(getContext());
                signUpPopUp.setContentView(R.layout.sign_up_popup_dialogbox);
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getContext(), customerMainActivity.class);
                startActivity(login);
            }
        });
    }

    private void attachID() {
        emailOrPhoneE = root.findViewById(R.id.email_phone_tf);
        passwordE = root.findViewById(R.id.password_tf);
        signUp = root.findViewById(R.id.sign_up_textView);
        loginBar = root.findViewById(R.id.login_toolbar);
        login= root.findViewById(R.id.login_btn);
    }
}
