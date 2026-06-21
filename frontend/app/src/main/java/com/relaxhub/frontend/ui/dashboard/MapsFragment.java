package com.relaxhub.frontend.ui.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.maps.MainActivity;
import com.relaxhub.frontend.util.SpotMapHelper;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private SpotMapHelper spotMapHelper;
    private RelaxhubApi api;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    onLocationReady();
                } else {
                    Toast.makeText(requireContext(), R.string.location_permission_required, Toast.LENGTH_SHORT).show();
                    spotMapHelper.loadSpots();
                }
            });

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getApi();
        spotMapHelper = new SpotMapHelper(requireContext(), api);

        ChipGroup filterGroup = view.findViewById(R.id.spotFilterGroup);
        Chip allChip = view.findViewById(R.id.filterAllChip);
        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.filterRestaurantChip) {
                spotMapHelper.setTypeFilter("RESTAURANT");
            } else if (checkedId == R.id.filterRelaxationChip) {
                spotMapHelper.setTypeFilter("RELAXATION");
            } else {
                spotMapHelper.setTypeFilter(null);
            }
        });
        allChip.setChecked(true);

        MaterialButton openFullMapButton = view.findViewById(R.id.openFullMapButton);
        openFullMapButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MainActivity.class)));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        spotMapHelper.bindMap(map);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            onLocationReady();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void onLocationReady() {
        spotMapHelper.enableMyLocation();
        spotMapHelper.refreshWithUserLocation();
    }
}
