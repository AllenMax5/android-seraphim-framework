import com.seraphim.plugin.compileSdkVersion
import com.seraphim.plugin.minSdkVersion

plugins {
    alias(libs.plugins.seraphim.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.seraphim.nfc.shared"
        compileSdk = project.compileSdkVersion
        minSdk = project.minSdkVersion
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "NfcShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier.logger)
            implementation(libs.koin.core)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspCommonMainMetadata", libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
}

// 让各平台编译包含 KSP 生成的 actual 实现
kotlin {
    sourceSets {
        androidMain {
            kotlin.srcDirs("build/generated/ksp/android/androidMain/kotlin")
        }
        iosMain {
            kotlin.srcDirs(
                "build/generated/ksp/iosArm64/iosArm64Main/kotlin",
                "build/generated/ksp/iosSimulatorArm64/iosSimulatorArm64Main/kotlin",
                "build/generated/ksp/iosX64/iosX64Main/kotlin"
            )
        }
    }
}
