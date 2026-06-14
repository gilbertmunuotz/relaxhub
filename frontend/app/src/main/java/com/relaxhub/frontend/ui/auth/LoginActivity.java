package com.relaxhub.frontend.ui.auth;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class LoginActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_login;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_login;
    }
}
