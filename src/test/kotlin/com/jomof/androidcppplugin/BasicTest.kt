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
        val userHomeTestNdkVersion = File(System.getProperty("user.home")).resolve("test-ndk-version.txt")
        testNdkVersion = if (userHomeTestNdkVersion.isFile) {
            val result = userHomeTestNdkVersion.readText().trim()
            println("TEST-NDK: Using NDK version from $userHomeTestNdkVersion: [$result]")
            result
        } else {
                println("TEST-NDK: Using default NDK version")
                DEFAULT_NDK_VERSION
        }
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
        val dollar = "$"
        buildFile!!.writeText("""
            plugins {
                id("cpp-library")
                id("cpp-android")
            }
            android {
                ndkVersion = "$testNdkVersion"
            }
            library {
//                error("${dollar}{machines.javaClass}")
 //               targetMachines.add(machines.os("aarch64-linux-android21"))
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
                .withArguments("assemble", "--info", "--stacktrace", "--build-cache")
                .forwardOutput()
                .build()

            println("-------------------------")
            GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("clean", "assemble", "--info", "--stacktrace", "--build-cache")
                .forwardOutput()
                .build()

            //assertTrue(result.output.contains("Hello world!"))
            assertEquals(TaskOutcome.SUCCESS, result.task(":assemble")?.outcome)
        } catch(e : Throwable) {
            throw(e)
        }
    }
}