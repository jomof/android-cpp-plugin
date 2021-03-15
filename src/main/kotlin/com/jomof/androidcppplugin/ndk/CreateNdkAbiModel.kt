package com.jomof.androidcppplugin.ndk

import com.jomof.androidcppplugin.AndroidClangToolchainConfig

fun createNdkAbiModel(targetArchitecture : String, config : AndroidClangToolchainConfig) : NdkAbiModel {
    val versionModel = createNdkVersionModel(config)
    return NdkAbiModel(
        target = targetArchitecture,
        ndkVersionModel = versionModel
    )
}
