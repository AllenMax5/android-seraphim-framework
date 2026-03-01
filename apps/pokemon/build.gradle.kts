plugins {
    alias(libs.plugins.seraphim.android.application)
    alias(libs.plugins.seraphim.android.application.compose)
    alias(libs.plugins.seraphim.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seraphim.app.pokemon"
    defaultConfig {
        applicationId = "com.seraphim.pokemon"
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
    implementation(project(":shareds:pokemon"))
    implementation(project(":utils"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.compose)
    implementation(libs.coil.kt.compose)
    implementation(libs.napier.logger)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.android)
    implementation(libs.slf4j.api)
}
