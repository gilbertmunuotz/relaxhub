package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.ReceiptResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.maps.MainActivity;
import com.relaxhub.frontend.ui.receipt.ReceiptActivity;
import com.relaxhub.frontend.ui.spots.SpotsListActivity;
import com.relaxhub.frontend.util.DateTimeUtils;
import com.relaxhub.frontend.util.NetworkUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RelaxhubApi api;
    private LinearLayout recentVisitsContainer;
    private TextView recentVisitsEmpty;

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

        TextView greetingText = view.findViewById(R.id.homeGreetingText);
        TextView emailChip = view.findViewById(R.id.homeEmailChip);
        recentVisitsContainer = view.findViewById(R.id.recentVisitsContainer);
        recentVisitsEmpty = view.findViewById(R.id.recentVisitsEmpty);
        View homeHeader = view.findViewById(R.id.homeHeader);

        ViewCompat.setOnApplyWindowInsetsListener(homeHeader, (header, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            int topPadding = statusBars.top + getResources().getDimensionPixelSize(R.dimen.padding_medium);
            header.setPadding(
                    header.getPaddingLeft(),
                    topPadding,
                    header.getPaddingRight(),
                    header.getPaddingBottom()
            );
            return windowInsets;
        });

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        String fullName = sessionManager.getFullName();
        if (fullName == null || fullName.isEmpty()) {
            greetingText.setText(R.string.home_greeting_fallback);
        } else {
            greetingText.setText(buildGreeting(fullName));
        }
        emailChip.setText(sessionManager.getEmail());

        setupQuickAction(
                view.findViewById(R.id.openMapAction),
                R.drawable.ic_action_map,
                R.string.home_action_map_title,
                R.string.home_action_map_subtitle,
                () -> startActivity(new Intent(requireContext(), MainActivity.class))
        );
        setupQuickAction(
                view.findViewById(R.id.openBrowseAction),
                R.drawable.ic_action_browse,
                R.string.home_action_browse_title,
                R.string.home_action_browse_subtitle,
                () -> startActivity(new Intent(requireContext(), SpotsListActivity.class))
        );
        setupQuickAction(
                view.findViewById(R.id.openReceiptAction),
                R.drawable.ic_action_receipt,
                R.string.home_action_receipt_title,
                R.string.home_action_receipt_subtitle,
                () -> startActivity(new Intent(requireContext(), ReceiptActivity.class))
        );

        loadRecentVisits();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisits();
    }

    private String buildGreeting(String fullName) {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) {
            return getString(R.string.home_greeting_morning, fullName);
        }
        if (hour < 17) {
            return getString(R.string.home_greeting_afternoon, fullName);
        }
        return getString(R.string.home_greeting_evening, fullName);
    }

    private void setupQuickAction(
            View card,
            int iconRes,
            int titleRes,
            int subtitleRes,
            Runnable onClick
    ) {
        ImageView icon = card.findViewById(R.id.actionIcon);
        TextView title = card.findViewById(R.id.actionTitle);
        TextView subtitle = card.findViewById(R.id.actionSubtitle);
        icon.setImageResource(iconRes);
        title.setText(titleRes);
        subtitle.setText(subtitleRes);
        card.setOnClickListener(v -> onClick.run());
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
                renderRecentVisits(receipts);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReceiptResponse>>> call, Throwable t) {
                // Keep existing list
            }
        });
    }

    private void renderRecentVisits(List<ReceiptResponse> receipts) {
        recentVisitsContainer.removeAllViews();

        if (receipts == null || receipts.isEmpty()) {
            recentVisitsEmpty.setVisibility(View.VISIBLE);
            recentVisitsContainer.setVisibility(View.GONE);
            return;
        }

        recentVisitsEmpty.setVisibility(View.GONE);
        recentVisitsContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int limit = Math.min(receipts.size(), 3);
        for (int i = 0; i < limit; i++) {
            ReceiptResponse receipt = receipts.get(i);
            View item = inflater.inflate(R.layout.item_recent_visit, recentVisitsContainer, false);
            TextView placeName = item.findViewById(R.id.visitPlaceName);
            TextView dateTime = item.findViewById(R.id.visitDateTime);
            placeName.setText(receipt.getPlaceName());
            dateTime.setText(formatVisitDateTime(receipt));
            recentVisitsContainer.addView(item);
        }
    }

    private String formatVisitDateTime(ReceiptResponse receipt) {
        try {
            LocalDate date = LocalDate.parse(receipt.getVisitDate());
            LocalTime time = LocalTime.parse(receipt.getVisitTime());
            return DateTimeUtils.formatDate(date) + " · " + DateTimeUtils.formatTime(time);
        } catch (Exception exception) {
            return receipt.getVisitDate() + " · " + receipt.getVisitTime();
        }
    }
}
