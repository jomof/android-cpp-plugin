package com.jomof.androidcppplugin.ndk

import com.jomof.androidcppplugin.AndroidClangToolchainConfig
import java.io.File
private const val DEFAULT_NDK_VERSION = "23.0.7123448"

fun createNdkVersionModel(config : AndroidClangToolchainConfig) : NdkVersionModel {
    val ndkVersion = config.ndkVersion ?: DEFAULT_NDK_VERSION
    val userHome = File(System.getProperty("user.home"))
    val ndkFolder = userHome.resolve("Library/Android/sdk/ndk/$ndkVersion")
    if (!ndkFolder.exists()) error("Expected '$ndkFolder' to exist")
    val major = ndkVersion.split(".")[0]
    val androidNdkHostTag = "darwin-x86_64"
    val androidNdkToolchain = ndkFolder.resolve("toolchains/llvm/prebuilt/$androidNdkHostTag")
    if (!androidNdkToolchain.exists()) error("Expected '$androidNdkToolchain' to exist")
    return NdkVersionModel(
        clangPlusPlusExe = androidNdkToolchain.resolve("bin/clang++")
    )
}