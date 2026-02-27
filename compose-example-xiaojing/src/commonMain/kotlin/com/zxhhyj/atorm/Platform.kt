package com.zxhhyj.atorm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform