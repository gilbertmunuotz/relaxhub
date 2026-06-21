package com.relaxhub.frontend.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.SpotResponse;
import com.relaxhub.frontend.data.remote.RelaxhubApi;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotMapHelper {

    private static final double DEFAULT_LAT = -6.7924;
    private static final double DEFAULT_LNG = 39.2083;
    private static final float DEFAULT_ZOOM = 12f;

    private final Context context;
    private final RelaxhubApi api;
    private final FusedLocationProviderClient locationClient;
    private final Map<String, SpotResponse> spotByMarkerId = new HashMap<>();

    private GoogleMap googleMap;
    private String typeFilter;
    private double searchLat = DEFAULT_LAT;
    private double searchLng = DEFAULT_LNG;

    public SpotMapHelper(Context context, RelaxhubApi api) {
        this.context = context;
        this.api = api;
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void bindMap(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LAT, DEFAULT_LNG), DEFAULT_ZOOM));
        googleMap.setOnInfoWindowClickListener(this::onInfoWindowClick);
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
        loadSpots();
    }

    @SuppressLint("MissingPermission")
    public void enableMyLocation() {
        if (googleMap == null || !hasLocationPermission()) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    public void refreshWithUserLocation() {
        if (!hasLocationPermission()) {
            loadSpots();
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                searchLat = location.getLatitude();
                searchLng = location.getLongitude();
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(searchLat, searchLng), 13f
                    ));
                }
            }
            loadSpots();
        });
    }

    public void loadSpots() {
        if (googleMap == null) {
            return;
        }

        api.getNearbySpots(searchLat, searchLng, 15, typeFilter).enqueue(new Callback<ApiResponse<List<SpotResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<SpotResponse>>> call,
                    Response<ApiResponse<List<SpotResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(context, R.string.spots_load_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                renderSpots(response.body().getData());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpotResponse>>> call, Throwable t) {
                Toast.makeText(context, R.string.spots_load_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderSpots(List<SpotResponse> spots) {
        googleMap.clear();
        spotByMarkerId.clear();

        if (spots == null || spots.isEmpty()) {
            Toast.makeText(context, R.string.spots_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        for (SpotResponse spot : spots) {
            float hue = "RESTAURANT".equals(spot.getType())
                    ? BitmapDescriptorFactory.HUE_ORANGE
                    : BitmapDescriptorFactory.HUE_VIOLET;
            String snippet = buildSnippet(spot);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
                    .title(spot.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            if (marker != null) {
                spotByMarkerId.put(marker.getId(), spot);
            }
        }
    }

    private String buildSnippet(SpotResponse spot) {
        StringBuilder builder = new StringBuilder();
        if (spot.getType() != null) {
            builder.append(spot.getType().replace('_', ' '));
        }
        if (spot.getDistanceKm() != null) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(String.format(Locale.getDefault(), "%.1f km", spot.getDistanceKm()));
        }
        if (spot.getAddress() != null && !spot.getAddress().isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(spot.getAddress());
        }
        return builder.toString();
    }

    private void onInfoWindowClick(Marker marker) {
        SpotResponse spot = spotByMarkerId.get(marker.getId());
        if (spot == null || spot.getDescription() == null) {
            return;
        }
        Toast.makeText(context, spot.getDescription(), Toast.LENGTH_LONG).show();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
