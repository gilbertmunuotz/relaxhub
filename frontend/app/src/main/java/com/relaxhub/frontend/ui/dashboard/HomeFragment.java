package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.ReceiptResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.maps.MainActivity;
import com.relaxhub.frontend.ui.receipt.ReceiptActivity;
import com.relaxhub.frontend.util.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RelaxhubApi api;
    private TextView recentVisitsText;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getApi();

        TextView welcomeText = view.findViewById(R.id.homeWelcomeText);
        recentVisitsText = view.findViewById(R.id.recentVisitsText);
        MaterialButton openMapButton = view.findViewById(R.id.openMapButton);
        MaterialButton openReceiptButton = view.findViewById(R.id.openReceiptButton);

        if (getContext() != null) {
            String fullName = SessionManager.getInstance(getContext()).getFullName();
            if (fullName == null || fullName.isEmpty()) {
                welcomeText.setText(R.string.dashboard_home_message);
            } else {
                welcomeText.setText(getString(R.string.dashboard_welcome, fullName));
            }
        }

        openMapButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MainActivity.class)));
        openReceiptButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ReceiptActivity.class)));

        loadRecentVisits();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisits();
    }

    private void loadRecentVisits() {
        if (getContext() == null || !NetworkUtils.isNetworkAvailable(requireContext())) {
            return;
        }

        api.getReceipts().enqueue(new Callback<ApiResponse<List<ReceiptResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<ReceiptResponse>>> call,
                    Response<ApiResponse<List<ReceiptResponse>>> response
            ) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    return;
                }

                List<ReceiptResponse> receipts = response.body().getData();
                if (receipts == null || receipts.isEmpty()) {
                    recentVisitsText.setText(R.string.receipt_history_empty);
                    return;
                }

                StringBuilder builder = new StringBuilder();
                int limit = Math.min(receipts.size(), 3);
                for (int i = 0; i < limit; i++) {
                    ReceiptResponse receipt = receipts.get(i);
                    if (builder.length() > 0) {
                        builder.append("\n\n");
                    }
                    builder.append(getString(
                            R.string.receipt_item_format,
                            receipt.getPlaceName(),
                            receipt.getVisitDate(),
                            receipt.getVisitTime()
                    ));
                }
                recentVisitsText.setText(builder.toString());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReceiptResponse>>> call, Throwable t) {
                // Keep existing text
            }
        });
    }
}
