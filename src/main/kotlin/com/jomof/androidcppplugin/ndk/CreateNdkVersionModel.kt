package com.jomof.androidcppplugin.ndk

import com.jomof.androidcppplugin.AndroidClangToolchainConfig
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import java.io.File
const val DEFAULT_NDK_VERSION = "23.0.7123448"

fun createNdkVersionModel(
    targetMachine: NativePlatformInternal,
    config : AndroidClangToolchainConfig) : NdkVersionModel {
    val ndkVersion = config.ndkVersion ?: DEFAULT_NDK_VERSION
    val sdkFolder = findSdkFolder()
    //val userHome = File(System.getProperty("user.home"))
    //ANDROID_SDK_ROOT
    val ndkFolder = sdkFolder.resolve("ndk/$ndkVersion")


    if (!ndkFolder.exists()) error("Expected '$ndkFolder' to exist")
    val major = ndkVersion.split(".")[0]
    val androidNdkHostTag =
        when {
            targetMachine.operatingSystem.isMacOsX -> "darwin-x86_64"
            targetMachine.operatingSystem.isWindows -> "windows-x86_64"
            targetMachine.operatingSystem.isLinux -> "linux-x86_64"
            else -> error("${targetMachine.operatingSystem}")
        }

    val androidNdkToolchain = ndkFolder.resolve("toolchains/llvm/prebuilt/$androidNdkHostTag")
    if (!androidNdkToolchain.exists()) error("Expected '$androidNdkToolchain' to exist")
    return NdkVersionModel(
        clangPlusPlusExe = androidNdkToolchain.resolve("bin/clang++")
    )
}

private fun sdkFolderFromEnvironment() = System.getenv("ANDROID_SDK_ROOT")
private fun sdkFolderOnMac() : String? {
    val userHome = System.getProperty("user.home")
    val candidate = File(userHome).resolve("Library/Android/sdk")
    if (candidate.isDirectory) return candidate.path
    return null
}

private fun findSdkFolder() : File {
    val androidSdkRoot = sdkFolderFromEnvironment() ?: sdkFolderOnMac()
    return File(androidSdkRoot)
}