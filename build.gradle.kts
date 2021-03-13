import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("maven-publish")
    id("java-gradle-plugin")
}

//group = "com.github.jomof"
//version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    testImplementation(gradleTestKit())
    implementation(gradleApi())
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("androidClang") {
            id = "android-cpp-plugin"
            implementationClass = "com.jomof.androidcppplugin.AndroidCppPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.jomof"
            artifactId = "android-cpp-plugin"
            version = project.property("version")?.toString() ?: "0.1-SNAPSHOT"
//            artifact(dokkaJavadocJar)
//            artifact(dokkaHtmlJar)
            from(components["java"])
        }
    }
}