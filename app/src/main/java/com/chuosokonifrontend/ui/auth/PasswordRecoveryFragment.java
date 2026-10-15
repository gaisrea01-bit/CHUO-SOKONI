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

public class PasswordRecoveryFragment extends Fragment {

    private TextInputEditText etForgotEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        etForgotEmail = view.findViewById(R.id.etForgotEmail);
        MaterialButton btnSendRecovery = view.findViewById(R.id.btnSendRecovery);

        if (btnSendRecovery != null) {
            btnSendRecovery.setOnClickListener(v -> {
                String emailPhone = etForgotEmail.getText() != null ? etForgotEmail.getText().toString().trim() : "";
                if (emailPhone.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter email or phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireContext(), "OTP sent to " + emailPhone, Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_passwordRecovery_to_verifyOtp);
            });
        }
    }
}
