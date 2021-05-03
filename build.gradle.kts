plugins {
    id("java")
    id("org.javamodularity.moduleplugin").version("1.7.0").apply(false)
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

subprojects {
    apply(plugin = "org.javamodularity.moduleplugin")
    group = "spck"
    version = "1.0.0"

    java {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }
}