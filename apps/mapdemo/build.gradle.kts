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
        manifestPlaceholders["MAPS_API_KEY"] = providers.gradleProperty("MAPS_API_KEY").orElse("")
    }
    flavorDimensions += "provider"
    productFlavors {
        create("amap") { dimension = "provider"; applicationIdSuffix = ".amap" }
        create("google") { dimension = "provider"; applicationIdSuffix = ".google" }
        create("tmap") { dimension = "provider"; applicationIdSuffix = ".tmap" }
        create("yandex") { dimension = "provider"; applicationIdSuffix = ".yandex" }
        create("here") { dimension = "provider"; applicationIdSuffix = ".here" }
    }
    packaging {
        resources { excludes.add("/META-INF/{AL2.0,LGPL2.1}") }
    }
}
dependencies {
    implementation(project(":core:map:commons"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation("com.google.android.material:material:1.12.0")

    add("amapImplementation", project(":core:map:map-amap"))
    add("googleImplementation", project(":core:map:map-google"))
    add("tmapImplementation", project(":core:map:map-tmap"))
    add("yandexImplementation", project(":core:map:map-yandex"))
    add("hereImplementation", project(":core:map:map-here"))
    add("hereImplementation", fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
}
