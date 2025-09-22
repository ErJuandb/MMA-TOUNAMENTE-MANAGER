package com.example.rankone2;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class RankOneApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
} 