package com.relaxhub.frontend.ui.privacy;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class PrivacyPolicyActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_privacy;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_privacy;
    }
}
