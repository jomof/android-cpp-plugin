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
        val notAvailable = object : PlatformToolProvider {
            override fun isAvailable() = false
            override fun isSupported() = false
            override fun explain(visitor: DiagnosticsVisitor) {

            }
            override fun <T : CompileSpec?> newCompiler(spec: Class<T>?) = error("Not available")
            override fun <T : Any?> get(toolType: Class<T>?) = error("Not available")
            override fun getObjectFileExtension() = error("Not available")
            override fun getExecutableName(executablePath: String?) = error("Not available")
            override fun getSharedLibraryName(libraryPath: String?) = error("Not available")
            override fun producesImportLibrary() = error("Not available")
            override fun requiresDebugBinaryStripping() = error("Not available")
            override fun getImportLibraryName(libraryPath: String?) = error("Not available")
            override fun getSharedLibraryLinkFileName(libraryPath: String?) = error("Not available")
            override fun getStaticLibraryName(libraryPath: String?) = error("Not available")
            override fun getExecutableSymbolFileName(executablePath: String?) = error("Not available")
            override fun getLibrarySymbolFileName(libraryPath: String?) = error("Not available")
            override fun getCompilerMetadata(compilerType: ToolType?) = error("Not available")
            override fun getSystemLibraries(compilerType: ToolType?) = error("Not available")
            override fun locateTool(compilerType: ToolType?) = error("Not available")
        }
    }

    override fun select(targetPlatform: NativePlatformInternal): PlatformToolProvider {
        if (!targetPlatform.isAndroid) return notAvailable
        return createAndroidPlatformToolProvider(serviceRegistry, targetPlatform, createConfig())
    }

    override fun select(
        sourceLanguage: NativeLanguage,
        targetMachine: NativePlatformInternal
    ): PlatformToolProvider {
        if (!targetMachine.isAndroid) return notAvailable
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