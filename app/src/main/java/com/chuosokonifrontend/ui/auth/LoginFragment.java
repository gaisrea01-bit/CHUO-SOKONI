package com.chuosokonifrontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.chuosokonifrontend.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private TextInputEditText etLoginPhone, etLoginPassword;
    private MaterialButton btnLoginSubmit;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etLoginPhone = view.findViewById(R.id.etLoginPhone);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        btnLoginSubmit = view.findViewById(R.id.btnLoginSubmit);
        TextView tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        TextView tvCreateAccountPrompt = view.findViewById(R.id.tvCreateAccountPrompt);

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> navController.navigate(R.id.action_login_to_passwordRecovery));
        }

        if (tvCreateAccountPrompt != null) {
            tvCreateAccountPrompt.setOnClickListener(v -> navController.navigate(R.id.action_login_to_register));
        }

        authViewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        btnLoginSubmit.setOnClickListener(v -> {
            String identifier = etLoginPhone.getText() != null ? etLoginPhone.getText().toString().trim() : "";
            String password = etLoginPassword.getText() != null ? etLoginPassword.getText().toString().trim() : "";
            authViewModel.login(identifier, password);
        });
    }
}
