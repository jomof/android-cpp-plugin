package com.jomof.androidcppplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.internal.service.ServiceRegistry
import org.gradle.language.cpp.CppLibrary
import org.gradle.language.cpp.CppPlatform
import org.gradle.language.cpp.internal.DefaultCppLibrary
import org.gradle.language.cpp.internal.DefaultCppPlatform
import org.gradle.language.cpp.internal.NativeVariantIdentity
import org.gradle.language.nativeplatform.internal.Dimensions
import org.gradle.language.nativeplatform.internal.toolchains.ToolChainSelector
import org.gradle.nativeplatform.Linkage
import org.gradle.nativeplatform.TargetMachineFactory
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.plugins.NativeComponentPlugin
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal
import java.util.concurrent.Callable
import javax.inject.Inject

@Suppress("unused")
class AndroidCppPlugin @Inject constructor(
    private val serviceRegistry: ServiceRegistry,
    private val targetMachineFactory: TargetMachineFactory,
    private val attributesFactory: ImmutableAttributesFactory,
    private val toolChainSelector: ToolChainSelector
): Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(NativeComponentPlugin::class.java)
        project.extensions.findByName("library")?.extendForAndroid(project)
        if (project.extensions.findByName("android") == null) {
            val pluginExtension = AndroidDsl()
            project.extensions.add(AndroidDsl::class.java, "android", pluginExtension)
        }
        //error("${library.javaClass}")
        //setupWith(project)
        val toolChainRegistry = project.extensions.getByType(NativeToolChainRegistry::class.java)

        toolChainRegistry.registerFactory(AndroidClang::class.java) { name ->
            val ndkVersion : String? =
                when(val android = project.extensions.getByName("android")) {
                    null -> null
                    is AndroidDsl -> android.ndkVersion
                    else -> error("${android.javaClass}")
                }
            val config = AndroidClangToolchainConfig(
                ndkVersion = ndkVersion
            )
            AndroidClangToolchain(name, config, serviceRegistry)
        }
        toolChainRegistry.register(AndroidClangToolchain.NAME, AndroidClang::class.java)
    }

    private fun Any.extendForAndroid(project: Project) {
        when(this) {
            is DefaultCppLibrary -> extendForAndroid(project)
            else -> error("Don't know how to extend $javaClass")
        }
    }

    private fun DefaultCppLibrary.extendForAndroid(project: Project) {
//        project.afterEvaluate {
//            Dimensions.libraryVariants(
//                baseName,
//                linkage,
//                targetMachines,
//                project.objects,
//                attributesFactory,
//                project.providers.provider { project.group.toString() },
//                project.providers.provider { project.version.toString() }
//            ) { variantIdentity: NativeVariantIdentity ->
//                if (variantIdentity.targetMachine.isAndroid) {
//                    val result: ToolChainSelector.Result<CppPlatform> =
//                        toolChainSelector.select(
//                            CppPlatform::class.java,
//                            DefaultCppPlatform(variantIdentity.targetMachine)
//                        )
//                    if (variantIdentity.linkage == Linkage.SHARED) {
//                        addSharedLibrary(
//                            variantIdentity,
//                            result.targetPlatform,
//                            result.toolChain,
//                            result.platformToolProvider
//                        )
//                    } else {
//                        addStaticLibrary(
//                            variantIdentity,
//                            result.targetPlatform,
//                            result.toolChain,
//                            result.platformToolProvider
//                        )
//                    }
//                }
//            }
//        }
    }

    private fun setupWith(project: Project) {
        project.beforeEvaluate {
            project.extensions.configure(CppLibrary::class.java) { cppLibrary ->
                listOf("x86", "x86_64")
                    .map { abi -> AndroidInfo(abi) }
                    .map {
                        targetMachineFactory
                            .linux
                            .architecture(it.platformName)
                    }.forEach {
                        error(it)
                        cppLibrary.targetMachines.add(it)
                    }
            }
        }
    }
}