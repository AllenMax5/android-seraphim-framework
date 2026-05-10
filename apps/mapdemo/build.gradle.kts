plugins {
    alias(libs.plugins.seraphim.android.application)
}
android {
    namespace = "com.seraphim.app.mapdemo"
    defaultConfig {
        applicationId = "com.seraphim.mapdemo"
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}
dependencies {
    implementation(project(":core:map:commons"))
    implementation(project(":core:map:map-google"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
