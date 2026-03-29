package loli.kanna

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android Device (API ${Build.VERSION.SDK_INT})"
}

actual fun getPlatform(): Platform = AndroidPlatform()
