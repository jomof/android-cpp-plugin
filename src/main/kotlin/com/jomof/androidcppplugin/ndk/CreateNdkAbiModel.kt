package com.jomof.androidcppplugin.ndk

import com.jomof.androidcppplugin.AndroidClangToolchainConfig
import com.jomof.androidcppplugin.AndroidDsl
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal

fun createAndroidClangToolchainConfig(androidDsl : Any) : AndroidClangToolchainConfig {
    when(androidDsl) {
        is AndroidDsl -> return AndroidClangToolchainConfig(
            ndkVersion = androidDsl.ndkVersion!!
        )
        else -> error("${androidDsl.javaClass}")
    }
}

fun createNdkAbiModel(coordinate : NdkTargetCoordinate) : NdkAbiModel {
    val versionModel = createNdkVersionModel(coordinate)
    return NdkAbiModel(
        targetCoordinate = coordinate,
        ndkVersionModel = versionModel
    )
}

/*
arm64-v8a       aarch64-linux-android21
armeabi-v7a     armv7-linux-androideabi19
x86             i686-linux-android19
x86_64          x86_64-linux-android21
 */
