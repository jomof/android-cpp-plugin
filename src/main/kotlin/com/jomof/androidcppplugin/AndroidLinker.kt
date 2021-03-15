package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.NdkAbiModel
import org.gradle.api.Action
import org.gradle.internal.operations.BuildOperationExecutor
import org.gradle.internal.operations.BuildOperationQueue
import org.gradle.internal.work.WorkerLeaseService
import org.gradle.nativeplatform.internal.LinkerSpec
import org.gradle.nativeplatform.internal.SharedLibraryLinkerSpec
import org.gradle.nativeplatform.toolchain.internal.*
import java.io.File
import java.lang.UnsupportedOperationException
import java.util.ArrayList

internal class GccLinker(
    abi : NdkAbiModel,
    buildOperationExecutor: BuildOperationExecutor?,
    commandLineToolInvocationWorker: CommandLineToolInvocationWorker?,
    invocationContext: CommandLineToolContext?,
    useCommandFile: Boolean,
    workerLeaseService: WorkerLeaseService?
) :
    AbstractCompiler<LinkerSpec>(
        buildOperationExecutor,
        commandLineToolInvocationWorker,
        invocationContext,
        GccLinkerArgsTransformer(abi),
        useCommandFile,
        workerLeaseService
    ) {
    override fun newInvocationAction(
        spec: LinkerSpec,
        args: List<String>
    ): Action<BuildOperationQueue<CommandLineToolInvocation>> {
        val invocation = newInvocation(
            "linking " + spec.outputFile.name, args, spec.operationLogger
        )
        return Action<BuildOperationQueue<CommandLineToolInvocation>> { buildQueue ->
            buildQueue.setLogLocation(spec.operationLogger.logLocation)
            buildQueue.add(invocation)
        }
    }

    override fun addOptionsFileArgs(args: List<String>, tempDir: File) {
        GccOptionsFileArgsWriter(tempDir).execute(args)
    }

    private class GccLinkerArgsTransformer(val abi : NdkAbiModel) :
        ArgsTransformer<LinkerSpec?> {
        override fun transform(spec: LinkerSpec?): List<String> {
            val args: MutableList<String> = ArrayList()
            args.addAll(spec!!.systemArgs)
            args.add("--target=${abi.target}")
            if (spec is SharedLibraryLinkerSpec) {
                args.add("-shared")
                maybeSetInstallName(spec, args)
            }
            args.add("-o")
            args.add(spec.outputFile.absolutePath)
            for (file in spec.objectFiles) {
                args.add(file.absolutePath)
            }
            for (file in spec.libraries) {
                args.add(file.absolutePath)
            }
            if (!spec.libraryPath.isEmpty()) {
                throw UnsupportedOperationException("Library Path not yet supported on GCC")
            }
            for (userArg in spec.args) {
                args.add(userArg)
            }
            return args
        }

        private fun maybeSetInstallName(spec: SharedLibraryLinkerSpec, args: MutableList<String>) {
            val installName = spec.installName
            val targetOs = spec.targetPlatform.operatingSystem
            if (installName == null || targetOs.isWindows) {
                return
            }
            if (targetOs.isMacOsX) {
                args.add("-Wl,-install_name,$installName")
            } else {
                args.add("-Wl,-soname,$installName")
            }
        }
    }
}