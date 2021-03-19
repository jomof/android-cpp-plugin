package com.jomof.androidcppplugin.ndk

import java.lang.StringBuilder

// Uniquely identifies a compilation target
// aarch64-linux-android21-ndk23.0.7123448
data class NdkTargetCoordinate(
    val arch : String, // For example, aarch64
    val family : String?, // Operating system family. For example, linux
    val platform : String?, // For example, android21
    val ndk : String? // Version of NDK
) {
    val clangTarget : String get() {
        val sb = StringBuilder()
        sb.append(arch)
        if (family != null) sb.append("-$family")
        if (platform != null) sb.append("-$platform")
        return sb.toString()
    }

    val abi : String get() = when(arch) {
            "aarch64" -> "arm64-v8a"
            "armv7" -> "armeabi-v7a"
            "i686" -> "x86"
            "x86_64" -> "x86_64"
            else -> error(arch)
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(arch)
        if (family != null) sb.append("-$family")
        if (platform != null) sb.append("-$platform")
        if (ndk != null) sb.append("-ndk$ndk")
        return sb.toString()
    }
    companion object {
        private enum class SegmentType(val tryParse : (String) -> String?) {
            ARCH(::tryParseArch),
            FAMILY(::tryParseFamily),
            PLATFORM(::tryParsePlatform),
            NDK(::tryParseNdk)
        }
        private fun tryParseArch(text : String) =
            when(text) {
                "i686",
                "x86_64",
                "armv7",
                "aarch64" -> text
                else -> null
            }
        private fun tryParseFamily(text : String) =
            when(text) {
                "linux" -> "linux"
                else -> null
            }
        private fun tryParsePlatform(text : String) =
            when {
                text.startsWith("android") -> text
                else -> null
            }
        private fun tryParseNdk(text : String) =
            when {
                text.startsWith("ndk") -> text.substring(3)
                else -> null
            }

        fun parse(text : String) : NdkTargetCoordinate {
            val segments = text.split("-")
            val types = SegmentType.values().toList()
            val typeMap = mutableMapOf<SegmentType, String>()
            for(segment in segments) {
                for(type in types) {
                    if (typeMap.keys.contains(type)) continue
                    when(val parsed = type.tryParse(segment)) {
                        null -> { }
                        else -> typeMap[type] = parsed
                    }
                }
            }
            return NdkTargetCoordinate(
                arch = typeMap[SegmentType.ARCH] ?: error("Target '$text' did not have a recognizable architecture"),
                family = typeMap[SegmentType.FAMILY],
                platform = typeMap[SegmentType.PLATFORM],
                ndk = typeMap[SegmentType.NDK]
            )
        }
    }
}

/*
arm64-v8a       aarch64-linux-android21
armeabi-v7a     armv7-linux-androideabi19
x86             i686-linux-android19
x86_64          x86_64-linux-android21
*/