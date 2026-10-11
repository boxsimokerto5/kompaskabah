package com.example.update

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Backend service for managing official Google Play In-App Updates.
 * Runs completely in the background without modifying app UI screens.
 * Automatically checks for updates from Google Play, prompts user via Google's native
 * dialog, or automatically completes flexible background downloads.
 */
class GooglePlayUpdateManager(
    private val activity: ComponentActivity
) {

    companion object {
        private const val TAG = "GooglePlayUpdateManager"
    }

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(activity)
    }

    private var updateResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    private val installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Log.d(TAG, "Update downloaded. Completing update automatically...")
                // Completes an update that has downloaded in the background (Flexible update)
                appUpdateManager.completeUpdate()
            }
            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytes = state.totalBytesToDownload()
                Log.d(TAG, "Downloading update: $bytesDownloaded / $totalBytes bytes")
            }
            InstallStatus.FAILED -> {
                Log.w(TAG, "Download failed with error code: ${state.installErrorCode()}")
            }
            InstallStatus.CANCELED -> {
                Log.d(TAG, "Update download was canceled")
            }
            else -> {
                Log.d(TAG, "Install status: ${state.installStatus()}")
            }
        }
    }

    init {
        // Register Activity Result launcher for in-app update intent sender
        updateResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "Update flow finished or dismissed with result code: ${result.resultCode}")
            } else {
                Log.d(TAG, "Update flow completed successfully with RESULT_OK")
            }
        }

        // Register listener for flexible background download status
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    /**
     * Check for Google Play updates in the background.
     * Can trigger IMMEDIATE (official Google fullscreen update sheet)
     * or FLEXIBLE (background download followed by automatic installation).
     */
    fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            val availability = appUpdateInfo.updateAvailability()
            Log.d(TAG, "Update availability: $availability, availableVersionCode: ${appUpdateInfo.availableVersionCode()}")

            if (availability == UpdateAvailability.UPDATE_AVAILABLE) {
                // Priority 1: Check if Immediate update is allowed
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    Log.d(TAG, "Starting Immediate update flow...")
                    startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
                // Priority 2: Check if Flexible update is allowed
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    Log.d(TAG, "Starting Flexible update flow...")
                    startUpdateFlow(appUpdateInfo, AppUpdateType.FLEXIBLE)
                }
            } else if (availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If update is already in progress, resume it
                Log.d(TAG, "Resuming developer-triggered update in progress...")
                startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Check for update failed or Google Play not available: ${exception.message}")
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        val launcher = updateResultLauncher ?: return
        try {
            val options = AppUpdateOptions.newBuilder(updateType).build()
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                launcher,
                options
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error starting update flow for type $updateType", e)
        }
    }

    /**
     * Call in Activity onResume to ensure an uncompleted immediate update
     * or finished download is handled cleanly.
     */
    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                Log.d(TAG, "Update already downloaded in onResume. Completing update...")
                appUpdateManager.completeUpdate()
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                Log.d(TAG, "Resuming in-progress immediate update on onResume...")
                startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error checking update in onResume: ${e.message}")
        }
    }

    /**
     * Call in Activity onDestroy to unregister listeners.
     */
    fun onDestroy() {
        try {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering installStateUpdatedListener", e)
        }
    }
}
