package com.moutamid.viewplusadmin.startup;

import android.app.Application;

import com.moutamid.viewplusadmin.utils.Utils;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
