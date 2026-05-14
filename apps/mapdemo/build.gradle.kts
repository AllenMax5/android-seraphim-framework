import java.util.Properties

plugins {
    alias(libs.plugins.seraphim.android.application)
}

// Read local.properties for API keys (not tracked by git)
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

fun localProp(key: String): String = localProperties.getProperty(key, "")

android {
    namespace = "com.seraphim.app.mapdemo"
    defaultConfig {
        applicationId = "com.seraphim.mapdemo"
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
        manifestPlaceholders["MAPS_API_KEY"] = localProp("MAPS_API_KEY")
        manifestPlaceholders["AMAP_API_KEY"] = localProp("AMAP_API_KEY")
    }
    flavorDimensions += "provider"
    productFlavors {
        create("amap") { dimension = "provider"; applicationIdSuffix = ".amap" }
        create("google") {
            dimension = "provider"; applicationIdSuffix = ".google"
            buildConfigField(
                "String", "GOOGLE_PLACES_API_KEY",
                "\"${localProp("GOOGLE_PLACES_API_KEY")}\""
            )
        }
        create("tmap") { dimension = "provider"; applicationIdSuffix = ".tmap" }
        create("yandex") { dimension = "provider"; applicationIdSuffix = ".yandex" }
        create("here") {
            dimension = "provider"; applicationIdSuffix = ".here"
            buildConfigField(
                "String", "HERE_ACCESS_KEY_ID",
                "\"${localProp("HERE_ACCESS_KEY_ID")}\""
            )
            buildConfigField(
                "String", "HERE_ACCESS_KEY_SECRET",
                "\"${localProp("HERE_ACCESS_KEY_SECRET")}\""
            )
        }
    }
    buildFeatures {
        buildConfig = true
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
