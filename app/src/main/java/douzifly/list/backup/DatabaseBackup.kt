package douzifly.list.backup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import douzifly.list.ListApplication
import douzifly.list.R
import douzifly.list.model.ThingGroup
import douzifly.list.settings.Settings
import douzifly.list.utils.logd
import douzifly.list.utils.loge
import douzifly.list.utils.toResString
import douzifly.list.utils.ui
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


data class Backup(val name: String, val path: String) {

    companion object {
        fun parseFromFile(f: File): Backup? {
            try {
                val name = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(f.name.toLong()))
                val backup = Backup(name, f.path)
                return backup
            } catch(e: Exception) {
                "parseFromFile exp: ${e.message}".loge("Backup")
                return null
            }
        }
    }

    override fun toString(): String {
        return name
    }

}

object BackupHelper {

    val TAG = "BackupHelper"

    val MY_PERMISSIONS_REQUEST_SDCARD = 1

    val PENDING_LIST_BACKUP = 1
    val PENDING_BACKUP = 2
    val PENDING_RESTORE = 3

    var pendingOperation = 0

    val PERMISSION_GRANT = 1
    val PERMISSION_DECLINE = 2
    val PERMISSION_PENDING = 3

    var currentPermissionStatus = PERMISSION_GRANT

    val needCheckPermisson by lazy { Build.VERSION.SDK_INT >= Build.VERSION_CODES.M }

    /**
     * @param readOrWrite 0 read, other write
     *
     */
    public fun checkPermission(activity: Activity, readOrWrite: Int, pendingType: Int): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            currentPermissionStatus = PERMISSION_GRANT
            return PERMISSION_GRANT
        }

        val requestPermission = if (readOrWrite == 0) Manifest.permission.READ_EXTERNAL_STORAGE
        else Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(activity, requestPermission) != PackageManager.PERMISSION_GRANTED) {
            // no permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ui {
                    AlertDialog.Builder(activity)
                            .setTitle(R.string.permission_rationable_title.toResString(activity))
                            .setMessage(R.string.permission_rationable_text.toResString(activity))
                            .setPositiveButton(android.R.string.ok.toResString(activity),
                                    { dialog, which ->
                                        ActivityCompat.requestPermissions(activity,
                                                arrayOf(requestPermission),
                                                MY_PERMISSIONS_REQUEST_SDCARD)

                                        pendingOperation = pendingType
                                    })
                            .show();
                }

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        arrayOf(requestPermission),
                        MY_PERMISSIONS_REQUEST_SDCARD)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                pendingOperation = pendingType
            }

            currentPermissionStatus = PERMISSION_PENDING
            return PERMISSION_PENDING
        } else {
            currentPermissionStatus = PERMISSION_GRANT
            return PERMISSION_GRANT
        }
    }

    fun onRequestPermissionsResult(permissions: Array<out String>?, grantResults: IntArray?, reDo:(Int)->Unit) {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults?.count() ?: 0) > 0
                && grantResults?.get(0) == PackageManager.PERMISSION_GRANTED) {

            currentPermissionStatus = PERMISSION_GRANT
            if (pendingOperation > 0) {
                reDo(pendingOperation)
                pendingOperation = 0
            }

        } else {
            currentPermissionStatus == PERMISSION_DECLINE
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            pendingOperation = 0
        }
    }


    public fun listBackupFiles(activity: Activity): List<Backup> {
        if (checkPermission(activity, 0, PENDING_LIST_BACKUP) != PERMISSION_GRANT) {
            return arrayListOf()
        }
        val dir = File(Settings.BACKUP_DIR)
        if (!dir.exists())
            return arrayListOf()
        val files = dir.listFiles()
        val backups = arrayListOf<Backup>()
        files.forEach {
            file ->
            val backup = Backup.parseFromFile(file)
            if (backup != null) {
                backups.add(backup)
                "found backup: ${backup}".logd(TAG)
            }
        }
        backups.sortByDescending {  backup->
            backup.name
        }

        return backups
    }


    fun resotre(backupDB: File, restoreDbName: String, activity: Activity): Boolean {
        if (checkPermission(activity, 0, PENDING_RESTORE) != PERMISSION_GRANT) {
            return false
        }
        try {
            "restore from ${backupDB.path}".logd(TAG)
            val currentDB = "/data/data/douzifly.list/databases/${restoreDbName}"
            val current = File(currentDB)
            if (current.exists()) {
                val ret = current.delete()
                "delete current success: ${ret}".logd(TAG)
                if (!ret) {
                    return false
                }
            }
            val src = FileInputStream(backupDB).channel
            val dst = FileOutputStream(currentDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            "restore success".logd(TAG)
            Settings.selectedGroupId = ThingGroup.SHOW_ALL_GROUP_ID
            return true
        } catch (e: Exception) {
            "restore failed ${e.message}".loge(TAG)
        }
        return false
    }

    fun backup(dbName: String, activity: Activity): String {
        if (checkPermission(activity, 1, PENDING_BACKUP) != PERMISSION_GRANT) {
            return ""
        }
        try {
            val fileName = "${System.currentTimeMillis()}"
            val backupDB = File(Settings.BACKUP_DIR, fileName)

            "backup to ${backupDB.path}".logd(TAG)

            if (!backupDB.parentFile.exists()) {
                val ret = backupDB.parentFile.mkdirs()
                if (!ret) {
                    "can't mkdir parent file".logd(TAG)
                    return ""
                }
            }
            if (backupDB.exists()) {
                backupDB.delete()
                "backup file exists, delete it".logd(TAG)
            }

            val currentDB = "/data/data/douzifly.list/databases/${dbName}"
            val src = FileInputStream(currentDB).channel
            val dst = FileOutputStream(backupDB).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
            "backup success".logd(TAG)
            return fileName
        } catch (e: Exception) {
            "backup failed ${e.message}".loge(TAG)
        }
        return ""
    }
}

