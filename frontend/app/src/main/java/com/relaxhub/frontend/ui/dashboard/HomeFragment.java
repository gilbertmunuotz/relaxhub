package com.relaxhub.frontend.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.data.local.SessionManager;

public class HomeFragment extends TabPlaceholderFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView messageView = view.findViewById(R.id.fragmentMessage);

        if (getContext() == null) {
            messageView.setText(R.string.dashboard_home_message);
            return;
        }

        String fullName = SessionManager.getInstance(getContext()).getFullName();
        if (fullName == null || fullName.isEmpty()) {
            messageView.setText(R.string.dashboard_home_message);
        } else {
            messageView.setText(getString(R.string.dashboard_welcome, fullName));
        }
    }

    @Override
    protected int getMessageRes() {
        return R.string.dashboard_home_message;
    }
}
