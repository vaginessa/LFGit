package com.lfgit.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static com.lfgit.Logger.LogAny;

public class PermissionRequester {
    public static Boolean isWriteStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                LogAny("Permission to Write is granted");
                return true;
            } else {
                LogAny("Permission to Write is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            LogAny("Permission to Write is automatically granted");
            return true;
        }
    }
    public static Boolean isTermuxExePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission("com.termux.permission.TERMUX_SERVICE")
                    == PackageManager.PERMISSION_GRANTED) {
                LogAny("Permission exe is granted");
                return true;
            } else {
                LogAny("Permission exe is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{"com.termux.permission.TERMUX_SERVICE"}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            LogAny("Permission to Write is granted2");
            return true;
        }
    }
}
