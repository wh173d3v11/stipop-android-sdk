package io.stipop

import android.app.Activity
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import io.stipop.model.SPPackage
import java.io.File
import java.io.FileOutputStream
import java.net.URL


public class PackUtils {

    companion object {
        fun downloadAndSaveLocal(activity:Activity, spPackage: SPPackage, responseCallback: () -> Unit) {
            val stickers = spPackage.stickers

            for (sticker in stickers) {
                val packageId = sticker.packageId
                val stickerImg = sticker.stickerImg

                // val encodedString = URLEncoder.encode(stickerImg, "utf-8")

                downloadImage(activity, packageId, stickerImg)
            }
        }

        private fun downloadImage(activity:Activity, packageId: Int, encodedString: String?) {
            if (encodedString == null) {
                return
            }

            val fileName = encodedString.split(File.separator).last()
            var filePath = File(activity.filesDir, "stipop/$packageId/$fileName")
            if (filePath.isDirectory) {
                filePath.delete()
            }
            filePath = File(activity.filesDir, "stipop/$packageId")
            filePath.mkdirs()
            filePath = File(activity.filesDir, "stipop/$packageId/$fileName")

            println("filePath : $filePath")

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            URL(encodedString).openStream().use { input ->
                FileOutputStream(filePath).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}