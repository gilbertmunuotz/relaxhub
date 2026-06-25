package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;
import com.relaxhub.frontend.ui.auth.LoginActivity;
import com.relaxhub.frontend.ui.complaints.ComplainsActivity;
import com.relaxhub.frontend.ui.contact.ContactActivity;
import com.relaxhub.frontend.ui.feedback.UserFeedbackActivity;
import com.relaxhub.frontend.ui.help.HelpActivity;
import com.relaxhub.frontend.ui.privacy.PrivacyPolicyActivity;
import com.relaxhub.frontend.ui.receipt.ReceiptActivity;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull android.view.LayoutInflater inflater,
            @Nullable android.view.ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        TextView avatarText = view.findViewById(R.id.settingsAvatarText);
        TextView userName = view.findViewById(R.id.settingsUserName);
        TextView userEmail = view.findViewById(R.id.settingsUserEmail);

        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        userName.setText(fullName == null || fullName.isEmpty() ? getString(R.string.nav_account) : fullName);
        userEmail.setText(email);
        avatarText.setText(buildInitial(fullName, email));

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
        bindSettingsRow(
                view.findViewById(R.id.openReceiptRow),
                R.drawable.ic_settings_receipt,
                R.string.open_receipt,
                R.string.settings_row_receipt_subtitle,
                () -> startActivity(new Intent(requireContext(), ReceiptActivity.class))
        );
        bindSettingsRow(
                view.findViewById(R.id.openHelpRow),
                R.drawable.ic_settings_help,
                R.string.open_help,
                R.string.settings_row_help_subtitle,
                () -> startActivity(new Intent(requireContext(), HelpActivity.class))
        );
        bindSettingsRow(
                view.findViewById(R.id.openContactRow),
                R.drawable.ic_settings_contact,
                R.string.open_contact,
                R.string.settings_row_contact_subtitle,
                () -> startActivity(new Intent(requireContext(), ContactActivity.class))
        );
        bindSettingsRow(
                view.findViewById(R.id.openPrivacyRow),
                R.drawable.ic_settings_privacy,
                R.string.open_privacy,
                R.string.settings_row_privacy_subtitle,
                () -> startActivity(new Intent(requireContext(), PrivacyPolicyActivity.class))
        );

        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
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
