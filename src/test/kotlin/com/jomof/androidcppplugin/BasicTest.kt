package com.jomof.androidcppplugin

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException




class BasicTest {
    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()
    private var settingsFile: File? = null
    private var buildFile: File? = null

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts")
        buildFile = testProjectDir.newFile("build.gradle.kts")
    }

    @Test
    fun basic() {
        val source = testProjectDir.root.resolve("src/main/cpp/my-source.cpp")
        source.absoluteFile.parentFile.mkdirs()
        source.writeText("""
            int main() { return 0; }
        """.trimIndent())
        settingsFile!!.writeText("""
            rootProject.name = "hello-world"
        """.trimIndent())
        buildFile!!.writeText("""
            plugins {
                id("cpp-library")
                id("cpp-android")
            }
            android {
                ndkVersion = "23.0.7123448"
            }
            library {
               // targetMachines.add(machines.linux.x86_64)
                linkage.add(Linkage.SHARED)
            }
            tasks.register("helloWorld") {
                doLast {
                    println("Hello world!")
                }
            }
        """.trimIndent())
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("assembleDebug")
            .build()

        //assertTrue(result.output.contains("Hello world!"))
        assertEquals(TaskOutcome.SUCCESS, result.task(":assembleDebug")?.outcome)
    }
}