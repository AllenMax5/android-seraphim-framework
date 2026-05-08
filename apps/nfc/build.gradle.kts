plugins {
    alias(libs.plugins.seraphim.android.application)
    alias(libs.plugins.seraphim.android.application.compose)
    alias(libs.plugins.seraphim.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seraphim.app.nfc"
    defaultConfig {
        applicationId = "com.seraphim.nfc"
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgs.add("-Xopt-in=kotlinx.coroutines.FlowPreview")
        freeCompilerArgs.add("-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }
}

dependencies {
    implementation(project(":shareds:nfc"))
    implementation(project(":core:permissions"))
    implementation(project(":utils"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.kt.compose)
    implementation(libs.napier.logger)
    implementation(libs.mmkv.kotlin)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // implementation(libs.androidx.biometric) // TODO: 网络恢复后启用
    // implementation(libs.androidx.security.crypto)
    // implementation(libs.lottie.compose) // TODO: 网络恢复后启用
}
