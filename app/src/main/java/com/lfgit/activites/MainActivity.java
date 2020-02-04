package com.lfgit.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.lfgit.BuildConfig;
import com.lfgit.R;
import com.lfgit.TaskListener;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.importer.AssetImporter;
import com.lfgit.tasks.GitExec;
import com.lfgit.tasks.GitLfsExec;

import static com.lfgit.Logger.LogAny;
import static com.lfgit.permissions.PermissionRequester.isTermuxExePermissionGranted;


public class MainActivity extends AppCompatActivity implements TaskListener {

    String TAG = "petr";
    ProgressDialog progressDialog;
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ListView mRepoOperationList;
    private RepoOperationsAdapter mDrawerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);

        setupDrawer();

        if (isFirstRun()) {
            AssetImporter importer = new AssetImporter(getAssets(), this);
            importer.execute(true);
        }

        final Button button = findViewById(R.id.action_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exeTermux();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle_drawer:
                if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                    mDrawerLayout.closeDrawer(mRightDrawer);
                } else {
                    mDrawerLayout.openDrawer(mRightDrawer);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mRightDrawer = findViewById(R.id.right_drawer);

        mRepoOperationList = findViewById(R.id.repoOperationList);
        mDrawerAdapter = new RepoOperationsAdapter(this);
        mRepoOperationList.setAdapter(mDrawerAdapter);
        mRepoOperationList.setOnItemClickListener(mDrawerAdapter);
    }

    private void exeTermux() {
        if (isTermuxExePermissionGranted(MainActivity.this)) {
            String str = "am start-service --user 0 -a com.termux.service_execute -n com.termux/com.termux.app.TermuxService -d com.termux.file:/data/data/com.termux/files/home/exe.sh";
            Uri newUri = Uri.parse(str);
            Uri myUri = Uri.parse("com.termux.file:/data/data/com.termux/files/home/git-annex.linux/git-annex" );
            Intent executeIntent = new Intent("com.termux.service_execute", newUri);
            executeIntent.setClassName("com.termux", "com.termux.app.TermuxService");

            // Whether to execute script in background.
            //executeIntent.putExtra("com.termux.execute.background", true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(executeIntent);
            } else {
                getApplicationContext().startService(executeIntent);
            }
        }
    }

    private void busybox_echo() {
        String res = "";
        GitExec gitExec = new GitExec(MainActivity.this);
        res = gitExec.busybox_echo();

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);}


    private void init() {
        String res = "";
        GitExec gitExec = new GitExec(MainActivity.this);
        res = gitExec.init("new");

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);
    }

    private void LFSExec() {
        String res = "";
        GitLfsExec GitLfsExec = new GitLfsExec(MainActivity.this);
        res = GitLfsExec.install("new");

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);
    }


    private Boolean isFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return false;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }
                break;

            case 2:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }
                break;

            case 3:
                Log.d(TAG, "Exe permission");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }
                break;
        }
    }

    @Override
    public void onTaskStarted() {
        lockScreenOrientation();
        progressDialog = ProgressDialog.show(this, "Installing...", "Getting things ready..");
    }

    @Override
    public void onTaskFinished() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            unlockScreenOrientation();
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
