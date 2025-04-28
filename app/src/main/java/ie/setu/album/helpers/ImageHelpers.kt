package ie.setu.album.helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import ie.setu.album.R

fun showImagePicker(intentLauncher: ActivityResultLauncher<Intent>) {
    val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
    }

    val chooser = Intent.createChooser(pickIntent, R.string.select_album_image.toString()).apply {
        flags = pickIntent.flags
    }
    intentLauncher.launch(chooser)
}


