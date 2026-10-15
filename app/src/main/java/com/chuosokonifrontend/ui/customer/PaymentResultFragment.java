package com.chuosokonifrontend.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.chuosokonifrontend.R;
import com.google.android.material.button.MaterialButton;

public class PaymentResultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        MaterialButton btnViewReceipt = view.findViewById(R.id.btnViewReceipt);
        if (btnViewReceipt != null) {
            btnViewReceipt.setOnClickListener(v -> navController.navigate(R.id.action_result_to_orderTracking));
        }
    }
}
