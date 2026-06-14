package com.relaxhub.frontend.ui.receipt;

import com.relaxhub.frontend.R;
import com.relaxhub.frontend.ui.BaseScreenActivity;

public class ReceiptActivity extends BaseScreenActivity {

    @Override
    protected int getScreenTitleRes() {
        return R.string.title_receipt;
    }

    @Override
    protected int getPlaceholderMessageRes() {
        return R.string.placeholder_receipt;
    }
}
