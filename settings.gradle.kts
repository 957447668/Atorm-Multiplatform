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

include(":composeApp")
include(":openai-client")
include(":atorm-core")
include(":atorm-core-test")
include(":atorm-agent")
include(":atorm-utils")
include(":atorm-agent-test")
include(":atorm-openai")
include(":atorm-openai-test")
include(":atorm-doubao")
include(":atorm-doubao-test")
include(":atorm-alibaba")
include(":atorm-alibaba-test")