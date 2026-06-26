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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.relaxhub.frontend.ui.spots.SpotsListActivity;
import com.relaxhub.frontend.util.SpotMapHelper;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private SpotMapHelper spotMapHelper;
    private SupportMapFragment mapFragment;

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
        RelaxhubApi api = ApiClient.getApi();
        spotMapHelper = new SpotMapHelper(requireContext(), api);

        ChipGroup filterGroup = view.findViewById(R.id.spotFilterGroup);
        Chip allChip = view.findViewById(R.id.filterAllChip);

        ViewCompat.setOnApplyWindowInsetsListener(filterGroup, (chipBar, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) chipBar.getLayoutParams();
            params.topMargin = statusBars.top + getResources().getDimensionPixelSize(R.dimen.spacing_sm);
            chipBar.setLayoutParams(params);
            return windowInsets;
        });

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

        MaterialButton listViewButton = view.findViewById(R.id.openSpotsListButton);
        listViewButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SpotsListActivity.class)));

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.mapContainer, mapFragment)
                    .commitNow();
        }
        mapFragment.getMapAsync(this);
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
        applyPendingSpotFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyPendingSpotFocus();
    }

    private void applyPendingSpotFocus() {
        if (spotMapHelper == null || !(requireActivity() instanceof DashboardActivity)) {
            return;
        }
        ((DashboardActivity) requireActivity()).consumePendingSpotFocus(spotMapHelper);
    }

    private void onLocationReady() {
        if (requireActivity() instanceof DashboardActivity
                && ((DashboardActivity) requireActivity()).hasPendingSpotFocus()) {
            applyPendingSpotFocus();
            spotMapHelper.enableMyLocation();
            return;
        }
        spotMapHelper.enableMyLocation();
        spotMapHelper.refreshWithUserLocation();
    }
}
