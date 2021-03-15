package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.NdkAbiModel
import org.gradle.api.Transformer
import org.gradle.internal.Transformers
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.process.ArgWriter
import org.gradle.internal.work.WorkerLeaseService
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.toolchain.internal.*
import org.gradle.nativeplatform.toolchain.internal.compilespec.CppCompileSpec
import java.io.File
import java.util.*

/**
 * Uses an option file for arguments passed to GCC if possible.
 * Certain GCC options do not function correctly when included in an option file, so include these directly on the command line as well.
 */
internal class GccOptionsFileArgsWriter(tempDir: File?) : OptionsFileArgsWriter(tempDir) {
    public override fun transformArgs(originalArgs: List<String>, tempDir: File): List<String> {
        val commandLineOnlyArgs = getCommandLineOnlyArgs(originalArgs)
        val finalArgs: MutableList<String> = mutableListOf()
        finalArgs.addAll(
            ArgWriter.argsFileGenerator(File(tempDir, "options.txt"), ArgWriter.unixStyleFactory())
                .transform(originalArgs)
        )
        finalArgs.addAll(commandLineOnlyArgs)
        return finalArgs
    }

    private fun getCommandLineOnlyArgs(allArgs: List<String>): List<String> {
        val commandLineOnlyArgs: MutableList<String> = ArrayList(allArgs)
        commandLineOnlyArgs.retainAll(CLI_ONLY_ARGS)
        return commandLineOnlyArgs
    }

    companion object {
        private val CLI_ONLY_ARGS = Arrays.asList("-m32", "-m64")
    }
}

/**
 * Maps common options for C/C++ compiling with GCC
 */
internal abstract class GccCompilerArgsTransformer<T : NativeCompileSpec?>(val abi : NdkAbiModel) :
    ArgsTransformer<T> {
    override fun transform(spec: T): List<String?> {
        val args: MutableList<String?> = mutableListOf()
        args.add("--target=${abi.target}")
        addToolSpecificArgs(spec, args)
        addMacroArgs(spec, args)
        addUserArgs(spec, args)
        addIncludeArgs(spec, args)
        return args
    }

    private fun addToolSpecificArgs(spec: T, args: MutableList<String?>) {
        Collections.addAll(args, "-x", language)
        args.add("-c")
        if (spec!!.isPositionIndependentCode) {
            if (!spec.targetPlatform.operatingSystem.isWindows) {
                args.add("-fPIC")
            }
        }
        if (spec.isDebuggable) {
            args.add("-g")
        }
        if (spec.isOptimized) {
            args.add("-O3")
        }
    }

    private fun addIncludeArgs(spec: T, args: MutableList<String?>) {
        if (!needsStandardIncludes(spec!!.targetPlatform)) {
            args.add("-nostdinc")
        }
        for (file in spec.includeRoots) {
            args.add("-I")
            args.add(file.absolutePath)
        }
        for (file in spec.systemIncludeRoots) {
            args.add("-isystem")
            args.add(file.absolutePath)
        }
    }

    private fun addMacroArgs(spec: T, args: MutableList<String?>) {
        for (macroArg in MacroArgsConverter().transform(
            spec!!.macros
        )) {
            args.add("-D$macroArg")
        }
    }

    private fun addUserArgs(spec: T, args: MutableList<String?>) {
        args.addAll(spec!!.allArgs)
    }

    protected open fun needsStandardIncludes(targetPlatform: NativePlatform): Boolean {
        return targetPlatform.operatingSystem.isMacOsX
    }

    protected abstract val language: String?
}

internal open class GccCompatibleNativeCompiler<T : NativeCompileSpec?>(
    buildOperationExecutor: BuildOperationExecutor?,
    compilerOutputFileNamingSchemeFactory: CompilerOutputFileNamingSchemeFactory?,
    commandLineTool: CommandLineToolInvocationWorker?,
    invocationContext: CommandLineToolContext?,
    argsTransformer: ArgsTransformer<T>?,
    specTransformer: Transformer<T, T>?,
    objectFileExtension: String?,
    useCommandFile: Boolean,
    workerLeaseService: WorkerLeaseService?
) :
    NativeCompiler<T>(
        buildOperationExecutor,
        compilerOutputFileNamingSchemeFactory,
        commandLineTool,
        invocationContext,
        argsTransformer,
        specTransformer,
        objectFileExtension,
        useCommandFile,
        workerLeaseService
    ) {
    override fun getOutputArgs(spec: T, outputFile: File): List<String> {
        return Arrays.asList("-o", outputFile.absolutePath)
    }

    override fun addOptionsFileArgs(args: List<String>, tempDir: File) {
        val writer: OptionsFileArgsWriter = GccOptionsFileArgsWriter(tempDir)
        // modifies args in place
        writer.execute(args)
    }

    override fun getPCHArgs(spec: T): List<String> {
        val pchArgs: MutableList<String> = ArrayList()
        if (spec!!.prefixHeaderFile != null) {
            pchArgs.add("-include")
            pchArgs.add(spec.prefixHeaderFile.absolutePath)
        }
        return pchArgs
    }
}

internal class CppCompiler(
    abi : NdkAbiModel,
    buildOperationExecutor: BuildOperationExecutor?,
    compilerOutputFileNamingSchemeFactory: CompilerOutputFileNamingSchemeFactory?,
    commandLineToolInvocationWorker: CommandLineToolInvocationWorker?,
    invocationContext: CommandLineToolContext?,
    objectFileExtension: String?,
    useCommandFile: Boolean,
    workerLeaseService: WorkerLeaseService?
) :
    GccCompatibleNativeCompiler<CppCompileSpec?>(
        buildOperationExecutor,
        compilerOutputFileNamingSchemeFactory,
        commandLineToolInvocationWorker,
        invocationContext,
        CppCompileArgsTransformer(abi),
        Transformers.noOpTransformer(),
        objectFileExtension,
        useCommandFile,
        workerLeaseService
    ) {
    private class CppCompileArgsTransformer(abi : NdkAbiModel) :
        GccCompilerArgsTransformer<CppCompileSpec?>(abi) {
        override val language: String
            get() = "c++"
    }
}