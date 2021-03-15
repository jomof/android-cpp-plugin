package com.jomof.androidcppplugin

import org.gradle.nativeplatform.TargetMachine
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal

val TargetMachine.isAndroid : Boolean get() =
    operatingSystemFamily.isLinux &&
            architecture.name.contains("android")

val NativePlatformInternal.isAndroid : Boolean get() =
    architecture.name.contains("android")