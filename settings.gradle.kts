rootProject.name = "AtormMultiplatform"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                    includeGroupAndSubgroups("com.android")
                    includeGroupAndSubgroups("com.google")
                }
            }
            mavenCentral()
            gradlePluginPortal()
        }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":compose-example-xiaojing")
include(":openai-client")
include(":atorm-core")
include(":atorm-test")
include(":atorm-agent")
include(":atorm-agent-test")
include(":atorm-mcp")
include(":atorm-mcp-test")
include(":atorm-utils")
include(":atorm-openai")
include(":atorm-openai-test")
include(":atorm-doubao")
include(":atorm-doubao-test")
include(":atorm-alibaba")
include(":atorm-alibaba-test")