import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.maven.publish)
}

kotlin {
    explicitApi()

    android {
        namespace = "com.zxhhyj.atorm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk { version = release(libs.versions.android.compileSdk.get().toInt()) }
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    jvm()

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64()
        hostOs == "Mac OS X" && !isArm64 -> macosX64()
        hostOs == "Linux" && isArm64 -> linuxArm64()
        hostOs == "Linux" && !isArm64 -> linuxX64()
        isMingwX64 -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.atormCore)
            implementation(projects.atormAgent)

            implementation(libs.mcp.kotlin.sdk)

            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "atorm-mcp", version.toString())

    pom {
        name = "atorm-mcp"
        description = "atorm-mcp"
        inceptionYear = "2026"
        url = "https://gitee.com/ZXHHYJ/atorm"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "ZXHHYJ"
                name = "ZXHHYJ"
                url = "https://gitee.com/ZXHHYJ"
            }
        }
        scm {
            url = "https://gitee.com/ZXHHYJ/atorm"
            connection = "scm:git:git://gitee.com:ZXHHYJ/atorm.git"
            developerConnection = "scm:git:ssh://git@gitee.com:ZXHHYJ/atorm.git"
        }
    }
}