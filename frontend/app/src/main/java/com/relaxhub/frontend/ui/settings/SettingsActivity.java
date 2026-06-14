package com.relaxhub.frontend.ui.settings;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class SettingsActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_settings;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_settings;
    }
}
