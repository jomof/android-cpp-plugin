package com.jomof.androidcppplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.nativeplatform.plugins.NativeComponentPlugin

class AndroidCppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(NativeComponentPlugin::class.java)
    }
}