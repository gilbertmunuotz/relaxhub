package com.relaxhub.frontend;

import android.app.Application;

import com.relaxhub.frontend.data.remote.ApiClient;
import com.relaxhub.frontend.util.NotificationHelper;

public class RelaxhubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(this);
        NotificationHelper.createChannels(this);
    }
}
