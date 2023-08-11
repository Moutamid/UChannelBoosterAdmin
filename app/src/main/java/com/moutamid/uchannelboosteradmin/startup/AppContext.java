package com.moutamid.uchannelboosteradmin.startup;

import android.app.Application;

import com.moutamid.uchannelboosteradmin.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
