package com.jomof.androidcppplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.cpp.CppLibrary
import org.gradle.nativeplatform.plugins.NativeComponentPlugin

@Suppress("unused")
class AndroidCppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(NativeComponentPlugin::class.java)
        project.extensions.findByName("library")?.extendForAndroid(project)
        if (project.extensions.findByName("android") == null) {
            val pluginExtension = AndroidDsl()
            project.extensions.add(AndroidDsl::class.java, "android", pluginExtension)
        }
        //error("${library.javaClass}")
    }

    private fun Any.extendForAndroid(project: Project) {
        when(this) {
            is CppLibrary -> extendForAndroid(project)
            else -> error("Don't know how to extend $javaClass")
        }
    }

    private fun CppLibrary.extendForAndroid(project: Project) {

    }
}