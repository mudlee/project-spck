import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

plugins {
    id("java")
    id("application")
    id("org.beryx.jlink").version("2.23.8")
}

dependencies {
    implementation(project(":core"))
}

val currentOs: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
var spckJvmArgs = listOf("-Dorg.lwjgl.system.allocator=system", "-Dorg.lwjgl.util.DebugLoader=true", "-Dorg.lwjgl.util.Debug=true", "-Dorg.lwjgl.opengl.Display.enableHighDPI=true", "-Dorg.lwjgl.opengl.Display.enableOSXFullscreenModeAPI=true")
if(currentOs.isMacOsX) {
    spckJvmArgs = spckJvmArgs.plus(listOf("-XstartOnFirstThread"))
}

application {
    mainModule.set(moduleName)
    mainClass.set("spck.sandbox.SandboxApplication")
    applicationDefaultJvmArgs = spckJvmArgs

    logger.quiet("[SPCK] [application] module: ${mainModule.get()}")
    logger.quiet("[SPCK] [application] class: ${mainClass.get()}")
    logger.quiet("[SPCK] [application] JVM args: ${spckJvmArgs.joinToString(", ")}")

    // See https://github.com/java9-modularity/gradle-modules-plugin/issues/165
    modularity.disableEffectiveArgumentsAdjustment()
}

jlink {
    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    // https://github.com/beryx-gist/badass-jlink-example-richtextfx/blob/master/build.gradle
    // jvmArgs = ['-splash:$APPDIR/splash.png']

    logger.quiet("[SPCK] [jlink] Options: ${jlink.options.get().joinToString(" ")}")
    logger.quiet("[SPCK] [jlink] JVM args: ${spckJvmArgs.joinToString(", ")}")

    launcher {
        name = "spck-app"
        jvmArgs = spckJvmArgs
    }

    jpackage {
        skipInstaller = true
    }
}
