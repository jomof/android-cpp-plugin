package com.jomof.androidcppplugin

import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.logging.text.DiagnosticsVisitor
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.service.ServiceRegistry
import org.gradle.nativeplatform.toolchain.GccPlatformToolChain
import org.gradle.internal.os.OperatingSystem
import org.gradle.language.base.internal.compile.CompileSpec
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.toolchain.internal.*


class AndroidClangToolchain(
    name: String,
    private val android: Any,
    private val serviceRegistry: ServiceRegistry,
    buildOperationExecutor: BuildOperationExecutor = serviceRegistry.get(BuildOperationExecutor::class.java),
    operatingSystem: OperatingSystem = OperatingSystem.current(),
    fileResolver: FileResolver = serviceRegistry.get(FileResolver::class.java)
) : ExtendableToolChain<GccPlatformToolChain>(
    name,
    buildOperationExecutor,
    operatingSystem,
    fileResolver
), AndroidClang {
    companion object {
        const val NAME = "Android Clang"
    }

    override fun select(targetPlatform: NativePlatformInternal): PlatformToolProvider {
        if (!targetPlatform.isAndroid)
            return UnsupportedPlatformToolProvider(targetPlatform.operatingSystem, "Was not Android")
        return createAndroidPlatformToolProvider(serviceRegistry, targetPlatform, createConfig())
    }

    override fun select(
        sourceLanguage: NativeLanguage,
        targetMachine: NativePlatformInternal
    ): PlatformToolProvider {
        if (!targetMachine.isAndroid)
            return UnsupportedPlatformToolProvider(targetMachine.operatingSystem, "Was not Android")
        return createAndroidPlatformToolProvider(serviceRegistry, targetMachine, createConfig())
    }

    private fun createConfig() : AndroidClangToolchainConfig {
        when(android) {
            is AndroidDsl -> return AndroidClangToolchainConfig(
                ndkVersion = android.ndkVersion!!
            )
            else -> error("${android.javaClass}")
        }
    }

    override fun getTypeName() = NAME
}