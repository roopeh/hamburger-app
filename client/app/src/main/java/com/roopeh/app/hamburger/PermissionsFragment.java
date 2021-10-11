package com.roopeh.app.hamburger;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public abstract class PermissionsFragment extends Fragment {
    final protected ActivityResultLauncher<String[]> activityResultLauncher;
    
    public PermissionsFragment() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (final boolean isGranted : result.values())
                        allGranted = isGranted && allGranted;

                    onPermissionsCheck(allGranted);
                });
    }

    // Method will be overridden in child classes
    protected abstract void onPermissionsCheck(final boolean allGranted);
}
