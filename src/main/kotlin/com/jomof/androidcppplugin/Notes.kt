@file:Suppress("unused")
package com.jomof.androidcppplugin

import org.gradle.language.cpp.CppLibrary
import org.gradle.language.cpp.CppComponent
import org.gradle.language.cpp.internal.NativeVariantIdentity
import org.gradle.language.cpp.internal.DefaultCppLibrary
import org.gradle.language.cpp.internal.DefaultCppComponent
import org.gradle.language.cpp.internal.DefaultCppSharedLibrary
import org.gradle.language.cpp.plugins.CppBasePlugin
import org.gradle.language.cpp.plugins.CppLibraryPlugin
import org.gradle.language.nativeplatform.internal.BuildType
import org.gradle.language.nativeplatform.internal.Dimensions
import org.gradle.nativeplatform.Linkage
import org.gradle.nativeplatform.internal.DefaultTargetMachineFactory
import org.gradle.nativeplatform.TargetMachine

/**
 * [CppLibraryPlugin] introduces 'library' [CppLibrary] stanza in build.gradle.
 * Example,
 *  library {
 *    targetMachines.add(machines.macOS.x86_64)
 *    linkage.add(Linkage.SHARED)
 *  }
 *
 *  In above,
 *  - 'machines' is exposed by [DefaultTargetMachineFactory]
 *
 *  Imports the plugin [CppBasePlugin].
 *
 *  Creates one library per [BuildType], per [Linkage], per [TargetMachine] by:
 *  calling [DefaultCppLibrary.addSharedLibrary] to:
 *  - Create a new [DefaultCppSharedLibrary]
 *  - Add it as a binary to [CppComponent]
 *
 * Crucially, due to [Dimensions.tryToBuildOnHost], will only create a library if
 * current _host_ operating system family name is equal to the [targetMachine]'s
 * operatingSystemFamily.name. This seems to indicate that [CppLibraryPlugin] is
 * not meant to for cross compilation.
 */
private val cppLibraryPlugin : CppLibraryPlugin get() { error("don't") }


private val targetMachine: TargetMachine get() { error("don't") }

/**
 * The 'library' stanza in the DSL. Typically implemented by [DefaultCppLibrary]
 * which extends [DefaultCppComponent].
 */
private val cppLibrary : CppLibrary  get() { error("don't") }

/**
 *
 */
private val cppBasePlugin : CppBasePlugin get() { error("don't") }


private val nativeVariantIdentity : NativeVariantIdentity get() { error("don't") }




