package com.lfgit.activities;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lfgit.R;
import com.lfgit.utilites.BasicFunctions;

public abstract class BasicAbstractActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicFunctions.setActiveActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BasicFunctions.setActiveActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // permission denied - TODO handle not granted better"
                showToastMsg("Permission not granted");
            }
        }
    }
    protected void checkAndRequestPermissions(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it from user
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST);
        }
    }

    public void showToastMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BasicAbstractActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showOptionsDialog(int title, int optionsResource, final onOptionClicked[] option_listeners) {
        CharSequence[] options_values = getResources().getStringArray(optionsResource);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(options_values, (dialog, which) ->
                option_listeners[which].onClicked()).create().show();
    }

    public interface onOptionClicked {
        void onClicked();
    }

    protected void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}