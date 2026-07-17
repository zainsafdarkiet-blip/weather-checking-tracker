package com.example.weathertracker.fragments;
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
import com.example.weathertracker.adapters.CheckInAdapter;
import com.example.weathertracker.databinding.FragmentHistoryBinding;
import com.example.weathertracker.entity.CheckInEntity;
import com.example.weathertracker.viewmodel.CheckInViewModel;
import com.google.android.material.snackbar.Snackbar;
public class HistoryFragment extends Fragment implements CheckInAdapter.OnItemDeleteListener {
    private FragmentHistoryBinding binding;
    private CheckInViewModel viewModel;
    private CheckInAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        setupRecyclerView();
        viewModel.getAllCheckIns().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewHistory.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.recyclerViewHistory.setVisibility(View.VISIBLE);
                adapter.setCheckInList(list);
            }
        });
    }
    private void setupRecyclerView() {
        adapter = new CheckInAdapter(this);
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewHistory.setHasFixedSize(true);
        binding.recyclerViewHistory.setAdapter(adapter);
    }
    @Override
    public void onDeleteClick(CheckInEntity checkIn, int position) {
        Snackbar.make(binding.getRoot(), "Delete check-in at " + checkIn.getAddress() + "?", Snackbar.LENGTH_LONG)
                .setAction("DELETE", v -> {
                    viewModel.delete(checkIn);
                    Toast.makeText(requireContext(), "Check-in removed from Database.", Toast.LENGTH_SHORT).show();
                }).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

