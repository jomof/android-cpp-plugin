package com.jomof.androidcppplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.service.ServiceRegistry
import org.gradle.language.cpp.internal.DefaultCppLibrary
import org.gradle.language.nativeplatform.internal.toolchains.ToolChainSelector
import org.gradle.nativeplatform.TargetMachineFactory
import org.gradle.nativeplatform.plugins.NativeComponentPlugin
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry
import javax.inject.Inject

@Suppress("unused")
class AndroidCppPlugin @Inject constructor(
    private val objectFactory: ObjectFactory,
    private val serviceRegistry: ServiceRegistry,
    private val targetMachineFactory: TargetMachineFactory,
    private val attributesFactory: ImmutableAttributesFactory,
    private val toolChainSelector: ToolChainSelector
): Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(NativeComponentPlugin::class.java)
        project.extensions.findByName("library")?.extendForAndroid(project)
        val androidExtension = AndroidDsl()
        if (project.extensions.findByName("android") == null) {
            project.extensions.add(AndroidDsl::class.java, "android", androidExtension)
        }
        val ndkExtension = NdkDsl(objectFactory, androidExtension)
        project.extensions.add(NdkDsl::class.java, "ndk", ndkExtension)
        //error("${library.javaClass}")
        //setupWith(project)
        val toolChainRegistry = project.extensions.getByType(NativeToolChainRegistry::class.java)

        toolChainRegistry.registerFactory(AndroidClang::class.java) { name ->
            val android = project.extensions.getByName("android")
            AndroidClangToolchain(
                name,
                android,
                serviceRegistry)
        }
        toolChainRegistry.register(AndroidClangToolchain.NAME, AndroidClang::class.java)
    }

    private fun Any.extendForAndroid(project: Project) {
        when(this) {
            is DefaultCppLibrary -> extendForAndroid()
            else -> error("Don't know how to extend $javaClass")
        }
    }

    private fun DefaultCppLibrary.extendForAndroid() {
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
}