package com.chuosokonifrontend.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.chuosokonifrontend.R;
import com.chuosokonifrontend.ui.adapters.VerificationQueueAdapter;
import java.util.ArrayList;

public class AdminDisputesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private VerificationQueueAdapter adapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_disputes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.swipeAdminDisputes);
        RecyclerView recyclerView = view.findViewById(R.id.rvAdminDisputes);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new VerificationQueueAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> viewModel.fetchDisputes());
        }

        viewModel.getDisputes().observe(getViewLifecycleOwner(), list -> {
            if (list != null && adapter != null) {
                adapter.setQueueList(new ArrayList<>(list));
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(loading);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.fetchDisputes();
    }
}
