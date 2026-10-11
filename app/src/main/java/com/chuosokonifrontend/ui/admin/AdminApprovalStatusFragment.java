package com.chuosokonifrontend.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AdminApprovalStatusFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private VerificationQueueAdapter adapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_approval_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.swipeApprovalStatus);
        RecyclerView recyclerView = view.findViewById(R.id.rvApprovalStatus);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new VerificationQueueAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> viewModel.fetchVerificationQueues());
        }

        viewModel.getVerificationQueues().observe(getViewLifecycleOwner(), queues -> {
            if (queues != null) {
                adapter.setQueueList(new ArrayList<>(queues));
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(loading);
            }
        });

        viewModel.fetchVerificationQueues();
    }
}
