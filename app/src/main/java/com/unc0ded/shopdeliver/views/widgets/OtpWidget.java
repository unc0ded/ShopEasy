package com.unc0ded.shopdeliver.views.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.chaos.view.PinView;
import com.google.android.material.button.MaterialButton;
import com.unc0ded.shopdeliver.databinding.OtpDialogBinding;

public class OtpWidget extends LinearLayout {

    OtpDialogBinding binding;
    PinView otpView;
    MaterialButton verifyBtn;

    public OtpWidget(Context context) {
        super(context);
        binding = OtpDialogBinding.inflate(LayoutInflater.from(context), this, true);

        otpView = binding.otpEt;
        verifyBtn = binding.otpVerifyBtn;

        otpView.setAnimationEnable(true);
        otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpView.getText().toString().isEmpty())
                    verifyBtn.setEnabled(false);
                else verifyBtn.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otpView.getText().toString().isEmpty())
                    verifyBtn.setEnabled(false);
                else verifyBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public MaterialButton getVerifyBtn() {
        return verifyBtn;
    }

    public PinView getOtpView() {
        return otpView;
    }
}
