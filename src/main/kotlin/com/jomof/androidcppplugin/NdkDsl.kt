package com.jomof.androidcppplugin

import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.TargetMachine
import org.gradle.nativeplatform.TargetMachineBuilder
import org.gradle.nativeplatform.TargetMachineFactory

class NdkDsl(private val targetMachineFactory : TargetMachineFactory) {
    @Suppress("unused")
    fun target(target : String) : TargetMachine {
        val host = OperatingSystem.current()
        val hostTarget = when {
            host.isLinux -> targetMachineFactory.linux
            host.isWindows -> targetMachineFactory.windows
            host.isMacOsX -> targetMachineFactory.macOS
            else -> error(host.familyName)
        }
        return NdkTargetMachine(hostTarget).architecture(target)
    }
}