package com.chuosokonifrontend.ui.agent;

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

public class ReferredUsersFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AgentViewModel viewModel;
    private VerificationQueueAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_referred_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AgentViewModel.class);
        swipeRefreshLayout = view.findViewById(R.id.swipeReferredUsers);
        recyclerView = view.findViewById(R.id.rvReferredUsers);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new VerificationQueueAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> viewModel.fetchReferredUsers(1L));
        }

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(loading);
            }
        });

        viewModel.getReferredUsers().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                adapter.setQueueList(new ArrayList<>(list));
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.fetchReferredUsers(1L);
    }
}
