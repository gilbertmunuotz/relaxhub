package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.UserResponse;
import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.data.remote.RelaxhubApi;
import com.relaxhub.frontend.ui.auth.LoginActivity;
import com.relaxhub.frontend.ui.complaints.ComplainsActivity;
import com.relaxhub.frontend.ui.feedback.UserFeedbackActivity;
import com.relaxhub.frontend.ui.receipt.ReceiptActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private RelaxhubApi api;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = ApiClient.getApi();

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        TextView avatarText = view.findViewById(R.id.accountAvatarText);
        TextView accountName = view.findViewById(R.id.accountName);
        TextView accountEmail = view.findViewById(R.id.accountEmail);
        TextView accountStatus = view.findViewById(R.id.accountStatus);

        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        accountName.setText(fullName == null || fullName.isEmpty() ? getString(R.string.nav_account) : fullName);
        accountEmail.setText(email);
        avatarText.setText(buildInitial(fullName, email));
        accountStatus.setText(R.string.profile_loading);

        bindSettingsRow(
                view.findViewById(R.id.openReceiptRow),
                R.drawable.ic_settings_receipt,
                R.string.open_receipt,
                R.string.settings_row_receipt_subtitle,
                () -> startActivity(new Intent(requireContext(), ReceiptActivity.class))
        );
        bindSettingsRow(
                view.findViewById(R.id.openFeedbackRow),
                R.drawable.ic_settings_feedback,
                R.string.open_feedback,
                R.string.settings_row_feedback_subtitle,
                () -> startActivity(new Intent(requireContext(), UserFeedbackActivity.class))
        );
        bindSettingsRow(
                view.findViewById(R.id.openComplaintsRow),
                R.drawable.ic_settings_complaint,
                R.string.open_complaints,
                R.string.settings_row_complaints_subtitle,
                () -> startActivity(new Intent(requireContext(), ComplainsActivity.class))
        );

        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        loadProfile(accountName, accountEmail, accountStatus, avatarText);
    }

    private void loadProfile(
            TextView accountName,
            TextView accountEmail,
            TextView accountStatus,
            TextView avatarText
    ) {
        api.getProfile().enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    UserResponse user = response.body().getData();
                    accountName.setText(user.getFullName());
                    accountEmail.setText(user.getEmail());
                    avatarText.setText(buildInitial(user.getFullName(), user.getEmail()));
                    accountStatus.setText(R.string.profile_loaded);
                    return;
                }
                accountStatus.setText(R.string.profile_failed);
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                accountStatus.setText(R.string.profile_failed);
            }
        });
    }

    private void bindSettingsRow(
            View row,
            int iconRes,
            int titleRes,
            int subtitleRes,
            Runnable onClick
    ) {
        ImageView icon = row.findViewById(R.id.settingsRowIcon);
        TextView title = row.findViewById(R.id.settingsRowTitle);
        TextView subtitle = row.findViewById(R.id.settingsRowSubtitle);

        icon.setImageResource(iconRes);
        title.setText(titleRes);
        subtitle.setText(subtitleRes);
        subtitle.setVisibility(View.VISIBLE);
        row.setOnClickListener(v -> onClick.run());
    }

    private String buildInitial(String fullName, String email) {
        if (fullName != null && !fullName.isEmpty()) {
            return String.valueOf(Character.toUpperCase(fullName.trim().charAt(0)));
        }
        if (email != null && !email.isEmpty()) {
            return String.valueOf(Character.toUpperCase(email.trim().charAt(0)));
        }
        return "?";
    }
}
