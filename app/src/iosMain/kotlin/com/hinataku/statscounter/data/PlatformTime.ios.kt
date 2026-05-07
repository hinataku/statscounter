@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.hinataku.statscounter.data

import platform.posix.time

actual fun currentTimeMillis(): Long = time(null).toLong() * 1000
