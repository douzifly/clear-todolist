package douzifly.list.backup

import douzifly.list.settings.Settings
import douzifly.list.utils.logd
import douzifly.list.utils.loge
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


data class Backup(val name:String, val path:String) {

    companion object {
        fun parseFromFile(f: File) : Backup? {
            try {
                val name = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(f.name))
                val backup = Backup(name, f.path)
                return backup
            } catch(e: Exception) {
                "parseFromFile exp: ${e.message}".loge("Backup")
                return null
            }
        }
    }

}

object BackupHelper {

    val TAG = "BackupHelper"

    public fun listBackupFiles(): List<Backup> {
        val dir = File(Settings.BACKUP_DIR)
        if (!dir.exists())
            return arrayListOf()
        val files = dir.listFiles()
        val backups = arrayListOf<Backup>()
        files.forEach {
            file->
            val backup = Backup.parseFromFile(file)
            if (backup != null) {
                backups.add(backup)
                "found backup: ${backup}".logd(TAG)
            }
        }
        return backups
    }


    fun resotre(backupDB: File, restoreDbName: String): Boolean {
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
            return true
        } catch (e:Exception) {
            "restore failed ${e.message}".loge(TAG)
        }
        return false
    }

    fun backup(dbName: String): String {
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
        } catch (e:Exception) {
            "backup failed ${e.message}".loge(TAG)
        }
        return ""
    }
}

