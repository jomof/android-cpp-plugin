package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.NdkTargetCoordinate
import com.jomof.androidcppplugin.ndk.createNdkAbiModel
import org.gradle.internal.logging.text.DiagnosticsVisitor
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.platform.internal.OperatingSystemInternal
import org.gradle.nativeplatform.toolchain.internal.*
import org.gradle.nativeplatform.toolchain.internal.compilespec.*
import org.gradle.nativeplatform.toolchain.internal.metadata.CompilerMetadata
import org.gradle.nativeplatform.toolchain.internal.tools.CommandLineToolSearchResult
import java.io.File
import org.gradle.internal.service.ServiceRegistry
import org.gradle.internal.work.WorkerLeaseService
import org.gradle.language.base.internal.compile.DefaultCompilerVersion
import org.gradle.language.base.internal.compile.VersionAwareCompiler
import org.gradle.nativeplatform.internal.LinkerSpec
import org.gradle.process.internal.ExecActionFactory
import org.gradle.util.VersionNumber

private val linuxOs : org.gradle.internal.os.OperatingSystem = org.gradle.internal.os.OperatingSystem.LINUX

private object AndroidOs : OperatingSystemInternal {
    override fun getName() = "${linuxOs.name} OperatingSystemInternal:name"
    override fun getDisplayName() = "${linuxOs.name} OperatingSystemInternal:Display Name"
    override fun isCurrent() = false
    override fun isWindows() = false
    override fun isMacOsX() = false
    override fun isLinux() = true
    override fun isSolaris() = true
    override fun isFreeBSD() = false
    override fun getInternalOs() = linuxOs
    override fun toFamilyName() = "android"
}

fun createAndroidPlatformToolProvider(
    serviceRegistry: ServiceRegistry,
    targetMachine: NativePlatformInternal,
    toolchainConfig: AndroidClangToolchainConfig) : PlatformToolProvider {
    val targetCoordinate = NdkTargetCoordinate.parse(targetMachine.architecture.name)
    val abi = createNdkAbiModel(targetCoordinate)
    val ndkVersionNumber = VersionNumber.parse(toolchainConfig.ndkVersion)
    val buildOperationExecutor = serviceRegistry.get(BuildOperationExecutor::class.java)
    val compilerOutputFileNamingSchemeFactory = serviceRegistry.get(CompilerOutputFileNamingSchemeFactory::class.java)
    val workerLeaseService = serviceRegistry.get(WorkerLeaseService::class.java)
    val execActionFactory = serviceRegistry.get(ExecActionFactory::class.java)

    return object : AbstractPlatformToolProvider(buildOperationExecutor, AndroidOs) {
        override fun isAvailable() = true
        override fun isSupported() = true
        override fun explain(visitor: DiagnosticsVisitor?) {
            TODO("Not yet implemented")
        }

        override fun <T : Any?> get(toolType: Class<T>?): T {
            TODO("Not yet implemented")
        }

        override fun getObjectFileExtension(): String = ".o"

        override fun getExecutableName(executablePath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getSharedLibraryName(libraryPath: String): String {
            // libraryPath is, for example, lib/main/debug/macos/hello-world

            // For example, lib/main/debug
            val baseFolder = File(libraryPath).parentFile.parentFile
            // For example, hello-world
            val baseName = File(libraryPath).nameWithoutExtension
            // For example, android/x86
            val androidSegment = targetMachine.architecture.name.replace("-", "/")
            // For example, libhello-world.so
            val soName = "lib$baseName.so"
            // For example, lib/main/debug/android/x86/libhello-world.so
            return baseFolder.resolve(androidSegment).resolve(soName).path
        }

        override fun producesImportLibrary() = false
        override fun requiresDebugBinaryStripping() = true

        override fun getImportLibraryName(libraryPath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getSharedLibraryLinkFileName(libraryPath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getStaticLibraryName(libraryPath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getExecutableSymbolFileName(executablePath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getLibrarySymbolFileName(libraryPath: String?): String {
            TODO("Not yet implemented")
        }

        override fun getCompilerMetadata(compilerType: ToolType?): CompilerMetadata {
            TODO("Not yet implemented")
        }

        override fun getSystemLibraries(compilerType: ToolType?): SystemLibraries {
            return object : SystemLibraries {
                override fun getIncludeDirs() = listOf<File>()
                override fun getLibDirs()  = listOf<File>()
                override fun getPreprocessorMacros() = mapOf<String, String>()
            }
        }

        override fun locateTool(compilerType: ToolType?): CommandLineToolSearchResult {
            TODO("Not yet implemented")
        }

        private fun context(): CommandLineToolContext {
            val baseInvocation = DefaultMutableCommandLineToolContext()
//            baseInvocation.addPath(toolSearchPath.path)
//            baseInvocation.argAction = toolConfiguration?.argAction
            return baseInvocation
        }

        override fun createCppCompiler(): Compiler<CppCompileSpec> {
            val commandLineToolInvocationWorker = DefaultCommandLineToolInvocationWorker(
                "clang.exe",
                abi.ndkVersionModel.clangPlusPlusExe,
                execActionFactory
            )
            val cppCompiler = CppCompiler(
                abi,
                buildOperationExecutor,
                compilerOutputFileNamingSchemeFactory,
                commandLineToolInvocationWorker,
                context(),
                objectFileExtension,
                useCommandFile = false,
                workerLeaseService
            )

            val outputCleaning = OutputCleaningCompiler<CppCompileSpec>(
                cppCompiler,
                compilerOutputFileNamingSchemeFactory,
                objectFileExtension
            )

            return VersionAwareCompiler(
                outputCleaning,
                DefaultCompilerVersion(
                    ToolType.CPP_COMPILER.toolName,
                    "Google",
                    ndkVersionNumber
                )
            )
        }

        override fun createLinker(): Compiler<LinkerSpec> {
            val commandLineToolInvocationWorker = DefaultCommandLineToolInvocationWorker(
                "clang.exe",
                abi.ndkVersionModel.clangPlusPlusExe,
                execActionFactory
            )
            val linker : Compiler<LinkerSpec> =
                GccLinker(
                    abi,
                    buildOperationExecutor,
                    commandLineToolInvocationWorker,
                    context(),
                    useCommandFile = true,
                    workerLeaseService
                )
            return VersionAwareCompiler(
                linker,
                DefaultCompilerVersion(
                    ToolType.LINKER.toolName,
                    "Google",
                    ndkVersionNumber
                )
            )
        }
    }
}