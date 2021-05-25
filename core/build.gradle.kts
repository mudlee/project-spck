import org.gradle.nativeplatform.platform.internal.ArchitectureInternal
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

plugins {
    id("java-library")
}

val lwjglVersion = "3.3.0-SNAPSHOT"
val jomlVersion = "1.10.1"
val slf4jVersion = "1.8.0-beta4"
val artemisVersion = "2.3.0"
val resourcesPath = "../resources"

val currentOs: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
val currentArch: ArchitectureInternal = DefaultNativePlatform.getCurrentArchitecture()
logger.quiet("[SPCK] System: ${currentOs.displayName} / ${currentArch.displayName}")

fun specifyLwjglNatives(): String {
    val currentOs = DefaultNativePlatform.getCurrentOperatingSystem()
    val currentArch = DefaultNativePlatform.getCurrentArchitecture()
    when {
        currentOs.isMacOsX -> {
            if(currentArch.displayName.contains("aarch64")) {
                return "natives-macos-arm64"
            }
            return "natives-macos"
        }
        currentOs.isWindows -> {
            if(currentArch.displayName.contains("aarch64")) {
                return "natives-windows-arm64"
            }
            return "natives-windows"
        }
        currentOs.isLinux -> {
            return "natives-linux"
        }
        else -> throw Error("Unrecognized or unsupported Operating system.")
    }
}

val lwjglNatives = specifyLwjglNatives()

logger.quiet("[SPCK] Natives in use: $lwjglNatives")
logger.quiet("[SPCK] Resources path: $resourcesPath")

sourceSets.main.get().resources.srcDirs(resourcesPath)

dependencies {
    api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    api("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-nanovg")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
    implementation("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    api("net.onedaybeard.artemis","artemis-odb", artemisVersion)
    api("org.joml", "joml", jomlVersion)
    implementation("org.slf4j", "slf4j-api", slf4jVersion)
    implementation("org.slf4j", "slf4j-simple", slf4jVersion)
}