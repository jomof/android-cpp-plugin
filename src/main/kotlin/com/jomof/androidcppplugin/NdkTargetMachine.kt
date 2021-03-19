package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.NdkTargetCoordinate
import org.gradle.nativeplatform.MachineArchitecture
import org.gradle.nativeplatform.OperatingSystemFamily
import org.gradle.nativeplatform.TargetMachine
import org.gradle.nativeplatform.TargetMachineBuilder
import org.gradle.api.model.ObjectFactory
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

class NdkTargetMachineBuilder(
    private val objectFactory : ObjectFactory,
    private val config : AndroidClangToolchainConfig) : TargetMachineBuilder {
    override fun getOperatingSystemFamily() = TODO("Not yet implemented")

    override fun getArchitecture() = TODO("Not yet implemented")

    override fun getX86() = TODO("Not yet implemented")

    override fun getX86_64() = TODO("Not yet implemented")

    override fun architecture(architecture: String): TargetMachine {
        val host = DefaultNativePlatform.host()
        val operatingSystemFamily = objectFactory.named(
            OperatingSystemFamily::class.java, host.operatingSystem.toFamilyName()
        )
        val targetCoordinate = NdkTargetCoordinate.parse(architecture)
        val targetCoordinateWithNdk =
            if (targetCoordinate.ndk == null) targetCoordinate.copy(ndk = config.ndkVersion)
            else targetCoordinate
        return NdkTargetMachine(
            operatingSystemFamily,
            objectFactory.named(MachineArchitecture::class.java, "$targetCoordinateWithNdk"))
    }
}

data class NdkTargetMachine(
    private val operatingSystemFamily: OperatingSystemFamily,
    private val architecture: MachineArchitecture) : TargetMachine {
    override fun getOperatingSystemFamily() = operatingSystemFamily
    override fun getArchitecture() = architecture
}

