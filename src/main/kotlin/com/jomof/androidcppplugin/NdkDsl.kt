package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.createAndroidClangToolchainConfig
import org.gradle.api.model.ObjectFactory
import org.gradle.nativeplatform.TargetMachine

class NdkDsl(private val objectFactory: ObjectFactory, private val android : Any) {
    @Suppress("unused")
    fun target(target : String) : TargetMachine {
        val config = createAndroidClangToolchainConfig(android)
        return NdkTargetMachineBuilder(objectFactory, config).architecture(target)
    }
}