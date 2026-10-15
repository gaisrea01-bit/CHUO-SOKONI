package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.chuosokonifrontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyOtpFragment extends Fragment {

    private TextInputEditText etOtpCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        etOtpCode = view.findViewById(R.id.etOtpCode);
        MaterialButton btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp);

        if (btnVerifyOtp != null) {
            btnVerifyOtp.setOnClickListener(v -> {
                String code = etOtpCode.getText() != null ? etOtpCode.getText().toString().trim() : "";
                if (code.length() < 4) {
                    Toast.makeText(requireContext(), "Please enter a valid OTP verification code", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireContext(), "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_verifyOtp_to_resetPassword);
            });
        }
    }
}
