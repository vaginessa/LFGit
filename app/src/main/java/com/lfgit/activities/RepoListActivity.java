package com.lfgit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.adapters.RepoListAdapter;
import com.lfgit.databinding.ActivityRepoListBinding;
import com.lfgit.fragments.InstallFragment;
import com.lfgit.fragments.FragmentCallback;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.LocalRepoViewModel;
import com.lfgit.view_models.RepoListViewModel;

import static com.lfgit.utilites.Logger.LogMsg;

public class RepoListActivity extends BasicAbstractActivity implements FragmentCallback {
    private ActivityRepoListBinding mBinding;
    private LocalRepoViewModel localRepoViewModel;
    private RepoListAdapter mRepoListAdapter;
    private InstallPreference installPref = new InstallPreference();
    FragmentManager mManager = getSupportFragmentManager();
    private String installTag = "install";

    private static final int ADD_REPO_REQUEST_CODE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (installPref.assetsInstalled()) {
            checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            runInstallFragment();
        }

        RepoListViewModel repoListViewModel = new ViewModelProvider(this).get(RepoListViewModel.class);
        localRepoViewModel = new ViewModelProvider(this).get(LocalRepoViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list);
        mBinding.setLifecycleOwner(this);
        mBinding.setRepoListViewModel(repoListViewModel);

        mRepoListAdapter = new RepoListAdapter(this, repoListViewModel);
        mBinding.repoList.setAdapter(mRepoListAdapter);
        mBinding.repoList.setOnItemClickListener(mRepoListAdapter);
        mBinding.repoList.setOnItemLongClickListener(mRepoListAdapter);

        repoListViewModel.getAllRepos().observe(this, repoList -> {
            mRepoListAdapter.setRepos(repoList);
            localRepoViewModel.setAllRepos(repoList);
        });
    }

    private void runInstallFragment() {
        FragmentTransaction transaction = mManager.beginTransaction();
        InstallFragment fragment = new InstallFragment();
        fragment.setCallback(this);
        transaction.add(R.id.repoListLayout,fragment);
        transaction.addToBackStack(installTag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_init_repo:
                intent = new Intent(this, InitRepoActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_add_repo:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, ADD_REPO_REQUEST_CODE);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void removeFragment() {
        Fragment fragment = mManager.findFragmentByTag(installTag);
        if(fragment != null) {
            mManager.popBackStack();
        }
        installPref.updateInstallPreference();
        checkAndRequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ADD_REPO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                String path = UriHelper.getDirPath(this, uri);
                if (path != null) {
                    if (localRepoViewModel.addLocalRepo(UriHelper.getDirPath(this, uri))
                            == Constants.AddRepo.ADDED) {
                        showToastMsg("Repository already added");
                    }
                } else {
                    showToastMsg("Please choose directory from primary volume");
                }
            }
        }
    }
}

