package com.jomof.androidcppplugin.ndk

import com.jomof.androidcppplugin.AndroidClangToolchainConfig
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal

fun createNdkAbiModel(
    targetMachine: NativePlatformInternal,
    config : AndroidClangToolchainConfig) : NdkAbiModel {
    val targetArchitecture = targetMachine.architecture.name
    val versionModel = createNdkVersionModel(targetMachine, config)
    return NdkAbiModel(
        target = targetArchitecture,
        ndkVersionModel = versionModel
    )
}
