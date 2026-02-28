import com.seraphim.plugin.compileSdkVersion
import com.seraphim.plugin.minSdkVersion

plugins {
    alias(libs.plugins.seraphim.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.skie)
}
kotlin {
    androidLibrary {
        namespace = "com.seraphim.core.network"
        compileSdk = project.compileSdkVersion
        minSdk = project.minSdkVersion
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        commonMain {
            dependencies {
                implementation(project(":utils"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.koin.core)
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.slf4j.api)
                implementation(libs.androidx.paging.common)
            }
            kotlin.srcDir("build/openapi/src/main/kotlin")
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.okhttp.logging)

        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}