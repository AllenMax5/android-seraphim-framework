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
            credentials {
                username = providers.gradleProperty("HERE_ACCESS_KEY_ID").orNull ?: ""
                password = providers.gradleProperty("HERE_ACCESS_KEY_SECRET").orNull ?: ""
            }
        }
        maven("https://maven.pkg.github.com/li-lance/android-seraphim-map") {
            credentials {
                username = providers.gradleProperty("GITHUB_PACKAGES_USER").orNull ?: ""
                password = providers.gradleProperty("GITHUB_PACKAGES_TOKEN").orNull ?: ""
            }
        }
        // AMap SDK repository
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repository.innovation.com.cn/repository/maven-public/")
    }
}

rootProject.name = "android-seraphim-framework"
include(":apps:mapdemo")
include(":apps:delicacies")
include(":apps:pokemon")
include(":apps:literacy-eval")
include(":apps:nfc")
include(":utils")
include(":core:permissions", ":core:network", ":core:storage")
include(
    ":core:map:commons",
    ":core:map:map-google",
    ":core:map:map-here",
    ":core:map:map-yandex",
    ":core:map:map-tmap",
    ":core:map:map-amap"
)
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
