package com.jomof.androidcppplugin

import org.gradle.nativeplatform.TargetMachineBuilder

class NdkTargetMachine(private val host : TargetMachineBuilder) : TargetMachineBuilder by host