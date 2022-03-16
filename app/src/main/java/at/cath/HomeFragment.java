package at.cath;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import at.cath.adapters.LocationListAdapter;
import at.cath.data.LocationEntry;
import at.cath.databinding.HomeFragmentBinding;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private LocationListAdapter listAdapter;
    private FusedLocationProviderClient fusedLocationClient;

    private List<LocationEntry> locationEntryStates;

    private final String[] requiredPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (!result) {
                    Snackbar.make(requireView(), "Permission not available!", Snackbar.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = binding.locationListView;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        listAdapter = new LocationListAdapter(requireContext(), this::displayDistanceBetween,
                () -> binding.distanceDisplay.setText(""));

        if (locationEntryStates != null)
            for (LocationEntry entry : locationEntryStates) {
                entry.setSelected(false);
                listAdapter.addEntry(entry);
            }

        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.addLocationButton.setOnClickListener(it -> requestLocationUpdate());
    }

    @Override
    public void onPause() {
        super.onPause();
        locationEntryStates = listAdapter.getEntries();
    }

    public void displayDistanceBetween(LocationEntry entry1, LocationEntry entry2) {
        binding.distanceDisplay.setText(getString(R.string.loc_distance_label,
                "" + entry1.getLocation().distanceTo(entry2.getLocation())));
    }

    private void requestLocationUpdate() {
        startLocationLoadingAnimation();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            }).addOnSuccessListener(location -> {
                listAdapter.addEntry(new LocationEntry(location));
                stopLocationLoadingAnimation();
            });
        } else {
            for (String perm : requiredPermissions)
                requestPermissionLauncher.launch(perm);
        }
    }

    private void stopLocationLoadingAnimation() {
        binding.addLocationButton.setVisibility(View.VISIBLE);
        binding.buttonLoadingBar.setVisibility(View.INVISIBLE);
    }

    private void startLocationLoadingAnimation() {
        binding.addLocationButton.setVisibility(View.INVISIBLE);
        binding.buttonLoadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}