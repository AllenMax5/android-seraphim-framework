import com.seraphim.plugin.compileSdkVersion
import com.seraphim.plugin.minSdkVersion

plugins {
    alias(libs.plugins.seraphim.kotlin.multiplatform.library)
}
kotlin {
    androidLibrary {
        namespace = "com.seraphim.core.storage"
        compileSdk = project.compileSdkVersion
        minSdk = project.minSdkVersion
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        )
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.mmkv.kotlin)
                implementation(libs.kotlinx.coroutines.core)
                api(libs.room.runtime)
                api(libs.sqlite.bundled)
            }
        }
    }
}
