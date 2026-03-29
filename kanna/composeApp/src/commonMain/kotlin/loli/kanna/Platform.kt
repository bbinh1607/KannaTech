package loli.kanna

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform