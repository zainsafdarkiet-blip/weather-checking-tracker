package com.example.weathertracker.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.weathertracker.R;
import com.example.weathertracker.databinding.FragmentSettingsBinding;
import com.example.weathertracker.preferences.PreferencesManager;
import com.example.weathertracker.viewmodel.CheckInViewModel;
public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private PreferencesManager preferences;
    private CheckInViewModel viewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = PreferencesManager.getInstance(requireContext());
        viewModel = new ViewModelProvider(this).get(CheckInViewModel.class);
        loadPreferenceStates();
        registerPreferenceListeners();
    }
    private void loadPreferenceStates() {
        preferences.getSaveLocations().subscribe(save -> {
            requireActivity().runOnUiThread(() -> binding.switchSaveLocations.setChecked(save));
        });

        preferences.getDarkMode().subscribe(enabled -> {
            requireActivity().runOnUiThread(() -> binding.switchDarkMode.setChecked(enabled));
        });

        preferences.getMapType().subscribe(type -> {
            requireActivity().runOnUiThread(() -> {
                switch (type) {
                    case "SATELLITE": binding.radioSatellite.setChecked(true); break;
                    case "TERRAIN": binding.radioTerrain.setChecked(true); break;
                    case "HYBRID": binding.radioHybrid.setChecked(true); break;
                    default: binding.radioNormal.setChecked(true); break;
                }
            });
        });
    }
    private void registerPreferenceListeners() {
        binding.switchSaveLocations.setOnCheckedChangeListener((btn, checked) -> {
            preferences.setSaveLocations(checked);
        });
        binding.switchDarkMode.setOnCheckedChangeListener((btn, checked) -> {
            preferences.setDarkMode(checked);
        });
        binding.radioGroupMap.setOnCheckedChangeListener((group, id) -> {
            String selectedType = "NORMAL";
            if (id == R.id.radio_satellite) {
                selectedType = "SATELLITE";
            } else if (id == R.id.radio_terrain) {
                selectedType = "TERRAIN";
            } else if (id == R.id.radio_hybrid) {
                selectedType = "HYBRID";
            }
            preferences.setMapType(selectedType);
        });

        binding.btnWipeDb.setOnClickListener(v -> showWipeDbConfirmationDialog());
    }
    private void showWipeDbConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Wipe Local Database?")
                .setMessage("Are you absolutely sure you want to clear all history? This action cannot be undone.")
                .setPositiveButton("DELETE ALL", (dialog, which) -> {
                    viewModel.deleteAll();
                    Toast.makeText(requireContext(), "Database fully cleared.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("CANCEL", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

