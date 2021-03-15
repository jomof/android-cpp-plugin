package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.DEFAULT_NDK_VERSION
import junit.framework.Assert.assertEquals
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class BasicTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()
    private var settingsFile: File? = null
    private var buildFile: File? = null
    private var testNdkVersion: String? = null

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts")
        buildFile = testProjectDir.newFile("build.gradle.kts")
        testNdkVersion = System.getenv("TEST_NDK_VERSION") ?: DEFAULT_NDK_VERSION
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
                targetMachines.add(machines.linux.architecture("aarch64-linux-android21"))
                targetMachines.add(machines.macOS.architecture("aarch64-linux-android21"))
                //targetMachines.add(machines.macOS.x86_64)
                linkage.add(Linkage.SHARED)
            }
        """.trimIndent())
        try {
            val result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("assemble", "--info", "--stacktrace")
                .forwardOutput()
                .build()

            //assertTrue(result.output.contains("Hello world!"))
            assertEquals(TaskOutcome.SUCCESS, result.task(":assemble")?.outcome)
        } catch(e : Throwable) {
            throw(e)
        }
    }
}