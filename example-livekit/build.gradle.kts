import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    android {
        namespace = "com.zxhhyj.atorm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk { version = release(libs.versions.android.compileSdk.get().toInt()) }
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.dashscope.sdk.java)

            implementation(libs.kotlinx.io.core)

            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.backdrop)

            implementation(libs.material)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.zxhhyj.atorm.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.zxhhyj.atorm"
            packageVersion = "1.0.0"
        }
    }
}