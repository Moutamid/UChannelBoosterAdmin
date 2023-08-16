package com.moutamid.uchannelboostadmin.startup;

import android.app.Application;

import com.moutamid.uchannelboostadmin.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
