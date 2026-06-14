package com.relaxhub.frontend.ui.dashboard;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class DashboardActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_dashboard;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_dashboard;
    }
}
