plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    explicitApi()

    androidTarget()

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
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.io.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.schema.generator.json)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.schema.generator.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.zxhhyj.atorm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "atorm-core", version.toString())

    pom {
        name = "atorm-core"
        description = "atorm-core"
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