package at.cath;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import at.cath.databinding.SettingsFragmentBinding;

public class SettingsFragment extends Fragment {

    private SettingsFragmentBinding binding;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = SettingsFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        if (preferences.contains("use_names")) {
            binding.useNameToggle.setChecked(preferences.getBoolean("use_names", true));
        }

        binding.useNameToggle.setOnCheckedChangeListener((compoundButton, checked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("use_names", checked);
            editor.apply();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}