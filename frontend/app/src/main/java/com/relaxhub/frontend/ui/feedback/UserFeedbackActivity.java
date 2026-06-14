package com.relaxhub.frontend.ui.feedback;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class UserFeedbackActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_feedback;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_feedback;
    }
}
