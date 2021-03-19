package com.jomof.androidcppplugin

import com.jomof.androidcppplugin.ndk.NdkTargetCoordinate
import org.junit.Test

class NdkTargetCoordinateTest {
    @Test
    fun basic() {
        NdkTargetCoordinate.parse("aarch64-linux-android21").assertString("aarch64-linux-android21")
        NdkTargetCoordinate.parse("armv7-linux-androideabi19").assertString("armv7-linux-androideabi19")
        NdkTargetCoordinate.parse("i686-linux-android19").assertString("i686-linux-android19")
        NdkTargetCoordinate.parse("x86_64-linux-android21").assertString("x86_64-linux-android21")

        NdkTargetCoordinate.parse("aarch64").assertString("aarch64")
        NdkTargetCoordinate.parse("armv7").assertString("armv7")
        NdkTargetCoordinate.parse("i686").assertString("i686")
        NdkTargetCoordinate.parse("x86_64").assertString("x86_64")

        NdkTargetCoordinate.parse("aarch64-android21").platform.assertString("android21")
        NdkTargetCoordinate.parse("aarch64-ndk1.2.3").ndk.assertString("1.2.3")
    }
}