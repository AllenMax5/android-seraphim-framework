plugins {
    alias(libs.plugins.seraphim.android.application)
    alias(libs.plugins.seraphim.android.application.compose)
    alias(libs.plugins.seraphim.android.application.jacoco)
    alias(libs.plugins.seraphim.koin)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.seraphim.app.literacy"
    defaultConfig {
        applicationId = "com.seraphim.literacy"
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
    implementation(project(":shareds:literacy"))
    implementation(project(":core:permissions"))
    implementation(project(":utils"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.compose)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.logback.android)
    implementation(libs.slf4j.api)
    implementation(libs.destinations.core)
    implementation(libs.androidx.palette.ktx)
    ksp(libs.destinations.ksp)
    implementation(libs.destinations.bottom.sheet)
    implementation(libs.process.phoenix)
    implementation(libs.napier.logger)
    implementation(libs.mmkv.kotlin)
    implementation(libs.calendar)
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.kotlinx.datetime)
}
