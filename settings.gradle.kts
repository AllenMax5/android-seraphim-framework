@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repo.platform.here.com/artifactory/open-location-platform") {
            // HERE SDK requires authentication.
            // Add these to ~/.gradle/gradle.properties:
            //   HERE_ACCESS_KEY_ID=your_key_id
            //   HERE_ACCESS_KEY_SECRET=your_key_secret
            credentials {
                username = providers.gradleProperty("HERE_ACCESS_KEY_ID").orNull ?: ""
                password = providers.gradleProperty("HERE_ACCESS_KEY_SECRET").orNull ?: ""
            }
        }
    }
}

rootProject.name = "android-seraphim-framework"
// Map modules are managed by repo tool: android-seraphim-map.git → core/map
include(":apps:delicacies")
include(":apps:pokemon")
include(":apps:literacy-eval")
include(":apps:nfc")
include(":utils")
include(":core:permissions", ":core:network", ":core:storage")
include(":shareds:delicacies")
include(":shareds:pokemon")
include(":shareds:literacy")
include(":shareds:nfc")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    Now in Android requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
