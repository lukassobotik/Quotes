package com.sforge.quotes;


import android.app.Application;
import com.google.android.material.color.DynamicColors;

public class myQuotesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
