package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.openFeedbackButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), UserFeedbackActivity.class)));
        view.findViewById(R.id.openComplaintsButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ComplainsActivity.class)));
        view.findViewById(R.id.openReceiptButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ReceiptActivity.class)));
        view.findViewById(R.id.openHelpButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HelpActivity.class)));
        view.findViewById(R.id.openContactButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ContactActivity.class)));
        view.findViewById(R.id.openPrivacyButton).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PrivacyPolicyActivity.class)));

        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).clearSession();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
