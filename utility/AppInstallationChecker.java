package com.example.vmann.mapbox.utility;

import android.content.pm.PackageManager;


public class AppInstallationChecker {
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}