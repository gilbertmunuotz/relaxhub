package com.relaxhub.frontend.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.contact.ContactActivity;
import com.relaxhub.frontend.ui.help.HelpActivity;
import com.relaxhub.frontend.ui.privacy.PrivacyPolicyActivity;

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
}
