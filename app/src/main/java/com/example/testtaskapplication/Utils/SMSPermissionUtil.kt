    package com.example.testtaskapplication.Utils

    import android.app.Activity
    import android.content.pm.PackageManager
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.example.testtaskapplication.constant.TaskConstant

    class SMSPermissionUtil(private val activity: Activity) {


        private val SMS_PERMISSIONS = arrayOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
        )

        fun requestSmsPermissions() {
            if (!hasSmsPermissions()) {
                ActivityCompat.requestPermissions(activity, SMS_PERMISSIONS, TaskConstant.SMS_PERMISSION_REQUEST_CODE)
            }
        }

         fun hasSmsPermissions(): Boolean {
             return SMS_PERMISSIONS.all {
                 ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
             }
        }
    }