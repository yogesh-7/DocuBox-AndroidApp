package com.docubox.data.modes.local

import androidx.annotation.DrawableRes
import com.docubox.R
import com.google.gson.annotations.SerializedName

// Class to get media type of a file
sealed class FileType(
    val type: String,
    @DrawableRes val icon: Int,
    val mimeType:String
) {
    object Audio : FileType("Audio", R.drawable.ic_audio, "audio")
    object Document : FileType("Documents", R.drawable.ic_document, "application/pdf")
    object Image : FileType("Image", R.drawable.ic_image, "image")
    object Video : FileType("Video", R.drawable.ic_video, "video")
    object File : FileType("File", R.drawable.ic_document, "application")
}
